package com.tien.lld.filesharing;

import com.tien.lld.filesharing.entities.FileSystemItem;
import com.tien.lld.filesharing.entities.Folder;
import com.tien.lld.filesharing.entities.ShareLink;
import com.tien.lld.filesharing.entities.SharedFile;
import com.tien.lld.filesharing.entities.User;
import com.tien.lld.filesharing.enums.AccessLevel;
import com.tien.lld.filesharing.enums.ItemStatus;
import com.tien.lld.filesharing.exceptions.AccessDeniedException;
import com.tien.lld.filesharing.exceptions.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FileSharingService {
    private final FileStorage storage;
    private final AccessManager accessManager;
    private final LinkSharingService linkSharingService;

    public FileSharingService() {
        this.storage = new FileStorage();
        this.accessManager = new AccessManager(storage);
        this.linkSharingService = new LinkSharingService(storage);
    }

    public User createUser(String name, String email) {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .build();
        storage.saveUser(user);
        return user;
    }

    public Folder createFolder(String ownerId, String parentFolderId, String folderName) {
        User owner = storage.getActiveUser(ownerId);
        validateName(folderName, "folderName");
        validateParentFolderForWrite(ownerId, parentFolderId);

        Folder folder = Folder.builder()
                .id(UUID.randomUUID().toString())
                .name(folderName)
                .owner(owner)
                .parentFolderId(parentFolderId)
                .status(ItemStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        storage.saveItem(folder);
        addChildToParent(parentFolderId, folder.getId());
        return folder;
    }

    public SharedFile uploadFile(String ownerId, String parentFolderId, String fileName, String content) {
        User owner = storage.getActiveUser(ownerId);
        validateName(fileName, "fileName");
        if (content == null) {
            throw new InvalidRequestException("content is required");
        }
        validateParentFolderForWrite(ownerId, parentFolderId);

        SharedFile file = SharedFile.builder()
                .id(UUID.randomUUID().toString())
                .name(fileName)
                .owner(owner)
                .parentFolderId(parentFolderId)
                .status(ItemStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .content(content)
                .build();

        storage.saveItem(file);
        addChildToParent(parentFolderId, file.getId());
        return file;
    }

    public List<FileSystemItem> listFolder(String userId, String folderId) {
        storage.getActiveUser(userId);

        if (folderId == null) {
            return listRootItems(userId);
        }

        Folder folder = storage.getActiveFolder(folderId);
        accessManager.requireAccess(userId, folder, AccessLevel.VIEW);

        List<FileSystemItem> result = new ArrayList<>();
        for (String childId : folder.getChildrenIds()) {
            FileSystemItem child = storage.getItem(childId);
            if (child != null && child.isActive() && accessManager.hasAccess(userId, child, AccessLevel.VIEW)) {
                result.add(child);
            }
        }
        return result;
    }

    public String downloadFile(String userId, String fileId) {
        storage.getActiveUser(userId);
        SharedFile file = storage.getActiveFile(fileId);
        accessManager.requireAccess(userId, file, AccessLevel.VIEW);
        return file.getContent();
    }

    public SharedFile updateFile(String userId, String fileId, String newContent) {
        storage.getActiveUser(userId);
        if (newContent == null) {
            throw new InvalidRequestException("newContent is required");
        }

        SharedFile file = storage.getActiveFile(fileId);
        accessManager.requireAccess(userId, file, AccessLevel.EDIT);
        file.updateContent(newContent);
        return file;
    }

    public void shareItem(String ownerId, String itemId, String targetUserId, AccessLevel accessLevel) {
        storage.getActiveUser(ownerId);
        storage.getActiveUser(targetUserId);
        if (accessLevel == null) {
            throw new InvalidRequestException("accessLevel is required");
        }

        FileSystemItem item = storage.getActiveItem(itemId);
        requireOwner(ownerId, item);
        accessManager.grantAccessRecursively(item, targetUserId, accessLevel);
    }

    public void revokeAccess(String ownerId, String itemId, String targetUserId) {
        storage.getActiveUser(ownerId);
        storage.getActiveUser(targetUserId);

        FileSystemItem item = storage.getActiveItem(itemId);
        requireOwner(ownerId, item);
        accessManager.revokeAccessRecursively(item, targetUserId);
    }

    public ShareLink createShareLink(String ownerId, String fileId, AccessLevel accessLevel) {
        storage.getActiveUser(ownerId);
        SharedFile file = storage.getActiveFile(fileId);
        requireOwner(ownerId, file);
        return linkSharingService.createShareLink(file, accessLevel);
    }

    public String downloadByLink(String linkId) {
        return linkSharingService.downloadByLink(linkId);
    }

    public void deactivateShareLink(String ownerId, String linkId) {
        storage.getActiveUser(ownerId);
        linkSharingService.deactivateShareLink(ownerId, linkId);
    }

    public void deleteItem(String ownerId, String itemId) {
        storage.getActiveUser(ownerId);
        FileSystemItem item = storage.getActiveItem(itemId);
        requireOwner(ownerId, item);
        deleteRecursively(item);
    }

    private List<FileSystemItem> listRootItems(String userId) {
        List<FileSystemItem> result = new ArrayList<>();
        for (FileSystemItem item : storage.getAllItems()) {
            if (item.isActive()
                    && item.getParentFolderId() == null
                    && accessManager.hasAccess(userId, item, AccessLevel.VIEW)) {
                result.add(item);
            }
        }
        return result;
    }

    private void validateParentFolderForWrite(String ownerId, String parentFolderId) {
        if (parentFolderId == null) {
            return;
        }
        Folder parent = storage.getActiveFolder(parentFolderId);
        requireOwner(ownerId, parent);
    }

    private void addChildToParent(String parentFolderId, String childId) {
        if (parentFolderId == null) {
            return;
        }
        storage.getActiveFolder(parentFolderId).addChild(childId);
    }

    private void deleteRecursively(FileSystemItem item) {
        if (item instanceof Folder folder) {
            for (String childId : folder.getChildrenIds()) {
                FileSystemItem child = storage.getItem(childId);
                if (child != null && child.isActive()) {
                    deleteRecursively(child);
                }
            }
        }
        item.markDeleted();
    }

    private void requireOwner(String userId, FileSystemItem item) {
        if (!item.isOwnedBy(userId)) {
            throw new AccessDeniedException("only owner can perform this operation on item: " + item.getId());
        }
    }

    private void validateName(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidRequestException(fieldName + " is required");
        }
    }
}


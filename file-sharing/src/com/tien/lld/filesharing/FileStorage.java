package com.tien.lld.filesharing;

import com.tien.lld.filesharing.entities.FilePermission;
import com.tien.lld.filesharing.entities.FileSystemItem;
import com.tien.lld.filesharing.entities.Folder;
import com.tien.lld.filesharing.entities.ShareLink;
import com.tien.lld.filesharing.entities.SharedFile;
import com.tien.lld.filesharing.entities.User;
import com.tien.lld.filesharing.exceptions.InvalidRequestException;
import com.tien.lld.filesharing.exceptions.ItemNotFoundException;
import com.tien.lld.filesharing.exceptions.UserNotFoundException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FileStorage {
    private final Map<String, User> usersById = new LinkedHashMap<>();
    private final Map<String, FileSystemItem> itemsById = new LinkedHashMap<>();
    private final Map<String, FilePermission> permissionsByKey = new LinkedHashMap<>();
    private final Map<String, ShareLink> shareLinksById = new LinkedHashMap<>();

    public void saveUser(User user) {
        usersById.put(user.getId(), user);
    }

    public User getActiveUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidRequestException("userId is required");
        }

        User user = usersById.get(userId);
        if (user == null) {
            throw new UserNotFoundException("user not found: " + userId);
        }
        return user;
    }

    public void saveItem(FileSystemItem item) {
        itemsById.put(item.getId(), item);
    }

    public FileSystemItem getItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            return null;
        }
        return itemsById.get(itemId);
    }

    public FileSystemItem getActiveItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new InvalidRequestException("itemId is required");
        }

        FileSystemItem item = itemsById.get(itemId);
        if (item == null || !item.isActive()) {
            throw new ItemNotFoundException("active item not found: " + itemId);
        }
        return item;
    }

    public Folder getActiveFolder(String folderId) {
        FileSystemItem item = getActiveItem(folderId);
        if (!(item instanceof Folder folder)) {
            throw new InvalidRequestException("item is not a folder: " + folderId);
        }
        return folder;
    }

    public SharedFile getActiveFile(String fileId) {
        FileSystemItem item = getActiveItem(fileId);
        if (!(item instanceof SharedFile file)) {
            throw new InvalidRequestException("item is not a file: " + fileId);
        }
        return file;
    }

    public Collection<FileSystemItem> getAllItems() {
        return itemsById.values();
    }

    public void savePermission(FilePermission permission) {
        permissionsByKey.put(permissionKey(permission.getItemId(), permission.getUserId()), permission);
    }

    public FilePermission getPermission(String itemId, String userId) {
        return permissionsByKey.get(permissionKey(itemId, userId));
    }

    public void removePermission(String itemId, String userId) {
        permissionsByKey.remove(permissionKey(itemId, userId));
    }

    public void saveShareLink(ShareLink shareLink) {
        shareLinksById.put(shareLink.getId(), shareLink);
    }

    public ShareLink getActiveShareLink(String linkId) {
        if (linkId == null || linkId.trim().isEmpty()) {
            throw new InvalidRequestException("linkId is required");
        }

        ShareLink link = shareLinksById.get(linkId);
        if (link == null || !link.isActive()) {
            throw new ItemNotFoundException("active share link not found: " + linkId);
        }
        return link;
    }

    private String permissionKey(String itemId, String userId) {
        return itemId + ":" + userId;
    }
}


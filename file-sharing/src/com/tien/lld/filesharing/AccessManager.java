package com.tien.lld.filesharing;

import com.tien.lld.filesharing.entities.FilePermission;
import com.tien.lld.filesharing.entities.FileSystemItem;
import com.tien.lld.filesharing.entities.Folder;
import com.tien.lld.filesharing.enums.AccessLevel;
import com.tien.lld.filesharing.exceptions.AccessDeniedException;

public final class AccessManager {
    private final FileStorage storage;

    public AccessManager(FileStorage storage) {
        this.storage = storage;
    }

    public boolean hasAccess(String userId, FileSystemItem item, AccessLevel requiredAccess) {
        if (item == null || !item.isActive() || requiredAccess == null) {
            return false;
        }
        if (item.isOwnedBy(userId)) {
            return true;
        }

        FileSystemItem current = item;
        while (current != null) {
            FilePermission permission = storage.getPermission(current.getId(), userId);
            if (permission != null && permission.getAccessLevel().allows(requiredAccess)) {
                return true;
            }

            String parentId = current.getParentFolderId();
            current = parentId == null ? null : storage.getItem(parentId);
        }

        return false;
    }

    public void requireAccess(String userId, FileSystemItem item, AccessLevel requiredAccess) {
        if (!hasAccess(userId, item, requiredAccess)) {
            throw new AccessDeniedException(
                    "user " + userId + " does not have " + requiredAccess + " access to item: " + item.getId()
            );
        }
    }

    public void grantAccessRecursively(FileSystemItem item, String targetUserId, AccessLevel accessLevel) {
        storage.savePermission(new FilePermission(item.getId(), targetUserId, accessLevel));

        if (item instanceof Folder folder) {
            for (String childId : folder.getChildrenIds()) {
                FileSystemItem child = storage.getItem(childId);
                if (child != null && child.isActive()) {
                    grantAccessRecursively(child, targetUserId, accessLevel);
                }
            }
        }
    }

    public void revokeAccessRecursively(FileSystemItem item, String targetUserId) {
        storage.removePermission(item.getId(), targetUserId);

        if (item instanceof Folder folder) {
            for (String childId : folder.getChildrenIds()) {
                FileSystemItem child = storage.getItem(childId);
                if (child != null) {
                    revokeAccessRecursively(child, targetUserId);
                }
            }
        }
    }
}


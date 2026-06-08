package com.tien.lld.filesharing.entities;

import com.tien.lld.filesharing.enums.AccessLevel;

public final class FilePermission {
    private final String itemId;
    private final String userId;
    private final AccessLevel accessLevel;

    public FilePermission(String itemId, String userId, AccessLevel accessLevel) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("permission itemId is required");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("permission userId is required");
        }
        if (accessLevel == null) {
            throw new IllegalArgumentException("permission accessLevel is required");
        }
        this.itemId = itemId;
        this.userId = userId;
        this.accessLevel = accessLevel;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}


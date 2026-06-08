package com.tien.lld.filesharing.entities;

import com.tien.lld.filesharing.enums.ItemStatus;

import java.time.LocalDateTime;

public abstract class FileSystemItem {
    private final String id;
    private final String name;
    private final User owner;
    private final String parentFolderId;
    private final LocalDateTime createdAt;
    private ItemStatus status;

    protected FileSystemItem(
            String id,
            String name,
            User owner,
            String parentFolderId,
            ItemStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.parentFolderId = parentFolderId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == ItemStatus.ACTIVE;
    }

    public boolean isOwnedBy(String userId) {
        return owner != null && owner.getId().equals(userId);
    }

    public void markDeleted() {
        this.status = ItemStatus.DELETED;
    }
}


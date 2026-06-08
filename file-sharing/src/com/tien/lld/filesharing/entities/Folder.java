package com.tien.lld.filesharing.entities;

import com.tien.lld.filesharing.enums.ItemStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Folder extends FileSystemItem {
    private final List<String> childrenIds = new ArrayList<>();

    private Folder(Builder builder) {
        super(
                builder.id,
                builder.name,
                builder.owner,
                builder.parentFolderId,
                builder.status,
                builder.createdAt
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public void addChild(String childId) {
        if (childId != null && !childrenIds.contains(childId)) {
            childrenIds.add(childId);
        }
    }

    public List<String> getChildrenIds() {
        return Collections.unmodifiableList(childrenIds);
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", owner=" + getOwner().getName() +
                ", parentFolderId='" + getParentFolderId() + '\'' +
                ", status=" + getStatus() +
                ", childrenIds=" + childrenIds +
                '}';
    }

    public static final class Builder {
        private String id;
        private String name;
        private User owner;
        private String parentFolderId;
        private ItemStatus status;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public Builder parentFolderId(String parentFolderId) {
            this.parentFolderId = parentFolderId;
            return this;
        }

        public Builder status(ItemStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Folder build() {
            validate();
            return new Folder(this);
        }

        private void validate() {
            requireText(id, "folder id");
            requireText(name, "folder name");
            if (owner == null) {
                throw new IllegalArgumentException("folder owner is required");
            }
            if (status == null) {
                throw new IllegalArgumentException("folder status is required");
            }
            if (createdAt == null) {
                throw new IllegalArgumentException("folder createdAt is required");
            }
        }

        private void requireText(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
        }
    }
}


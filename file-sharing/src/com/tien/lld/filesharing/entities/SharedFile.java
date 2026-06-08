package com.tien.lld.filesharing.entities;

import com.tien.lld.filesharing.enums.ItemStatus;

import java.time.LocalDateTime;

public final class SharedFile extends FileSystemItem {
    private String content;
    private long size;

    private SharedFile(Builder builder) {
        super(
                builder.id,
                builder.name,
                builder.owner,
                builder.parentFolderId,
                builder.status,
                builder.createdAt
        );
        this.content = builder.content;
        this.size = builder.content.length();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getContent() {
        return content;
    }

    public long getSize() {
        return size;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.size = newContent.length();
    }

    @Override
    public String toString() {
        return "SharedFile{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", owner=" + getOwner().getName() +
                ", parentFolderId='" + getParentFolderId() + '\'' +
                ", status=" + getStatus() +
                ", size=" + size +
                '}';
    }

    public static final class Builder {
        private String id;
        private String name;
        private User owner;
        private String parentFolderId;
        private ItemStatus status;
        private LocalDateTime createdAt;
        private String content;

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

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SharedFile build() {
            validate();
            return new SharedFile(this);
        }

        private void validate() {
            requireText(id, "file id");
            requireText(name, "file name");
            if (owner == null) {
                throw new IllegalArgumentException("file owner is required");
            }
            if (status == null) {
                throw new IllegalArgumentException("file status is required");
            }
            if (createdAt == null) {
                throw new IllegalArgumentException("file createdAt is required");
            }
            if (content == null) {
                throw new IllegalArgumentException("file content is required");
            }
        }

        private void requireText(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
        }
    }
}


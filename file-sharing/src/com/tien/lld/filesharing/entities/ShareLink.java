package com.tien.lld.filesharing.entities;

import com.tien.lld.filesharing.enums.AccessLevel;

import java.time.LocalDateTime;

public final class ShareLink {
    private final String id;
    private final String fileId;
    private final AccessLevel accessLevel;
    private final LocalDateTime createdAt;
    private boolean active;

    private ShareLink(Builder builder) {
        this.id = builder.id;
        this.fileId = builder.fileId;
        this.accessLevel = builder.accessLevel;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getFileId() {
        return fileId;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "ShareLink{" +
                "id='" + id + '\'' +
                ", fileId='" + fileId + '\'' +
                ", accessLevel=" + accessLevel +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }

    public static final class Builder {
        private String id;
        private String fileId;
        private AccessLevel accessLevel;
        private boolean active;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder fileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder accessLevel(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShareLink build() {
            requireText(id, "share link id");
            requireText(fileId, "share link fileId");
            if (accessLevel == null) {
                throw new IllegalArgumentException("share link accessLevel is required");
            }
            if (createdAt == null) {
                throw new IllegalArgumentException("share link createdAt is required");
            }
            return new ShareLink(this);
        }

        private void requireText(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
        }
    }
}


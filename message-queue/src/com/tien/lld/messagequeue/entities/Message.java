package com.tien.lld.messagequeue.entities;

import java.time.LocalDateTime;

public final class Message {
    private final String id;
    private final String topicName;
    private final int partitionId;
    private final long offset;
    private final String key;
    private final String payload;
    private final String publisherId;
    private final LocalDateTime createdAt;

    private Message(Builder builder) {
        this.id = builder.id;
        this.topicName = builder.topicName;
        this.partitionId = builder.partitionId;
        this.offset = builder.offset;
        this.key = builder.key;
        this.payload = builder.payload;
        this.publisherId = builder.publisherId;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public long getOffset() {
        return offset;
    }

    public String getKey() {
        return key;
    }

    public String getPayload() {
        return payload;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topicName + '\'' +
                ", partitionId=" + partitionId +
                ", offset=" + offset +
                ", key='" + key + '\'' +
                ", payload='" + payload + '\'' +
                ", publisherId='" + publisherId + '\'' +
                '}';
    }

    public static final class Builder {
        private String id;
        private String topicName;
        private int partitionId;
        private long offset;
        private String key;
        private String payload;
        private String publisherId;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Builder partitionId(int partitionId) {
            this.partitionId = partitionId;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder publisherId(String publisherId) {
            this.publisherId = publisherId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Message build() {
            validateText(id, "message id");
            validateText(topicName, "message topicName");
            if (partitionId < 0) {
                throw new IllegalArgumentException("message partitionId cannot be negative");
            }
            if (offset < 0) {
                throw new IllegalArgumentException("message offset cannot be negative");
            }
            validateText(payload, "message payload");
            validateText(publisherId, "message publisherId");
            if (createdAt == null) {
                throw new IllegalArgumentException("message createdAt is required");
            }
            return new Message(this);
        }

        private void validateText(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required");
            }
        }
    }
}


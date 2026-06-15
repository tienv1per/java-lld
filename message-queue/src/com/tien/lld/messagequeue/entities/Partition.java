package com.tien.lld.messagequeue.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class Partition {
    private final int id;
    private final List<Message> messages = new ArrayList<>();
    private long nextOffset;

    public Partition(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("partition id cannot be negative");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized Message appendMessage(
            String topicName,
            String key,
            String payload,
            String publisherId
    ) {
        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .topicName(topicName)
                .partitionId(id)
                .offset(nextOffset)
                .key(key)
                .payload(payload)
                .publisherId(publisherId)
                .createdAt(LocalDateTime.now())
                .build();
        messages.add(message);
        nextOffset++;
        return message;
    }

    public synchronized List<Message> getMessagesSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    @Override
    public synchronized String toString() {
        return "Partition{" +
                "id=" + id +
                ", nextOffset=" + nextOffset +
                ", messages=" + messages +
                '}';
    }
}

package com.tien.lld.messagequeue.subscriber;

import com.tien.lld.messagequeue.entities.Message;

public final class LoggingSubscriber implements Subscriber {
    private final String id;

    public LoggingSubscriber(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("subscriber id is required");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void consume(Message message) {
        System.out.println("[LOG][" + id + "] message received with payload=" + message.getPayload()
                + ", partition=" + message.getPartitionId()
                + ", offset=" + message.getOffset());
    }
}


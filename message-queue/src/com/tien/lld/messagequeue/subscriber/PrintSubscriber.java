package com.tien.lld.messagequeue.subscriber;

import com.tien.lld.messagequeue.entities.Message;

public final class PrintSubscriber implements Subscriber {
    private final String id;

    public PrintSubscriber(String id) {
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
        System.out.println("[PRINT][" + id + "] consumed " + message);
    }
}


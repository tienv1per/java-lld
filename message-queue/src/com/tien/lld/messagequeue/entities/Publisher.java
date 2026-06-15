package com.tien.lld.messagequeue.entities;

public final class Publisher {
    private final String id;

    public Publisher(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("publisher id is required");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "id='" + id + '\'' +
                '}';
    }
}


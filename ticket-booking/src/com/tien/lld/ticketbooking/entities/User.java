package com.tien.lld.ticketbooking.entities;

public final class User {
    private final String id;
    private final String name;

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static final class Builder {
        private String id;
        private String name;

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

        public User build() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("user id is required");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("user name is required");
            }
            return new User(this);
        }
    }
}


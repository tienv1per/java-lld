package com.tien.lld.cachesystem;

import com.tien.lld.cachesystem.exceptions.InvalidCacheConfigException;

public final class CacheConfig {
    private final int capacity;
    private final long ttlMillis;

    private CacheConfig(Builder builder) {
        this.capacity = builder.capacity;
        this.ttlMillis = builder.ttlMillis;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getCapacity() {
        return capacity;
    }

    public long getTtlMillis() {
        return ttlMillis;
    }

    public static final class Builder {
        private int capacity;
        private long ttlMillis;

        private Builder() {
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder ttlMillis(long ttlMillis) {
            this.ttlMillis = ttlMillis;
            return this;
        }

        public CacheConfig build() {
            if (capacity <= 0) {
                throw new InvalidCacheConfigException("capacity must be positive");
            }
            if (ttlMillis <= 0) {
                throw new InvalidCacheConfigException("ttlMillis must be positive");
            }
            return new CacheConfig(this);
        }
    }
}


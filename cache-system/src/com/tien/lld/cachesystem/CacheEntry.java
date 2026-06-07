package com.tien.lld.cachesystem;

public final class CacheEntry<V> {
    private final V value;
    private final long createdAtMillis;
    private final long expiresAtMillis;

    public CacheEntry(V value, long createdAtMillis, long ttlMillis) {
        this.value = value;
        this.createdAtMillis = createdAtMillis;
        this.expiresAtMillis = createdAtMillis + ttlMillis;
    }

    public V getValue() {
        return value;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public boolean isExpired(long nowMillis) {
        return nowMillis >= expiresAtMillis;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "value=" + value +
                ", createdAtMillis=" + createdAtMillis +
                ", expiresAtMillis=" + expiresAtMillis +
                '}';
    }
}


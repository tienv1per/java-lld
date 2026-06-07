package com.tien.lld.cachesystem;

public final class CacheStats {
    private final long hitCount;
    private final long missCount;
    private final long evictionCount;
    private final long expirationCount;

    public CacheStats(long hitCount, long missCount, long evictionCount, long expirationCount) {
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.evictionCount = evictionCount;
        this.expirationCount = expirationCount;
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

    public long getExpirationCount() {
        return expirationCount;
    }

    @Override
    public String toString() {
        return "CacheStats{" +
                "hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", evictionCount=" + evictionCount +
                ", expirationCount=" + expirationCount +
                '}';
    }
}


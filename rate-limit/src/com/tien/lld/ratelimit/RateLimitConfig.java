package com.tien.lld.ratelimit;

public final class RateLimitConfig {
    private final RateLimitAlgorithm algorithm;
    private final int maxRequests;
    private final long windowMillis;
    private final int bucketCapacity;
    private final int refillTokens;
    private final long refillIntervalMillis;

    private RateLimitConfig(
            RateLimitAlgorithm algorithm,
            int maxRequests,
            long windowMillis,
            int bucketCapacity,
            int refillTokens,
            long refillIntervalMillis
    ) {
        this.algorithm = algorithm;
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.bucketCapacity = bucketCapacity;
        this.refillTokens = refillTokens;
        this.refillIntervalMillis = refillIntervalMillis;
    }

    public static RateLimitConfig fixedWindow(int maxRequests, long windowMillis) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        return new RateLimitConfig(
                RateLimitAlgorithm.FIXED_WINDOW,
                maxRequests,
                windowMillis,
                0,
                0,
                0
        );
    }

    public static RateLimitConfig tokenBucket(int bucketCapacity, int refillTokens, long refillIntervalMillis) {
        if (bucketCapacity <= 0) {
            throw new IllegalArgumentException("bucketCapacity must be positive");
        }
        if (refillTokens <= 0) {
            throw new IllegalArgumentException("refillTokens must be positive");
        }
        if (refillIntervalMillis <= 0) {
            throw new IllegalArgumentException("refillIntervalMillis must be positive");
        }
        return new RateLimitConfig(
                RateLimitAlgorithm.TOKEN_BUCKET,
                0,
                0,
                bucketCapacity,
                refillTokens,
                refillIntervalMillis
        );
    }

    public RateLimitAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public long getWindowMillis() {
        return windowMillis;
    }

    public int getBucketCapacity() {
        return bucketCapacity;
    }

    public int getRefillTokens() {
        return refillTokens;
    }

    public long getRefillIntervalMillis() {
        return refillIntervalMillis;
    }
}


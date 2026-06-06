package com.tien.lld.ratelimit.store;

public final class TokenBucketState {
    private int availableTokens;
    private long lastRefillMillis;

    public TokenBucketState(int availableTokens, long lastRefillMillis) {
        this.availableTokens = availableTokens;
        this.lastRefillMillis = lastRefillMillis;
    }

    public int getAvailableTokens() {
        return availableTokens;
    }

    public long getLastRefillMillis() {
        return lastRefillMillis;
    }

    public void refill(int tokens, int capacity, long refillMillis) {
        long updatedTokens = (long) this.availableTokens + tokens;
        this.availableTokens = (int) Math.min(capacity, updatedTokens);
        this.lastRefillMillis = refillMillis;
    }

    public void consumeOneToken() {
        this.availableTokens--;
    }
}


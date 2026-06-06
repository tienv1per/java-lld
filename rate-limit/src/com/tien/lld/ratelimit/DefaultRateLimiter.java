package com.tien.lld.ratelimit;

import com.tien.lld.ratelimit.strategy.RateLimitStrategy;

public final class DefaultRateLimiter implements RateLimiter {
    private final RateLimitStrategy strategy;

    public DefaultRateLimiter(RateLimitStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy is required");
        }
        this.strategy = strategy;
    }

    @Override
    public RateLimitResult allowRequest(String clientId) {
        validateClientId(clientId);
        return strategy.allow(clientId);
    }

    private void validateClientId(String clientId) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("clientId is required");
        }
    }
}


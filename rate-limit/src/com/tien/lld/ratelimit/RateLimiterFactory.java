package com.tien.lld.ratelimit;

import com.tien.lld.ratelimit.store.InMemoryRateLimitStore;
import com.tien.lld.ratelimit.strategy.FixedWindowRateLimitStrategy;
import com.tien.lld.ratelimit.strategy.RateLimitStrategy;
import com.tien.lld.ratelimit.strategy.TokenBucketRateLimitStrategy;

public final class RateLimiterFactory {
    private RateLimiterFactory() {
    }

    public static RateLimiter create(RateLimitConfig config) {
        return create(config, new InMemoryRateLimitStore());
    }

    public static RateLimiter create(RateLimitConfig config, InMemoryRateLimitStore store) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }
        if (store == null) {
            throw new IllegalArgumentException("store is required");
        }
        return new DefaultRateLimiter(createStrategy(config, store));
    }

    private static RateLimitStrategy createStrategy(RateLimitConfig config, InMemoryRateLimitStore store) {
        if (config.getAlgorithm() == RateLimitAlgorithm.FIXED_WINDOW) {
            return new FixedWindowRateLimitStrategy(config, store);
        }
        if (config.getAlgorithm() == RateLimitAlgorithm.TOKEN_BUCKET) {
            return new TokenBucketRateLimitStrategy(config, store);
        }
        throw new IllegalArgumentException("Unsupported algorithm: " + config.getAlgorithm());
    }
}


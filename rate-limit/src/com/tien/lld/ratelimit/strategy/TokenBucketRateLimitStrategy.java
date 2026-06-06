package com.tien.lld.ratelimit.strategy;

import com.tien.lld.ratelimit.RateLimitAlgorithm;
import com.tien.lld.ratelimit.RateLimitConfig;
import com.tien.lld.ratelimit.RateLimitResult;
import com.tien.lld.ratelimit.store.InMemoryRateLimitStore;
import com.tien.lld.ratelimit.store.TokenBucketState;

import java.util.concurrent.atomic.AtomicReference;

public final class TokenBucketRateLimitStrategy implements RateLimitStrategy {
    private final RateLimitConfig config;
    private final InMemoryRateLimitStore store;

    public TokenBucketRateLimitStrategy(RateLimitConfig config, InMemoryRateLimitStore store) {
        this.config = config;
        this.store = store;
    }

    @Override
    public RateLimitResult allow(String clientId) {
        final long nowMillis = System.currentTimeMillis();
        final AtomicReference<RateLimitResult> resultRef = new AtomicReference<>();

        store.computeTokenBucketState(clientId, (key, currentState) -> {
            TokenBucketState state = currentState;
            if (state == null) {
                state = new TokenBucketState(config.getBucketCapacity(), nowMillis);
            }

            refillIfNeeded(state, nowMillis);

            if (state.getAvailableTokens() > 0) {
                state.consumeOneToken();
                resultRef.set(RateLimitResult.allowed(
                        clientId,
                        RateLimitAlgorithm.TOKEN_BUCKET,
                        state.getAvailableTokens(),
                        "Request allowed"
                ));
                return state;
            }

            resultRef.set(RateLimitResult.denied(
                    clientId,
                    RateLimitAlgorithm.TOKEN_BUCKET,
                    0,
                    calculateRetryAfterMillis(state, nowMillis),
                    "Token bucket is empty"
            ));
            return state;
        });

        return resultRef.get();
    }

    private void refillIfNeeded(TokenBucketState state, long nowMillis) {
        long elapsedMillis = nowMillis - state.getLastRefillMillis();
        long completedIntervals = elapsedMillis / config.getRefillIntervalMillis();

        if (completedIntervals <= 0) {
            return;
        }

        long tokensToAdd = completedIntervals * config.getRefillTokens();
        long refillMillis = state.getLastRefillMillis()
                + completedIntervals * config.getRefillIntervalMillis();

        state.refill(safeTokenCount(tokensToAdd), config.getBucketCapacity(), refillMillis);
    }

    private int safeTokenCount(long tokensToAdd) {
        if (tokensToAdd > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) tokensToAdd;
    }

    private long calculateRetryAfterMillis(TokenBucketState state, long nowMillis) {
        long elapsedSinceLastRefill = nowMillis - state.getLastRefillMillis();
        long retryAfterMillis = config.getRefillIntervalMillis() - elapsedSinceLastRefill;
        return Math.max(1L, retryAfterMillis);
    }
}


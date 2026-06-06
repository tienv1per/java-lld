package com.tien.lld.ratelimit.strategy;

import com.tien.lld.ratelimit.RateLimitAlgorithm;
import com.tien.lld.ratelimit.RateLimitConfig;
import com.tien.lld.ratelimit.RateLimitResult;
import com.tien.lld.ratelimit.store.FixedWindowState;
import com.tien.lld.ratelimit.store.InMemoryRateLimitStore;

import java.util.concurrent.atomic.AtomicReference;

public final class FixedWindowRateLimitStrategy implements RateLimitStrategy {
    private final RateLimitConfig config;
    private final InMemoryRateLimitStore store;

    public FixedWindowRateLimitStrategy(RateLimitConfig config, InMemoryRateLimitStore store) {
        this.config = config;
        this.store = store;
    }

    @Override
    public RateLimitResult allow(String clientId) {
        final long nowMillis = System.currentTimeMillis();
        final AtomicReference<RateLimitResult> resultRef = new AtomicReference<>();

        store.computeFixedWindowState(clientId, (key, currentState) -> {
            FixedWindowState state = currentState;
            if (state == null) {
                state = new FixedWindowState(nowMillis, 0);
            }

            if (isWindowExpired(state, nowMillis)) {
                state.reset(nowMillis);
            }

            if (state.getRequestCount() < config.getMaxRequests()) {
                state.incrementRequestCount();
                int remaining = config.getMaxRequests() - state.getRequestCount();
                resultRef.set(RateLimitResult.allowed(
                        clientId,
                        RateLimitAlgorithm.FIXED_WINDOW,
                        remaining,
                        "Request allowed"
                ));
                return state;
            }

            resultRef.set(RateLimitResult.denied(
                    clientId,
                    RateLimitAlgorithm.FIXED_WINDOW,
                    0,
                    calculateRetryAfterMillis(state, nowMillis),
                    "Fixed window limit exceeded"
            ));
            return state;
        });

        return resultRef.get();
    }

    private boolean isWindowExpired(FixedWindowState state, long nowMillis) {
        return nowMillis - state.getWindowStartMillis() >= config.getWindowMillis();
    }

    private long calculateRetryAfterMillis(FixedWindowState state, long nowMillis) {
        long nextWindowStartMillis = state.getWindowStartMillis() + config.getWindowMillis();
        return Math.max(0L, nextWindowStartMillis - nowMillis);
    }
}


package com.tien.lld.ratelimit.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public final class InMemoryRateLimitStore {
    private final ConcurrentHashMap<String, FixedWindowState> fixedWindowStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucketState> tokenBucketStates = new ConcurrentHashMap<>();

    public FixedWindowState computeFixedWindowState(
            String clientId,
            BiFunction<String, FixedWindowState, FixedWindowState> remappingFunction
    ) {
        return fixedWindowStates.compute(clientId, remappingFunction);
    }

    public TokenBucketState computeTokenBucketState(
            String clientId,
            BiFunction<String, TokenBucketState, TokenBucketState> remappingFunction
    ) {
        return tokenBucketStates.compute(clientId, remappingFunction);
    }
}


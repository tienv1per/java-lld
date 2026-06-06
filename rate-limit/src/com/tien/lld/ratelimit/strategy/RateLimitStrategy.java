package com.tien.lld.ratelimit.strategy;

import com.tien.lld.ratelimit.RateLimitResult;

public interface RateLimitStrategy {
    RateLimitResult allow(String clientId);
}


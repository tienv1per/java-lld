package com.tien.lld.ratelimit;

public interface RateLimiter {
    RateLimitResult allowRequest(String clientId);
}


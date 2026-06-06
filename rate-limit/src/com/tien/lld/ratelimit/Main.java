package com.tien.lld.ratelimit;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        testFixedWindow();
        System.out.println();
        testTokenBucket();
    }

    private static void testFixedWindow() throws InterruptedException {
        System.out.println("=== Fixed Window: max 3 requests per 2 seconds ===");

        RateLimiter rateLimiter = RateLimiterFactory.create(
                RateLimitConfig.fixedWindow(3, 2_000L)
        );

        print(rateLimiter.allowRequest("client-1"));
        print(rateLimiter.allowRequest("client-1"));
        print(rateLimiter.allowRequest("client-1"));
        print(rateLimiter.allowRequest("client-1"));

        print(rateLimiter.allowRequest("client-2"));

        Thread.sleep(2_100L);
        print(rateLimiter.allowRequest("client-1"));
    }

    private static void testTokenBucket() throws InterruptedException {
        System.out.println("=== Token Bucket: capacity 2, refill 1 token per second ===");

        RateLimiter rateLimiter = RateLimiterFactory.create(
                RateLimitConfig.tokenBucket(2, 1, 1_000L)
        );

        print(rateLimiter.allowRequest("client-3"));
        print(rateLimiter.allowRequest("client-3"));
        print(rateLimiter.allowRequest("client-3"));

        Thread.sleep(1_100L);
        print(rateLimiter.allowRequest("client-3"));
    }

    private static void print(RateLimitResult result) {
        System.out.printf(
                "client=%s algorithm=%s allowed=%s remaining=%d retryAfterMillis=%d message=%s%n",
                result.getClientId(),
                result.getAlgorithm(),
                result.isAllowed(),
                result.getRemaining(),
                result.getRetryAfterMillis(),
                result.getMessage()
        );
    }
}


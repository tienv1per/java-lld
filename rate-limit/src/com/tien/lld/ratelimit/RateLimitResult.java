package com.tien.lld.ratelimit;

public final class RateLimitResult {
    private final String clientId;
    private final RateLimitAlgorithm algorithm;
    private final boolean allowed;
    private final int remaining;
    private final long retryAfterMillis;
    private final String message;

    private RateLimitResult(
            String clientId,
            RateLimitAlgorithm algorithm,
            boolean allowed,
            int remaining,
            long retryAfterMillis,
            String message
    ) {
        this.clientId = clientId;
        this.algorithm = algorithm;
        this.allowed = allowed;
        this.remaining = remaining;
        this.retryAfterMillis = retryAfterMillis;
        this.message = message;
    }

    public static RateLimitResult allowed(
            String clientId,
            RateLimitAlgorithm algorithm,
            int remaining,
            String message
    ) {
        return new RateLimitResult(clientId, algorithm, true, remaining, 0L, message);
    }

    public static RateLimitResult denied(
            String clientId,
            RateLimitAlgorithm algorithm,
            int remaining,
            long retryAfterMillis,
            String message
    ) {
        return new RateLimitResult(clientId, algorithm, false, remaining, retryAfterMillis, message);
    }

    public String getClientId() {
        return clientId;
    }

    public RateLimitAlgorithm getAlgorithm() {
        return algorithm;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemaining() {
        return remaining;
    }

    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RateLimitResult{" +
                "clientId='" + clientId + '\'' +
                ", algorithm=" + algorithm +
                ", allowed=" + allowed +
                ", remaining=" + remaining +
                ", retryAfterMillis=" + retryAfterMillis +
                ", message='" + message + '\'' +
                '}';
    }
}


# Rate Limiter

Plain Java implementation for a Low Level Design interview exercise.

## Scope

- In-memory only.
- One API, keyed by `clientId`.
- Algorithm selected by `RateLimitConfig`.
- Supported algorithms:
  - Fixed Window Counter
  - Token Bucket
- Same public method for every algorithm: `allowRequest(clientId)`.
- Thread-safe per client through `ConcurrentHashMap.compute`.

## Design

This project intentionally keeps the structure simple, similar to common LLD interview solutions:

```text
com.tien.lld.ratelimit
├── Main.java
├── RateLimiter.java
├── DefaultRateLimiter.java
├── RateLimiterFactory.java
├── RateLimitConfig.java
├── RateLimitResult.java
├── RateLimitAlgorithm.java
├── strategy/
│   ├── RateLimitStrategy.java
│   ├── FixedWindowRateLimitStrategy.java
│   └── TokenBucketRateLimitStrategy.java
└── store/
    ├── InMemoryRateLimitStore.java
    ├── FixedWindowState.java
    └── TokenBucketState.java
```

## Core Classes

- `RateLimiter`: public interface used by callers.
- `DefaultRateLimiter`: validates input and delegates to the selected strategy.
- `RateLimiterFactory`: builds a limiter from config.
- `RateLimitStrategy`: common interface for algorithms.
- `FixedWindowRateLimitStrategy`: fixed window counter implementation.
- `TokenBucketRateLimitStrategy`: token bucket implementation.
- `InMemoryRateLimitStore`: stores per-client state safely.

## Design Patterns Used

- Strategy Pattern: swap Fixed Window and Token Bucket without changing caller code.
- Factory Pattern: create the correct limiter based on config.

## Example Usage

```java
RateLimiter limiter = RateLimiterFactory.create(
    RateLimitConfig.tokenBucket(2, 1, 1_000L)
);

RateLimitResult result = limiter.allowRequest("client-1");
```

## Run

From repository root:

```bash
javac -d /private/tmp/java-lld-rate-limit-out \
  $(find rate-limit/src -name "*.java")

java -cp /private/tmp/java-lld-rate-limit-out com.tien.lld.ratelimit.Main
```


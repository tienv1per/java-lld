# Cache System

Plain Java implementation for a Low Level Design / Machine Coding interview exercise.

## Scope

- Generic cache: `Cache<K, V>`.
- Fixed capacity.
- Thread-safe.
- LRU eviction.
- TTL per entry.
- Null key/value rejected.
- `get(missingKey)` returns `null`.
- Runnable `Main` demo with single-thread and multi-thread scenarios.

## Design

```text
com.tien.lld.cachesystem
├── Main.java
├── Cache.java
├── DefaultCache.java
├── CacheConfig.java
├── CacheEntry.java
├── CacheStats.java
├── eviction/
│   ├── EvictionPolicy.java
│   └── LRUEvictionPolicy.java
└── exceptions/
    ├── CacheException.java
    ├── InvalidCacheConfigException.java
    └── InvalidCacheOperationException.java
```

## Patterns Used

- Strategy Pattern: `EvictionPolicy` allows adding LFU/FIFO later.
- Builder Pattern: `CacheConfig`.

## Thread Safety

`DefaultCache` uses a single `ReentrantLock` around public operations because `LinkedHashMap` is not thread-safe and access-order `get` mutates internal ordering.

## Run

From repository root:

```bash
javac -d /private/tmp/java-lld-cache-system-out \
  $(find cache-system/src -name "*.java")

java -cp /private/tmp/java-lld-cache-system-out com.tien.lld.cachesystem.Main
```


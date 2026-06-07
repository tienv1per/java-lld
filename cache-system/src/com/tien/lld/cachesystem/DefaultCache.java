package com.tien.lld.cachesystem;

import com.tien.lld.cachesystem.eviction.EvictionPolicy;
import com.tien.lld.cachesystem.eviction.LRUEvictionPolicy;
import com.tien.lld.cachesystem.exceptions.InvalidCacheOperationException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public final class DefaultCache<K, V> implements Cache<K, V> {
    private final CacheConfig config;
    private final EvictionPolicy<K, V> evictionPolicy;
    private final LinkedHashMap<K, CacheEntry<V>> entries;
    private final ReentrantLock lock = new ReentrantLock();

    private long hitCount;
    private long missCount;
    private long evictionCount;
    private long expirationCount;

    public DefaultCache(CacheConfig config) {
        this(config, new LRUEvictionPolicy<>());
    }

    public DefaultCache(CacheConfig config, EvictionPolicy<K, V> evictionPolicy) {
        if (config == null) {
            throw new InvalidCacheOperationException("config is required");
        }
        if (evictionPolicy == null) {
            throw new InvalidCacheOperationException("evictionPolicy is required");
        }
        this.config = config;
        this.evictionPolicy = evictionPolicy;
        this.entries = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public void put(K key, V value) {
        validateKey(key);
        validateValue(value);

        lock.lock();
        try {
            removeExpiredEntries(System.currentTimeMillis());

            CacheEntry<V> entry = new CacheEntry<>(value, System.currentTimeMillis(), config.getTtlMillis());
            entries.put(key, entry);

            if (entries.size() > config.getCapacity()) {
                evictOneEntry();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(K key) {
        validateKey(key);

        lock.lock();
        try {
            CacheEntry<V> entry = entries.get(key);
            if (entry == null) {
                missCount++;
                return null;
            }

            if (entry.isExpired(System.currentTimeMillis())) {
                entries.remove(key);
                expirationCount++;
                missCount++;
                return null;
            }

            hitCount++;
            return entry.getValue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(K key) {
        validateKey(key);

        lock.lock();
        try {
            CacheEntry<V> removed = entries.remove(key);
            return removed == null ? null : removed.getValue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(K key) {
        validateKey(key);

        lock.lock();
        try {
            CacheEntry<V> entry = entries.get(key);
            if (entry == null) {
                return false;
            }
            if (entry.isExpired(System.currentTimeMillis())) {
                entries.remove(key);
                expirationCount++;
                return false;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            removeExpiredEntries(System.currentTimeMillis());
            return entries.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            entries.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CacheStats stats() {
        lock.lock();
        try {
            return new CacheStats(hitCount, missCount, evictionCount, expirationCount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            removeExpiredEntries(System.currentTimeMillis());
            return "DefaultCache{" +
                    "entries=" + entriesAsString() +
                    ", stats=" + new CacheStats(hitCount, missCount, evictionCount, expirationCount) +
                    '}';
        } finally {
            lock.unlock();
        }
    }

    private void evictOneEntry() {
        K keyToEvict = evictionPolicy.selectKeyToEvict(entries);
        if (keyToEvict != null && entries.remove(keyToEvict) != null) {
            evictionCount++;
        }
    }

    private void removeExpiredEntries(long nowMillis) {
        Iterator<Map.Entry<K, CacheEntry<V>>> iterator = entries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, CacheEntry<V>> entry = iterator.next();
            if (entry.getValue().isExpired(nowMillis)) {
                iterator.remove();
                expirationCount++;
            }
        }
    }

    private void validateKey(K key) {
        if (key == null) {
            throw new InvalidCacheOperationException("key cannot be null");
        }
    }

    private void validateValue(V value) {
        if (value == null) {
            throw new InvalidCacheOperationException("value cannot be null");
        }
    }

    private String entriesAsString() {
        StringBuilder builder = new StringBuilder("{");
        Iterator<Map.Entry<K, CacheEntry<V>>> iterator = entries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, CacheEntry<V>> entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue().getValue());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }
}

package com.tien.lld.cachesystem.eviction;

import com.tien.lld.cachesystem.CacheEntry;

import java.util.LinkedHashMap;

public final class LRUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    @Override
    public K selectKeyToEvict(LinkedHashMap<K, CacheEntry<V>> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        return entries.keySet().iterator().next();
    }
}


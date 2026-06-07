package com.tien.lld.cachesystem.eviction;

import com.tien.lld.cachesystem.CacheEntry;

import java.util.LinkedHashMap;

public interface EvictionPolicy<K, V> {
    K selectKeyToEvict(LinkedHashMap<K, CacheEntry<V>> entries);
}


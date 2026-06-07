package com.tien.lld.cachesystem;

public interface Cache<K, V> {
    void put(K key, V value);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);

    int size();

    void clear();

    CacheStats stats();
}


package com.tien.lld.cachesystem;

import com.tien.lld.cachesystem.eviction.LRUEvictionPolicy;
import com.tien.lld.cachesystem.exceptions.CacheException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        runSingleThreadDemo();
        runMultiThreadDemo();
    }

    private static void runSingleThreadDemo() throws InterruptedException {
        System.out.println("=== Single Thread Demo ===");

        Cache<String, Integer> cache = new DefaultCache<>(
                CacheConfig.builder()
                        .capacity(3)
                        .ttlMillis(1_000L)
                        .build(),
                new LRUEvictionPolicy<>()
        );

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("After A, B, C: " + cache);

        System.out.println("get(A): " + cache.get("A"));
        cache.put("D", 4);
        System.out.println("After put D, LRU B should be evicted: " + cache);
        System.out.println("get(B): " + cache.get("B"));

        cache.put("A", 10);
        System.out.println("After update A: get(A)=" + cache.get("A"));

        System.out.println("remove(C): " + cache.remove("C"));
        System.out.println("After remove C: " + cache);

        cache.put("E", 5);
        System.out.println("get(E) before TTL expires: " + cache.get("E"));
        Thread.sleep(1_100L);
        System.out.println("get(E) after TTL expires: " + cache.get("E"));

        run("put null key", () -> cache.put(null, 99));
        run("put null value", () -> cache.put("NULL", null));

        System.out.println("Final stats: " + cache.stats());
        System.out.println();
    }

    private static void runMultiThreadDemo() throws InterruptedException {
        System.out.println("=== Multi Thread Demo ===");

        Cache<String, Integer> cache = new DefaultCache<>(
                CacheConfig.builder()
                        .capacity(5)
                        .ttlMillis(5_000L)
                        .build()
        );

        int threadCount = 5;
        int operationsPerThread = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            int workerId = threadIndex;
            executorService.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        String key = "K" + (i % 8);
                        cache.put(key, workerId * 100 + i);
                        cache.get(key);
                        cache.get("missing-" + i);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        if (!done.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("multi-thread demo timed out");
        }
        executorService.shutdown();

        System.out.println("Final cache: " + cache);
        System.out.println("Final size: " + cache.size());
        System.out.println("Capacity respected: " + (cache.size() <= 5));
        System.out.println("Final stats: " + cache.stats());
    }

    private static void run(String title, DemoAction action) {
        System.out.println();
        System.out.println("Testcase: " + title);
        try {
            action.run();
        } catch (CacheException exception) {
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface DemoAction {
        void run();
    }
}


package com.tien.lld.messagequeue.strategy;

public final class KeyHashPartitioningStrategy implements PartitioningStrategy {
    @Override
    public int selectPartition(String key, int partitionCount) {
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }
        return Math.floorMod(key.hashCode(), partitionCount);
    }
}


package com.tien.lld.messagequeue.strategy;

public interface PartitioningStrategy {
    int selectPartition(String key, int partitionCount);
}


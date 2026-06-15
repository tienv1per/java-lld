package com.tien.lld.messagequeue.entities;

import com.tien.lld.messagequeue.exceptions.InvalidRequestException;
import com.tien.lld.messagequeue.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Topic {
    private final String name;
    private final List<Partition> partitions;
    private final Map<String, Subscriber> subscribersById = new ConcurrentHashMap<>();
    private final AtomicInteger roundRobinCounter = new AtomicInteger();

    public Topic(String name, int partitionCount) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException("topic name is required");
        }
        if (partitionCount <= 0) {
            throw new InvalidRequestException("partitionCount must be positive");
        }
        this.name = name;
        this.partitions = createPartitions(partitionCount);
    }

    public String getName() {
        return name;
    }

    public int getPartitionCount() {
        return partitions.size();
    }

    public Partition getPartition(int partitionId) {
        if (partitionId < 0 || partitionId >= partitions.size()) {
            throw new InvalidRequestException("invalid partitionId: " + partitionId);
        }
        return partitions.get(partitionId);
    }

    public List<Partition> getPartitions() {
        return Collections.unmodifiableList(partitions);
    }

    public void addSubscriber(Subscriber subscriber) {
        if (subscriber == null) {
            throw new InvalidRequestException("subscriber is required");
        }
        subscribersById.put(subscriber.getId(), subscriber);
    }

    public void removeSubscriber(String subscriberId) {
        if (subscriberId == null || subscriberId.trim().isEmpty()) {
            throw new InvalidRequestException("subscriberId is required");
        }
        subscribersById.remove(subscriberId);
    }

    public Collection<Subscriber> getSubscribersSnapshot() {
        return List.copyOf(subscribersById.values());
    }

    public int nextRoundRobinPartitionId() {
        return Math.floorMod(roundRobinCounter.getAndIncrement(), partitions.size());
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", partitionCount=" + partitions.size() +
                ", subscriberCount=" + subscribersById.size() +
                '}';
    }

    private List<Partition> createPartitions(int partitionCount) {
        List<Partition> createdPartitions = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {
            createdPartitions.add(new Partition(i));
        }
        return Collections.unmodifiableList(createdPartitions);
    }
}


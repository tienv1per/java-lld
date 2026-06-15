package com.tien.lld.messagequeue;

import com.tien.lld.messagequeue.entities.Message;
import com.tien.lld.messagequeue.entities.Partition;
import com.tien.lld.messagequeue.entities.Publisher;
import com.tien.lld.messagequeue.entities.Topic;
import com.tien.lld.messagequeue.exceptions.InvalidRequestException;
import com.tien.lld.messagequeue.strategy.KeyHashPartitioningStrategy;
import com.tien.lld.messagequeue.strategy.PartitioningStrategy;
import com.tien.lld.messagequeue.subscriber.Subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageQueueService {
    private final TopicManager topicManager;
    private final Dispatcher dispatcher;
    private final PartitioningStrategy partitioningStrategy;
    private final Map<String, Publisher> publishersById = new ConcurrentHashMap<>();

    public MessageQueueService(int dispatcherThreadCount) {
        this.topicManager = new TopicManager();
        this.dispatcher = new Dispatcher(dispatcherThreadCount);
        this.partitioningStrategy = new KeyHashPartitioningStrategy();
    }

    public Topic createTopic(String topicName, int partitionCount) {
        return topicManager.createTopic(topicName, partitionCount);
    }

    public Publisher createPublisher(String publisherId) {
        validateText(publisherId, "publisherId");
        Publisher publisher = new Publisher(publisherId);
        Publisher existingPublisher = publishersById.putIfAbsent(publisherId, publisher);
        if (existingPublisher != null) {
            throw new InvalidRequestException("publisher already exists: " + publisherId);
        }
        return publisher;
    }

    public void subscribe(String topicName, Subscriber subscriber) {
        if (subscriber == null) {
            throw new InvalidRequestException("subscriber is required");
        }
        Topic topic = topicManager.getTopic(topicName);
        topic.addSubscriber(subscriber);
    }

    public void unsubscribe(String topicName, String subscriberId) {
        validateText(subscriberId, "subscriberId");
        Topic topic = topicManager.getTopic(topicName);
        topic.removeSubscriber(subscriberId);
    }

    public Message publish(String publisherId, String topicName, String key, String payload) {
        validateText(publisherId, "publisherId");
        validateText(payload, "payload");

        Publisher publisher = publishersById.get(publisherId);
        if (publisher == null) {
            throw new InvalidRequestException("publisher not found: " + publisherId);
        }

        Topic topic = topicManager.getTopic(topicName);
        int partitionId = selectPartition(topic, key);
        Partition partition = topic.getPartition(partitionId);

        Message message = partition.appendMessage(topic.getName(), key, payload, publisher.getId());
        dispatcher.dispatch(topic.getSubscribersSnapshot(), message);
        return message;
    }

    public void shutdown() {
        dispatcher.shutdown();
    }

    private int selectPartition(Topic topic, String key) {
        if (key == null || key.trim().isEmpty()) {
            return topic.nextRoundRobinPartitionId();
        }
        return partitioningStrategy.selectPartition(key, topic.getPartitionCount());
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidRequestException(fieldName + " is required");
        }
    }
}

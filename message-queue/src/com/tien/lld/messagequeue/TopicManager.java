package com.tien.lld.messagequeue;

import com.tien.lld.messagequeue.entities.Topic;
import com.tien.lld.messagequeue.exceptions.InvalidRequestException;
import com.tien.lld.messagequeue.exceptions.TopicNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TopicManager {
    private final Map<String, Topic> topicsByName = new ConcurrentHashMap<>();

    public Topic createTopic(String topicName, int partitionCount) {
        validateTopicName(topicName);
        if (partitionCount <= 0) {
            throw new InvalidRequestException("partitionCount must be positive");
        }

        Topic topic = new Topic(topicName, partitionCount);
        Topic existingTopic = topicsByName.putIfAbsent(topicName, topic);
        if (existingTopic != null) {
            throw new InvalidRequestException("topic already exists: " + topicName);
        }
        return topic;
    }

    public Topic getTopic(String topicName) {
        validateTopicName(topicName);
        Topic topic = topicsByName.get(topicName);
        if (topic == null) {
            throw new TopicNotFoundException("topic not found: " + topicName);
        }
        return topic;
    }

    private void validateTopicName(String topicName) {
        if (topicName == null || topicName.trim().isEmpty()) {
            throw new InvalidRequestException("topicName is required");
        }
    }
}


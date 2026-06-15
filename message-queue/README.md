# Pub/Sub Message Queue

Plain Java implementation for a Low Level Design / Machine Coding interview exercise.

## Scope

- In-memory only.
- Pub/Sub push model with asynchronous delivery.
- Multiple topics.
- Multiple publishers.
- Multiple subscribers per topic.
- Fixed partition count per topic.
- Key-based partitioning when a key is provided.
- Round-robin partitioning when the key is null or blank.
- No polling, consumer groups, or offset commits in V1.

## Design

```text
com.tien.lld.messagequeue
├── Main.java
├── MessageQueueService.java
├── TopicManager.java
├── Dispatcher.java
├── entities/
│   ├── Message.java
│   ├── Topic.java
│   ├── Partition.java
│   └── Publisher.java
├── subscriber/
│   ├── Subscriber.java
│   ├── PrintSubscriber.java
│   └── LoggingSubscriber.java
├── strategy/
│   ├── PartitioningStrategy.java
│   └── KeyHashPartitioningStrategy.java
└── exceptions/
    ├── MessageQueueException.java
    ├── InvalidRequestException.java
    └── TopicNotFoundException.java
```

## Patterns Used

- Observer Pattern: topics notify subscribers.
- Strategy Pattern: partition selection.
- Facade: `MessageQueueService` exposes the public API.
- Builder Pattern: `Message`.

## Run

From repository root:

```bash
javac -d /private/tmp/java-lld-message-queue-out \
  $(find message-queue/src -name "*.java")

java -cp /private/tmp/java-lld-message-queue-out com.tien.lld.messagequeue.Main
```


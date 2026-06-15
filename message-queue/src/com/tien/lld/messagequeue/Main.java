package com.tien.lld.messagequeue;

import com.tien.lld.messagequeue.entities.Publisher;
import com.tien.lld.messagequeue.exceptions.MessageQueueException;
import com.tien.lld.messagequeue.subscriber.LoggingSubscriber;
import com.tien.lld.messagequeue.subscriber.PrintSubscriber;
import com.tien.lld.messagequeue.subscriber.Subscriber;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        MessageQueueService queueService = new MessageQueueService(4);

        queueService.createTopic("orders", 3);
        queueService.createTopic("payments", 2);

        Publisher orderService = queueService.createPublisher("order-service");
        Publisher paymentService = queueService.createPublisher("payment-service");

        Subscriber printSubscriber = new PrintSubscriber("print-subscriber");
        Subscriber loggingSubscriber = new LoggingSubscriber("logging-subscriber");

        queueService.subscribe("orders", printSubscriber);
        queueService.subscribe("orders", loggingSubscriber);

        System.out.println("=== Publish keyed messages to orders ===");
        System.out.println(queueService.publish(orderService.getId(), "orders", "order-1", "order-created"));
        System.out.println(queueService.publish(orderService.getId(), "orders", "order-2", "order-paid"));
        System.out.println(queueService.publish(orderService.getId(), "orders", "order-3", "order-cancelled"));

        System.out.println();
        System.out.println("=== Publish blank-key messages to orders with round-robin partitions ===");
        System.out.println(queueService.publish(orderService.getId(), "orders", null, "round-robin-message-1"));
        System.out.println(queueService.publish(orderService.getId(), "orders", "", "round-robin-message-2"));
        System.out.println(queueService.publish(orderService.getId(), "orders", "   ", "round-robin-message-3"));

        waitForAsyncDelivery();

        System.out.println();
        System.out.println("=== Unsubscribe logging subscriber ===");
        queueService.unsubscribe("orders", loggingSubscriber.getId());
        System.out.println(queueService.publish(orderService.getId(), "orders", "order-4", "order-shipped"));

        waitForAsyncDelivery();

        System.out.println();
        System.out.println("=== Payments topic has no subscribers yet ===");
        System.out.println(queueService.publish(paymentService.getId(), "payments", "payment-1", "payment-created"));

        run("subscribe null subscriber", () -> queueService.subscribe("orders", null));
        run("publish to missing topic", () ->
                System.out.println(queueService.publish(orderService.getId(), "missing-topic", "k1", "payload"))
        );
        run("publish blank payload", () ->
                System.out.println(queueService.publish(orderService.getId(), "orders", "k1", " "))
        );
        run("publish with missing publisher", () ->
                System.out.println(queueService.publish("missing-publisher", "orders", "k1", "payload"))
        );
        run("create duplicate topic", () -> queueService.createTopic("orders", 2));

        runConcurrentPublishDemo(queueService, orderService);

        queueService.shutdown();
    }

    private static void waitForAsyncDelivery() throws InterruptedException {
        Thread.sleep(300L);
    }

    private static void run(String title, DemoAction action) {
        System.out.println();
        System.out.println("Testcase: " + title);
        try {
            action.run();
        } catch (MessageQueueException exception) {
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    private static void runConcurrentPublishDemo(
            MessageQueueService queueService,
            Publisher publisher
    ) throws InterruptedException {
        System.out.println();
        System.out.println("=== Concurrent Publish Demo ===");

        int threadCount = 3;
        int messagesPerThread = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int worker = 0; worker < threadCount; worker++) {
            int workerId = worker;
            executorService.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < messagesPerThread; i++) {
                        queueService.publish(
                                publisher.getId(),
                                "orders",
                                "worker-" + workerId,
                                "concurrent-message-" + workerId + "-" + i
                        );
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        if (!done.await(3, TimeUnit.SECONDS)) {
            throw new IllegalStateException("concurrent publish demo timed out");
        }
        executorService.shutdown();
        waitForAsyncDelivery();
        System.out.println("Concurrent publish demo completed");
    }

    @FunctionalInterface
    private interface DemoAction {
        void run();
    }
}

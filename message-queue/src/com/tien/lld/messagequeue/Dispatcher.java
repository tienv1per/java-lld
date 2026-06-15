package com.tien.lld.messagequeue;

import com.tien.lld.messagequeue.entities.Message;
import com.tien.lld.messagequeue.exceptions.InvalidRequestException;
import com.tien.lld.messagequeue.subscriber.Subscriber;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Dispatcher {
    private final ExecutorService executorService;

    public Dispatcher(int threadCount) {
        if (threadCount <= 0) {
            throw new InvalidRequestException("dispatcher threadCount must be positive");
        }
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void dispatch(Collection<Subscriber> subscribers, Message message) {
        for (Subscriber subscriber : subscribers) {
            executorService.submit(() -> subscriber.consume(message));
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}


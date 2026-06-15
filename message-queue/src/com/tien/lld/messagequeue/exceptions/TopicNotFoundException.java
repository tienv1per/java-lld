package com.tien.lld.messagequeue.exceptions;

public final class TopicNotFoundException extends MessageQueueException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}


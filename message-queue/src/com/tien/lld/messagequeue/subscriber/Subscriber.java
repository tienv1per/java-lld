package com.tien.lld.messagequeue.subscriber;

import com.tien.lld.messagequeue.entities.Message;

public interface Subscriber {
    String getId();

    void consume(Message message);
}


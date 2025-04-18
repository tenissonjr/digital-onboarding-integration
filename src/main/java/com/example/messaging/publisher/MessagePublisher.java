package com.example.messaging.publisher;

import com.example.messaging.broker.MessageBroker;

public class MessagePublisher {
    private final MessageBroker messageBroker;

    public MessagePublisher(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    public void publishMessage(String topic, String message) {
        messageBroker.publish(topic, message);
    }
}
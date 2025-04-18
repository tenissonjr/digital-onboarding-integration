package com.example.messaging.consumer;

import com.example.messaging.broker.MessageBroker;

public class ConsoleConsumer {
    public ConsoleConsumer(MessageBroker messageBroker, String topic) {
        messageBroker.subscribe(topic, message -> doAction(message));
    }

    private void doAction(String message) {
        System.out.println("Received message: " + message);
    }
}
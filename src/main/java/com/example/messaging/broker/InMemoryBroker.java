package com.example.messaging.broker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class InMemoryBroker implements MessageBroker {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Consumer<String>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(String topic, String message) {
        var consumers = subscribers.getOrDefault(topic, new CopyOnWriteArrayList<>());
        for (var consumer : consumers) {
            consumer.accept(message);
        }
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }
}
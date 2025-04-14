package com.onboarding.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
@Slf4j
public class InMemoryQueueService {
    
    private final Map<String, BlockingQueue<Object>> queues = new ConcurrentHashMap<>();
    private final Map<String, Thread> consumerThreads = new ConcurrentHashMap<>();
    
    /**
     * Send a message to a queue
     */
    public void send(String queueName, Object message) {
        log.debug("Sending message to queue {}: {}", queueName, message);
        getOrCreateQueue(queueName).add(message);
    }
    
    /**
     * Register a consumer for a queue
     */
    public <T> void registerConsumer(String queueName, Class<T> messageType, Consumer<T> consumer) {
        log.info("Registering consumer for queue: {}", queueName);
        BlockingQueue<Object> queue = getOrCreateQueue(queueName);
        
        // Create and start consumer thread
        Thread consumerThread = new Thread(() -> {
            log.info("Starting consumer thread for queue: {}", queueName);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Object message = queue.take(); // Blocking
                    if (messageType.isInstance(message)) {
                        try {
                            log.debug("Processing message from queue {}: {}", queueName, message);
                            consumer.accept(messageType.cast(message));
                        } catch (Exception e) {
                            log.error("Error processing message from queue {}: {}", queueName, e.getMessage(), e);
                        }
                    } else {
                        log.warn("Received message of incorrect type. Expected {}, got {}", 
                                messageType.getName(), message.getClass().getName());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Consumer thread for queue {} was interrupted", queueName);
                    break;
                }
            }
            log.info("Consumer thread for queue {} is stopping", queueName);
        });
        
        consumerThread.setDaemon(true);
        consumerThread.start();
        
        consumerThreads.put(queueName, consumerThread);
    }
    
    /**
     * Stop a consumer
     */
    public void stopConsumer(String queueName) {
        Thread thread = consumerThreads.remove(queueName);
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    /**
     * Stop all consumers
     */
    public void stopAllConsumers() {
        consumerThreads.forEach((queueName, thread) -> thread.interrupt());
        consumerThreads.clear();
    }
    
    /**
     * Get or create a queue
     */
    private BlockingQueue<Object> getOrCreateQueue(String queueName) {
        return queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>());
    }
}
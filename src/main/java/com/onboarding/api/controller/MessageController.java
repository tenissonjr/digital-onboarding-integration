package com.onboarding.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.messaging.publisher.MessagePublisher;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessagePublisher messagePublisher;

    public MessageController(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @PostMapping("/{topic}")
    public void sendMessage(@PathVariable String topic, @RequestBody String message) {
        messagePublisher.publishMessage(topic, message);
    }
}
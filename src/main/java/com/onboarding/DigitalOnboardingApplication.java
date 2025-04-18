package com.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.messaging.broker.InMemoryBroker;
import com.example.messaging.broker.MessageBroker;
import com.example.messaging.consumer.ConsoleConsumer;
import com.example.messaging.publisher.MessagePublisher;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DigitalOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalOnboardingApplication.class, args);
    }

        @Bean
    public MessageBroker messageBroker() {
        return new InMemoryBroker();
    }

    @Bean
    public MessagePublisher messagePublisher(MessageBroker messageBroker) {
        return new MessagePublisher(messageBroker);
    }

    @Bean
    public ConsoleConsumer consoleConsumer(MessageBroker messageBroker) {
        return new ConsoleConsumer(messageBroker, "default-topic");
    }
}
package com.onboarding.service.queue;

import com.onboarding.service.ReceiveHashService;
import com.onboarding.service.ValidationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QueueConsumerConfig {
    
    private final InMemoryQueueService queueService;
    private final ReceiveHashService onboardingProcessorService;
    private final ValidationService validationService;
    
    @PostConstruct
    public void init() {
        // Register consumer for hash received queue
        queueService.registerConsumer(
            QueueConstants.HASH_RECEIVED_QUEUE, 
            String.class, 
            hash -> onboardingProcessorService.processHash(hash)
        );
        
        // Register consumer for data validation queue
        queueService.registerConsumer(
            QueueConstants.DATA_VALIDATION_QUEUE,
            String.class,
            hash -> validationService.validateData(hash)
        );
        
        log.info("Queue consumers registered");
    }
    
    @PreDestroy
    public void destroy() {
        queueService.stopAllConsumers();
        log.info("Queue consumers stopped");
    }
}
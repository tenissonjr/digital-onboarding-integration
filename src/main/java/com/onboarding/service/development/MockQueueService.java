package com.onboarding.service.development;

import com.onboarding.service.OnboardingProcessorService;
import com.onboarding.service.ValidationService;
import com.onboarding.service.queue.InMemoryQueueService;
import com.onboarding.service.queue.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class MockQueueService {

    private final InMemoryQueueService queueService;
    private final OnboardingProcessorService onboardingProcessorService;
    private final ValidationService validationService;

    // Method to send a test hash directly to the processing service
    public void submitTestHash(String hash) {
        log.info("[DEV] Submitting test hash: {}", hash);
        queueService.send(QueueConstants.HASH_RECEIVED_QUEUE, hash);
    }
}
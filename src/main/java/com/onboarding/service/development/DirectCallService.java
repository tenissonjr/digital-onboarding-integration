package com.onboarding.service.development;

import com.onboarding.service.OnboardingProcessorService;
import com.onboarding.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DirectCallService {

    private final OnboardingProcessorService onboardingProcessorService;
    private final ValidationService validationService;

    @Async
    public void submitTestHash(String hash) {
        log.info("[DEV] Submitting test hash: {}", hash);
        onboardingProcessorService.processHash(hash);
    }
}
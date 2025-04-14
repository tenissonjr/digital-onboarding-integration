package com.onboarding.service;

import com.onboarding.api.dto.response.StatisticsResponse;
import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.FetchAttemptRepository;
import com.onboarding.domain.repository.OnboardingDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OnboardingDataRepository onboardingDataRepository;
    private final FetchAttemptRepository fetchAttemptRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticsResponse getStatistics() {
        // Counts for OnboardingStatus
        Map<OnboardingStatus, Long> onboardingStatusCounts = new EnumMap<>(OnboardingStatus.class);
        for (OnboardingStatus status : OnboardingStatus.values()) {
            long count = onboardingDataRepository.countByStatus(status);
            onboardingStatusCounts.put(status, count);
        }

        // Counts for FetchStatus
        Map<FetchStatus, Long> fetchStatusCounts = new EnumMap<>(FetchStatus.class);
        for (FetchStatus status : FetchStatus.values()) {
            long count = fetchAttemptRepository.countByStatus(status);
            fetchStatusCounts.put(status, count);
        }

        return StatisticsResponse.builder()
                .onboardingStatusCounts(onboardingStatusCounts)
                .fetchStatusCounts(fetchStatusCounts)
                .timestamp(LocalDateTime.now().format(formatter))
                .user("tenissonjr")
                .build();
    }
}
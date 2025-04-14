package com.onboarding.service;

import com.onboarding.api.dto.request.StatsByTimeRequest;
import com.onboarding.api.dto.response.StatsByTimeResponse;
import com.onboarding.api.dto.response.StatsByTimeResponse.TimePeriodStats;
import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.FetchAttemptRepository;
import com.onboarding.domain.repository.OnboardingDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsByTimeService {

    private final OnboardingDataRepository onboardingDataRepository;
    private final FetchAttemptRepository fetchAttemptRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsByTimeResponse getStatsByTime(StatsByTimeRequest request) {
        // Calculate the time periods and gather statistics for each
        LocalDateTime now = LocalDateTime.now();
        List<TimePeriodStats> timePeriodStatsList = new ArrayList<>();

        for (int i = 0; i < request.getAmount(); i++) {
            LocalDateTime startTime = calculateStartTime(now, request.getUnit(), i + 1);
            LocalDateTime endTime = calculateStartTime(now, request.getUnit(), i);

            // Gather statistics for this time period
            Map<OnboardingStatus, Long> onboardingStatusCounts = gatherOnboardingStats(startTime, endTime);
            Map<FetchStatus, Long> fetchStatusCounts = gatherFetchStats(startTime, endTime);

            // Add to the list
            timePeriodStatsList.add(TimePeriodStats.builder()
                    .periodLabel(formatPeriodLabel(request.getUnit(), i))
                    .onboardingStatusCounts(onboardingStatusCounts)
                    .fetchStatusCounts(fetchStatusCounts)
                    .build());
        }

        return StatsByTimeResponse.builder()
                .timePeriodStats(timePeriodStatsList)
                .timestamp(now.format(formatter))
                .user("tenissonjr")
                .build();
    }

    private Map<OnboardingStatus, Long> gatherOnboardingStats(LocalDateTime startTime, LocalDateTime endTime) {
        Map<OnboardingStatus, Long> counts = new EnumMap<>(OnboardingStatus.class);
        for (OnboardingStatus status : OnboardingStatus.values()) {
            long count = onboardingDataRepository.countByStatusAndCreatedAtBetween(status, startTime, endTime);
            counts.put(status, count);
        }
        return counts;
    }

    private Map<FetchStatus, Long> gatherFetchStats(LocalDateTime startTime, LocalDateTime endTime) {
        Map<FetchStatus, Long> counts = new EnumMap<>(FetchStatus.class);
        for (FetchStatus status : FetchStatus.values()) {
            long count = fetchAttemptRepository.countByStatusAndCreatedAtBetween(status, startTime, endTime);
            counts.put(status, count);
        }
        return counts;
    }

    private LocalDateTime calculateStartTime(LocalDateTime now, StatsByTimeRequest.TimeUnit unit, int amount) {
        return switch (unit) {
            case SEGUNDOS -> now.minus(amount, ChronoUnit.SECONDS);
            case MINUTOS -> now.minus(amount, ChronoUnit.MINUTES);
            case HORAS -> now.minus(amount, ChronoUnit.HOURS);
            case DIAS -> now.minus(amount, ChronoUnit.DAYS);
            case MESES -> now.minus(amount, ChronoUnit.MONTHS);
        };
    }

    private String formatPeriodLabel(StatsByTimeRequest.TimeUnit unit, int index) {
        String unitLabel = switch (unit) {
            case SEGUNDOS -> "Segundo";
            case MINUTOS -> "Minuto";
            case HORAS -> "Hora";
            case DIAS -> "Dia";
            case MESES -> "Mês";
        };
        return (index == 0 ? "Último" : (index + 1) + "º") + " " + unitLabel;
    }
}
package com.onboarding.api.dto.response;

import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsByTimeResponse {

    private List<TimePeriodStats> timePeriodStats;
    private String timestamp;
    private String user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimePeriodStats {
        private String periodLabel; // Ex: "Última Hora", "Penúltima Hora"
        private Map<OnboardingStatus, Long> onboardingStatusCounts;
        private Map<FetchStatus, Long> fetchStatusCounts;
    }
}
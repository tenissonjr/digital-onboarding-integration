package com.onboarding.api.dto.response;

import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private Map<OnboardingStatus, Long> onboardingStatusCounts;
    private Map<FetchStatus, Long> fetchStatusCounts;
    private String timestamp;
    private String user;
}
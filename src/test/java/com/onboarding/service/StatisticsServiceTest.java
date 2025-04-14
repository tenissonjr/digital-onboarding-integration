package com.onboarding.service;

import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.FetchAttemptRepository;
import com.onboarding.domain.repository.OnboardingDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    @Mock
    private OnboardingDataRepository onboardingDataRepository;

    @Mock
    private FetchAttemptRepository fetchAttemptRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStatistics() {
        // Mock counts for OnboardingStatus
        when(onboardingDataRepository.countByStatus(OnboardingStatus.EM_PROCESSAMENTO)).thenReturn(5L);
        when(onboardingDataRepository.countByStatus(OnboardingStatus.VALIDADO_SUCESSO)).thenReturn(10L);
        when(onboardingDataRepository.countByStatus(OnboardingStatus.VALIDACAO_COM_ERROS)).thenReturn(2L);

        // Mock counts for FetchStatus
        when(fetchAttemptRepository.countByStatus(FetchStatus.SUCESSO)).thenReturn(8L);
        when(fetchAttemptRepository.countByStatus(FetchStatus.ERRO)).thenReturn(3L);

        // Call the service
        Map<OnboardingStatus, Long> expectedOnboardingStatusCounts = new EnumMap<>(OnboardingStatus.class);
        expectedOnboardingStatusCounts.put(OnboardingStatus.EM_PROCESSAMENTO, 5L);
        expectedOnboardingStatusCounts.put(OnboardingStatus.VALIDADO_SUCESSO, 10L);
        expectedOnboardingStatusCounts.put(OnboardingStatus.VALIDACAO_COM_ERROS, 2L);

        Map<FetchStatus, Long> expectedFetchStatusCounts = new EnumMap<>(FetchStatus.class);
        expectedFetchStatusCounts.put(FetchStatus.SUCESSO, 8L);
        expectedFetchStatusCounts.put(FetchStatus.ERRO, 3L);

        var stats = statisticsService.getStatistics();

        // Assertions
        assertEquals(expectedOnboardingStatusCounts, stats.getOnboardingStatusCounts());
        assertEquals(expectedFetchStatusCounts, stats.getFetchStatusCounts());
        assertNotNull(stats.getTimestamp());
        assertEquals("tenissonjr", stats.getUser());
    }
}
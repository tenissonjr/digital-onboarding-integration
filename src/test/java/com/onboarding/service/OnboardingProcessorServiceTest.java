package com.onboarding.service;

import com.onboarding.domain.entity.FetchAttempt;
import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.repository.FetchAttemptRepository;
import com.onboarding.domain.repository.OnboardingDataRepository;
import com.onboarding.integration.client.OnboardingApiClient;
import com.onboarding.integration.dto.OnboardingDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OnboardingProcessorServiceTest {

    @Mock
    private OnboardingApiClient onboardingApiClient;

    @Mock
    private OnboardingDataRepository onboardingDataRepository;

    @Mock
    private FetchAttemptRepository fetchAttemptRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OnboardingProcessorService onboardingProcessorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(onboardingProcessorService, "maxAttempts", 3);
    }

    @Test
    void processHash_AlreadyProcessed_ShouldEarlyReturn() {
        // Arrange
        String hash = "test123";
        when(onboardingDataRepository.existsByHash(hash)).thenReturn(true);

        // Act
        onboardingProcessorService.processHash(hash);

        // Assert
        verify(onboardingApiClient, never()).fetchOnboardingData(any());
        verify(onboardingDataRepository, never()).save(any());
    }

    @Test
    void processHash_SuccessfulProcessing_ShouldSaveAndValidate() {
        // Arrange
        String hash = "test123";
        when(onboardingDataRepository.existsByHash(hash)).thenReturn(false);
        when(fetchAttemptRepository.findMaxAttemptByHash(hash)).thenReturn(Optional.empty());

        OnboardingDataDto dataDto = createTestDto(hash);
        when(onboardingApiClient.fetchOnboardingData(hash)).thenReturn(dataDto);

        // Act
        onboardingProcessorService.processHash(hash);

        // Assert
        verify(onboardingApiClient).fetchOnboardingData(hash);
        verify(fetchAttemptRepository).save(any(FetchAttempt.class));
        verify(onboardingDataRepository).save(any(OnboardingData.class));
    }

    @Test
    void processHash_ExceedMaxAttempts_ShouldNotifyFailure() {
        // Arrange
        String hash = "test123";
        when(onboardingDataRepository.existsByHash(hash)).thenReturn(false);
        when(fetchAttemptRepository.findMaxAttemptByHash(hash)).thenReturn(Optional.of(3));

        // Act
        onboardingProcessorService.processHash(hash);

        // Assert
        verify(notificationService).notifyFailure(eq(hash), any(String.class));
        verify(fetchAttemptRepository).save(argThat(fetchAttempt -> 
            fetchAttempt.getStatus() == FetchStatus.ERRO && 
            fetchAttempt.getHash().equals(hash)
        ));
    }

    @Test
    void processHash_ApiException_ShouldRetry() {
        // Arrange
        String hash = "test123";
        when(onboardingDataRepository.existsByHash(hash)).thenReturn(false);
        when(fetchAttemptRepository.findMaxAttemptByHash(hash)).thenReturn(Optional.of(1));
        when(onboardingApiClient.fetchOnboardingData(hash)).thenThrow(new RuntimeException("API Error"));

        // Act
        onboardingProcessorService.processHash(hash);

        // Assert
        verify(onboardingApiClient).fetchOnboardingData(hash);
        verify(fetchAttemptRepository).save(argThat(fetchAttempt -> 
            fetchAttempt.getStatus() == FetchStatus.ERRO && 
            fetchAttempt.getHash().equals(hash)
        ));
        verify(notificationService, never()).notifyFailure(any(), any());
    }

    private OnboardingDataDto createTestDto(String hash) {
        return OnboardingDataDto.builder()
                .hash(hash)
                .cpf("12345678909")
                .nome("Test User")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
    }
}
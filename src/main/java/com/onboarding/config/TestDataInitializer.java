package com.onboarding.config;

import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.OnboardingDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev") // Only execute in dev profile
public class TestDataInitializer implements CommandLineRunner {

    private final OnboardingDataRepository onboardingDataRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing test data...");
        
        OnboardingData testData = OnboardingData.builder()
                .hash("test123456789")
                .cpf("12345678909")
                .nome("Usuario Teste")
                .nomeSocial("Nome Social Teste")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .nomeMae("Mae do Usuario")
                .numeroDocumento("123456")
                .paisOrigem("Brasil")
                .orgaoEmissor("SSP")
                .uf("SP")
                .dataExpedicao(LocalDate.of(2015, 1, 1))
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .status(OnboardingStatus.EM_PROCESSAMENTO)
                .dataRecebimento(LocalDateTime.now())
                .createdBy("tenissonjr")
                .build();
        
        onboardingDataRepository.save(testData);
        log.info("Test data initialized successfully");
    }
}
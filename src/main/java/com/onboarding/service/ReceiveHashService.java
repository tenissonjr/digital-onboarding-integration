package com.onboarding.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboarding.domain.entity.FetchAttempt;
import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.model.FetchStatus;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.FetchAttemptRepository;
import com.onboarding.domain.repository.OnboardingDataRepository;
import com.onboarding.integration.client.OnboardingApiClient;
import com.onboarding.integration.dto.OnboardingDataDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiveHashService {

    private final OnboardingApiClient onboardingApiClient;
    private final OnboardingDataRepository onboardingDataRepository;
    private final FetchAttemptRepository fetchAttemptRepository;
    private final ValidationService validationService;
    private final NotificationService notificationService;
    
    @Value("${app.retry.max-attempts}")
    private int maxAttempts;

    @Transactional
    public void processHash(String hash) {
        log.info("Processando hash recebido: {}", hash);
        
        // Verificar se o hash já foi processado
        if (onboardingDataRepository.existsByHash(hash)) {
            log.info("Hash {} já foi processado anteriormente", hash);
            return;
        }
        
        try {
            // Obter o número da tentativa atual
            int attempt = fetchAttemptRepository.findMaxAttemptByHash(hash)
                    .map(maxAttempt -> maxAttempt + 1)
                    .orElse(1);
            
            // Verificar se excedeu o número máximo de tentativas
            if (attempt > maxAttempts) {
                log.error("Número máximo de tentativas excedido para o hash: {}", hash);
                notificationService.notifyFailure(hash, "Número máximo de tentativas excedido");
                registerFailedAttempt(hash, attempt, "Número máximo de tentativas excedido");
                return;
            }
            
            // Buscar os dados do sistema de onboarding
            OnboardingDataDto dataDto = onboardingApiClient.fetchOnboardingData(hash);
            
            // Registrar uma tentativa bem-sucedida
            registerSuccessfulAttempt(hash, attempt);
            
            // Criar entidade com os dados recebidos
            OnboardingData onboardingData = createOnboardingDataEntity(hash,dataDto);
            
            // Persistir a entidade
            onboardingDataRepository.save(onboardingData);
            
            // Process validation asynchronously
            processValidation(hash);
            
            log.info("Hash {} processado com sucesso e enviado para validação", hash);
            
        } catch (Exception e) {
            log.error("Erro ao processar hash {}: {}", hash, e.getMessage(), e);
            
            // Obter o número da tentativa atual
            int attempt = fetchAttemptRepository.findMaxAttemptByHash(hash)
                    .map(maxAttempt -> maxAttempt + 1)
                    .orElse(1);
            
            // Registrar a tentativa falha
            registerFailedAttempt(hash, attempt, e.getMessage());
            
            // Verificar se atingiu o número máximo de tentativas
            if (attempt >= maxAttempts) {
                notificationService.notifyFailure(hash, "Número máximo de tentativas excedido: " + e.getMessage());
                log.error("Número máximo de tentativas excedido para o hash: {}", hash);
            } else {
                // Reenviar para processamento após um breve atraso
                log.info("Reagendando processamento do hash {} (tentativa {})", hash, attempt + 1);
                
                // Simulated retry with delay
                final int currentAttempt = attempt;
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000 * currentAttempt); // Incremental delay for retries
                        processHash(hash);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }
    
    @Async
    public void processValidation(String hash) {
        try {
            validationService.validateData(hash);
        } catch (Exception e) {
            log.error("Erro ao validar dados para hash {}: {}", hash, e.getMessage(), e);
        }
    }
    
    private OnboardingData createOnboardingDataEntity(String hash, OnboardingDataDto dataDto) {
        return OnboardingData.builder()
                .hash(hash)
                .cpf(dataDto.getCpf())
                .nome(dataDto.getNome())
                .nomeSocial(dataDto.getNomeSocial())
                .dataNascimento(dataDto.getDataNascimento())
                .nomeMae(dataDto.getNomeMae())
                .numeroDocumento(dataDto.getNumeroDocumento())
                .paisOrigem(dataDto.getPaisOrigem())
                .orgaoEmissor(dataDto.getOrgaoEmissor())
                .uf(dataDto.getUf())
                .dataExpedicao(dataDto.getDataExpedicao())
                .dataVencimento(dataDto.getDataVencimento())
                .status(OnboardingStatus.EM_PROCESSAMENTO)
                .dataRecebimento(LocalDateTime.now())
                .createdBy("tenissonjr")
                .build();
    }
    
    private void registerSuccessfulAttempt(String hash, int attempt) {
        FetchAttempt fetchAttempt = FetchAttempt.builder()
                .hash(hash)
                .dataHora(LocalDateTime.now())
                .status(FetchStatus.SUCESSO)
                .tentativa(attempt)
                .createdBy("tenissonjr")
                .build();
        fetchAttemptRepository.save(fetchAttempt);
    }
    
    private void registerFailedAttempt(String hash, int attempt, String errorMessage) {
        FetchAttempt fetchAttempt = FetchAttempt.builder()
                .hash(hash)
                .dataHora(LocalDateTime.now())
                .status(FetchStatus.ERRO)
                .mensagemErro(errorMessage)
                .tentativa(attempt)
                .createdBy("tenissonjr")
                .build();
        fetchAttemptRepository.save(fetchAttempt);
    }
}
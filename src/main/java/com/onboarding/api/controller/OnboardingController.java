package com.onboarding.api.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.onboarding.api.dto.request.NotificationRequest;
import com.onboarding.api.dto.response.StatusResponse;
import com.onboarding.api.dto.response.ValidationErrorResponse;
import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.entity.ValidationError;
import com.onboarding.domain.repository.OnboardingDataRepository;
import com.onboarding.domain.repository.ValidationErrorRepository;
import com.onboarding.service.OnboardingProcessorService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for handling onboarding related requests
 * 
 * @author tenissonjr
 * @version 1.1
 * @since 2025-04-14
 */
@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Slf4j
public class OnboardingController {

    private final OnboardingDataRepository onboardingDataRepository;
    private final ValidationErrorRepository validationErrorRepository;
    private final OnboardingProcessorService onboardingProcessorService;
    private final MeterRegistry meterRegistry;
    
    private Counter notificationCounter;
    private Counter rateExceededCounter;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Rate limiter implementation
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
    
    @Value("${app.ratelimit.limit:10}")
    private int rateLimit;
    
    @Value("${app.ratelimit.refresh-period:1}")
    private int refreshPeriodSeconds;
    
    @PostConstruct
    public void init() {
        notificationCounter = Counter.builder("onboarding.notifications")
                .description("Counter of received onboarding notifications")
                .register(meterRegistry);
                
        rateExceededCounter = Counter.builder("onboarding.rate_exceeded")
                .description("Counter of rate limit exceeded events")
                .register(meterRegistry);
                
        log.info("OnboardingController initialized with rate limit: {}/{} sec", rateLimit, refreshPeriodSeconds);
    }

    /**
     * Endpoint for receiving onboarding notifications
     * Rate limited to prevent abuse
     * 
     * @param request the notification request containing a hash
     * @throws RuntimeException if rate limit is exceeded
     */
    @PostMapping("/notification")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveNotification(@Valid @RequestBody NotificationRequest request) {
        // Rate limiting logic
        if (!checkRateLimit()) {
            log.warn("Rate limit exceeded for notification request with hash: {}", request.getHash());
            rateExceededCounter.increment();
            throw new RuntimeException("Rate limit exceeded. Please try again later.");
        }
        
        log.info("Received onboarding notification with hash: {}", request.getHash());
        notificationCounter.increment();
        
        // Process the hash
        onboardingProcessorService.processHash(request.getHash());
    }
    
    /**
     * Check if the current request is within the rate limit
     * 
     * @return true if request is within rate limit, false otherwise
     */
    private boolean checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastResetTime.get();
        
        // Reset counter if refresh period has passed
        if (timeElapsed > refreshPeriodSeconds * 1000L) {
            requestCount.set(0);
            lastResetTime.set(currentTime);
        }
        
        // Check if under limit and increment
        return requestCount.incrementAndGet() <= rateLimit;
    }
    
    /**
     * Get the status of an onboarding process by hash
     * 
     * @param hash the unique hash identifier
     * @return status response with processing details
     */
    @GetMapping("/status/{hash}")
    public ResponseEntity<StatusResponse> getStatus(@PathVariable String hash) {
        log.info("Checking status for hash: {}", hash);
        
        Optional<OnboardingData> optionalData = onboardingDataRepository.findByHash(hash);
        
        if (optionalData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        OnboardingData data = optionalData.get();
        int errorCount = validationErrorRepository.findByOnboardingData(data).size();
        
        StatusResponse response = StatusResponse.builder()
                .hash(data.getHash())
                .status(data.getStatus())
                .dataRecebimento(data.getDataRecebimento())
                .dataProcessamento(data.getDataProcessamento())
                .dataPersistencia(data.getDataPersistencia())
                .errosValidacao(errorCount)
                .timestamp(LocalDateTime.now().format(formatter))
                .user("tenissonjr")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get validation errors for an onboarding process by hash
     * 
     * @param hash the unique hash identifier
     * @return validation errors response with details
     */
    @GetMapping("/validations/{hash}")
    public ResponseEntity<ValidationErrorResponse> getValidationErrors(@PathVariable String hash) {
        log.info("Retrieving validation errors for hash: {}", hash);
        
        Optional<OnboardingData> optionalData = onboardingDataRepository.findByHash(hash);
        
        if (optionalData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<ValidationError> errors = validationErrorRepository.findByOnboardingDataHash(hash);
        
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .hash(hash)
                .erros(errors.stream()
                        .map(error -> ValidationErrorResponse.ErrorDetail.builder()
                                .campo(error.getCampo())
                                .mensagem(error.getMensagem())
                                .dataRegistro(error.getDataRegistro())
                                .build())
                        .collect(Collectors.toList()))
                .timestamp(LocalDateTime.now().format(formatter))
                .user("tenissonjr")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Internal endpoint for system health check
     * 
     * @return 200 OK if system is operational
     */
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck() {
        return "System operational - " + LocalDateTime.now().format(formatter);
    }
}

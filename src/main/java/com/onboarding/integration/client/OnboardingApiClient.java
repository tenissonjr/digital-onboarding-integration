package com.onboarding.integration.client;

import com.onboarding.integration.dto.OnboardingDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnboardingApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${app.onboarding.api.url}")
    private String apiUrl;
    
    @Value("${app.onboarding.api.username}")
    private String username;
    
    @Value("${app.onboarding.api.password}")
    private String password;
    
    @Value("${app.retry.max-attempts}")
    private int maxAttempts;

    public OnboardingDataDto fetchOnboardingData(String hash) {
        log.info("Buscando dados de onboarding para o hash: {}", hash);
        
        String url = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .path("/api/v1/onboarding/data/{hash}")
                .buildAndExpand(hash)
                .toUriString();
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Manual retry implementation
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxAttempts) {
            try {
                ResponseEntity<OnboardingDataDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        requestEntity,
                        OnboardingDataDto.class
                );
                
                log.info("Dados de onboarding obtidos com sucesso para o hash: {}", hash);
                return response.getBody();
            } catch (Exception e) {
                lastException = e;
                log.warn("Tentativa {} falhou ao buscar dados para hash {}: {}", 
                         (attempts + 1), hash, e.getMessage());
                attempts++;
                
                // Simple delay between retries
                try {
                    Thread.sleep(1000 * attempts); // Exponential back-off: 1s, 2s, 3s...
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during retry delay", ie);
                }
            }
        }
        
        log.error("Todas as {} tentativas falharam ao buscar dados para hash: {}", maxAttempts, hash);
        throw new RuntimeException("Falha ao obter dados de onboarding após " + maxAttempts + " tentativas", lastException);
    }
    
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }
}
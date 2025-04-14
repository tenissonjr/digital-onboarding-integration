package com.onboarding.config;

import com.onboarding.util.SimpleCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {
    
    @Bean
    public SimpleCircuitBreaker onboardingApiCircuitBreaker() {
        return new SimpleCircuitBreaker(5, 30000); // 5 failures, 30 second timeout
    }
}
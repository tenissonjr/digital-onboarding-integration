package com.onboarding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .disable())
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints públicos (sem autenticação)
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/onboarding/notification")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/stats")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/statsbytime")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
                // H2 Console
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                // Endpoints internos (sem autenticação para desenvolvimento)
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/onboarding/status/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/onboarding/validations/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/**")).permitAll()
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                // Allow H2 Console to be displayed in frame (needed for H2 console)
                .frameOptions(frame -> frame.sameOrigin())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }
}
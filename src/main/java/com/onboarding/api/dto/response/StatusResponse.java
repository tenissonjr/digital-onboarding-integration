package com.onboarding.api.dto.response;

import com.onboarding.domain.model.OnboardingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    
    private String hash;
    private OnboardingStatus status;
    private LocalDateTime dataRecebimento;
    private LocalDateTime dataProcessamento;
    private LocalDateTime dataPersistencia;
    private Integer errosValidacao;
    private String timestamp;
    private String user;
}
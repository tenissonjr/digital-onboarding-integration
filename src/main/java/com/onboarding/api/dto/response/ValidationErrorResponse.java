package com.onboarding.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    
    private String hash;
    private List<ErrorDetail> erros;
    private String timestamp;
    private String user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String campo;
        private String mensagem;
        private LocalDateTime dataRegistro;
    }
}
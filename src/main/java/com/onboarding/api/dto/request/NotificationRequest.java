package com.onboarding.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "Hash é obrigatório")
    @Size(min = 32, max = 64, message = "Hash deve ter entre 32 e 64 caracteres")
    private String hash;
}
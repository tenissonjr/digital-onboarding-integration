package com.onboarding.config;

import com.onboarding.integration.dto.OnboardingDataDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Configuration
@Profile("dev")
public class MockConfig {
    
    @Bean
    public MockOnboardingApiController mockOnboardingApiController() {
        return new MockOnboardingApiController();
    }
    
    @RestController
    public static class MockOnboardingApiController {
        
        @GetMapping("/api/v1/onboarding/data/{hash}")
        public OnboardingDataDto getMockData(@PathVariable String hash) {
            OnboardingDataDto.DocumentoImagensDto imagens = OnboardingDataDto.DocumentoImagensDto.builder()
                .fotoUsuario("base64encoded_photo_data")
                .documentoFrente("base64encoded_doc_front")
                .documentoVerso("base64encoded_doc_back")
                .build();
            
            return OnboardingDataDto.builder()
                .hash(hash)
                .cpf("12345678909")
                .nome("Usuário Teste")
                .nomeSocial("Nome Social Teste")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .nomeMae("Mãe do Usuário Teste")
                .numeroDocumento("123456789")
                .paisOrigem("Brasil")
                .orgaoEmissor("SSP")
                .uf("SP")
                .dataExpedicao(LocalDate.of(2015, 1, 1))
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .imagens(imagens)
                .build();
        }
    }
}
package com.onboarding.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDataDto {
    
    private String hash;
    private String cpf;
    private String nome;
    private String nomeSocial;
    private LocalDate dataNascimento;
    private String nomeMae;
    private String numeroDocumento;
    private String paisOrigem;
    private String orgaoEmissor;
    private String uf;
    private LocalDate dataExpedicao;
    private LocalDate dataVencimento;
    private DocumentoImagensDto imagens;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoImagensDto {
        private String fotoUsuario;
        private String documentoFrente;
        private String documentoVerso;
    }
}
package com.onboarding.service;

import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.entity.ValidationError;
import com.onboarding.domain.model.OnboardingStatus;
import com.onboarding.domain.repository.OnboardingDataRepository;
import com.onboarding.domain.repository.ValidationErrorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final OnboardingDataRepository onboardingDataRepository;
    private final ValidationErrorRepository validationErrorRepository;

    @Transactional
    public void validateData(String hash) {
        log.info("Validando dados para o hash: {}", hash);
        
        Optional<OnboardingData> optionalData = onboardingDataRepository.findByHash(hash);
        
        if (optionalData.isEmpty()) {
            log.error("Dados não encontrados para o hash: {}", hash);
            throw new IllegalArgumentException("Dados não encontrados para o hash: " + hash);
        }
        
        OnboardingData data = optionalData.get();
        List<ValidationError> errors = new ArrayList<>();
        
        // Validar CPF
        if (!isValidCpf(data.getCpf())) {
            errors.add(createError(data, "cpf", "CPF inválido"));
        }
        
        // Verificar maioridade
        if (data.getDataNascimento() != null && !isAdult(data.getDataNascimento())) {
            errors.add(createError(data, "dataNascimento", "O usuário deve ter pelo menos 18 anos"));
        }
        
        // Verificar data de validade do documento
        if (data.getDataVencimento() != null && data.getDataVencimento().isBefore(LocalDate.now())) {
            errors.add(createError(data, "dataVencimento", "Documento vencido"));
        }
        
        // Validar se a data de expedição é anterior à data atual
        if (data.getDataExpedicao() != null && data.getDataExpedicao().isAfter(LocalDate.now())) {
            errors.add(createError(data, "dataExpedicao", "Data de expedição não pode ser futura"));
        }
        
        // Validar nome (não pode estar em branco)
        if (data.getNome() == null || data.getNome().trim().isEmpty()) {
            errors.add(createError(data, "nome", "Nome não pode estar em branco"));
        }
        
        // Validar nome da mãe (não pode estar em branco)
        if (data.getNomeMae() == null || data.getNomeMae().trim().isEmpty()) {
            errors.add(createError(data, "nomeMae", "Nome da mãe não pode estar em branco"));
        }
        
        // Validar número do documento
        if (data.getNumeroDocumento() == null || data.getNumeroDocumento().trim().isEmpty()) {
            errors.add(createError(data, "numeroDocumento", "Número do documento não pode estar em branco"));
        }
        
        // Atualizar status e adicionar erros
        if (errors.isEmpty()) {
            data.setStatus(OnboardingStatus.VALIDADO_SUCESSO);
            log.info("Validação concluída para o hash {} sem erros", hash);
        } else {
            data.setStatus(OnboardingStatus.VALIDACAO_COM_ERROS);
            for (ValidationError error : errors) {
                validationErrorRepository.save(error);
            }
            log.info("Validação concluída para o hash {} com {} erros", hash, errors.size());
        }
        
        data.setDataProcessamento(java.time.LocalDateTime.now());
        onboardingDataRepository.save(data);
    }
    
    private ValidationError createError(OnboardingData data, String field, String message) {
        return ValidationError.builder()
                .onboardingData(data)
                .campo(field)
                .mensagem(message)
                .dataRegistro(LocalDateTime.now())
                .createdBy("tenissonjr")
                .build();
    }
    
    private boolean isValidCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        // Remove non-numeric characters
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same
        boolean allDigitsEqual = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                allDigitsEqual = false;
                break;
            }
        }
        if (allDigitsEqual) {
            return false;
        }
        
        // Calculate verification digits
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int remainder = sum % 11;
        int digit1 = remainder < 2 ? 0 : 11 - remainder;
        
        if (Character.getNumericValue(cpf.charAt(9)) != digit1) {
            return false;
        }
        
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = remainder < 2 ? 0 : 11 - remainder;
        
        return Character.getNumericValue(cpf.charAt(10)) == digit2;
    }
    
    private boolean isAdult(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}
package com.onboarding.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "validation_error")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_data_id", nullable = false)
    private OnboardingData onboardingData;

    @Column(nullable = false)
    private String campo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy = "tenissonjr"; // Current user
        if (dataRegistro == null) {
            dataRegistro = LocalDateTime.now();
        }
    }
}
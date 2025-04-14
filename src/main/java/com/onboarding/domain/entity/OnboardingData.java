package com.onboarding.domain.entity;

import com.onboarding.domain.model.OnboardingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "onboarding_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String hash;

    @Column(length = 14)
    private String cpf;

    @Column(length = 200)
    private String nome;

    @Column(name = "nome_social", length = 200)
    private String nomeSocial;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "nome_mae", length = 200)
    private String nomeMae;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(name = "pais_origem", length = 100)
    private String paisOrigem;

    @Column(name = "orgao_emissor", length = 10)
    private String orgaoEmissor;

    @Column(length = 2)
    private String uf;

    @Column(name = "data_expedicao")
    private LocalDate dataExpedicao;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status;

    @Column(name = "data_recebimento", nullable = false)
    private LocalDateTime dataRecebimento;

    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;

    @Column(name = "data_persistencia")
    private LocalDateTime dataPersistencia;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "onboarding_data_id")
    private List<ValidationError> validationErrors = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy = "tenissonjr"; // Current user
        dataRecebimento = LocalDateTime.now();
        if (status == null) {
            status = OnboardingStatus.EM_PROCESSAMENTO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataPersistencia = LocalDateTime.now();
    }
}
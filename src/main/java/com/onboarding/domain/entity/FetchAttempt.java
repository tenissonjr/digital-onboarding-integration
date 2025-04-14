package com.onboarding.domain.entity;

import com.onboarding.domain.model.FetchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fetch_attempt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hash;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FetchStatus status;

    @Column(name = "mensagem_erro", length = 1000)
    private String mensagemErro;

    @Column(nullable = false)
    private Integer tentativa;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy = "tenissonjr"; // Current user
        dataHora = LocalDateTime.now();
    }
}
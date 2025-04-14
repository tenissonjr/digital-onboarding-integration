package com.onboarding.domain.repository;

import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.model.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OnboardingDataRepository extends JpaRepository<OnboardingData, Long> {
    
    Optional<OnboardingData> findByHash(String hash);
    
    boolean existsByHash(String hash);
    
    long countByStatus(OnboardingStatus status);
    
    long countByStatusAndCreatedAtAfter(OnboardingStatus status, LocalDateTime createdAt);
    
    long countByStatusAndCreatedAtBetween(OnboardingStatus status, LocalDateTime startTime, LocalDateTime endTime);
}
package com.onboarding.domain.repository;

import com.onboarding.domain.entity.OnboardingData;
import com.onboarding.domain.entity.ValidationError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidationErrorRepository extends JpaRepository<ValidationError, Long> {
    
    List<ValidationError> findByOnboardingData(OnboardingData onboardingData);
    
    @Query("SELECT v FROM ValidationError v WHERE v.onboardingData.hash = :hash")
    List<ValidationError> findByOnboardingDataHash(@Param("hash") String hash);
}
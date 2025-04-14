package com.onboarding.domain.repository;

import com.onboarding.domain.entity.FetchAttempt;
import com.onboarding.domain.model.FetchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FetchAttemptRepository extends JpaRepository<FetchAttempt, Long> {
    
    List<FetchAttempt> findByHashOrderByDataHoraDesc(String hash);
    
    @Query("SELECT MAX(fa.tentativa) FROM FetchAttempt fa WHERE fa.hash = :hash")
    Optional<Integer> findMaxAttemptByHash(String hash);
    
    long countByStatus(FetchStatus status);
    
    long countByStatusAndCreatedAtAfter(FetchStatus status, LocalDateTime createdAt);
    
    long countByStatusAndCreatedAtBetween(FetchStatus status, LocalDateTime startTime, LocalDateTime endTime);
}
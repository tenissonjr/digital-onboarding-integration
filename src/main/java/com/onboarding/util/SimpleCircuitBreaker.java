package com.onboarding.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SimpleCircuitBreaker {
    private static final int DEFAULT_FAILURE_THRESHOLD = 5;
    private static final long DEFAULT_RESET_TIMEOUT_MS = 30000; // 30 seconds
    
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }
    
    @Getter
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile long lastFailureTime;
    
    private final int failureThreshold;
    private final long resetTimeoutMs;
    
    public SimpleCircuitBreaker() {
        this(DEFAULT_FAILURE_THRESHOLD, DEFAULT_RESET_TIMEOUT_MS);
    }
    
    public SimpleCircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }
    
    public <T> T execute(Callable<T> operation) throws Exception {
        if (isOpen()) {
            if (canAttemptReset()) {
                log.info("Circuit breaker in half-open state. Attempting reset.");
                state = State.HALF_OPEN;
            } else {
                log.warn("Circuit breaker is open. Rejecting request.");
                throw new RuntimeException("Circuit Breaker is open");
            }
        }
        
        try {
            T result = operation.call();
            
            // If we get here in HALF_OPEN, the call was successful, so we can reset
            if (state == State.HALF_OPEN) {
                reset();
            }
            
            return result;
        } catch (Exception e) {
            handleFailure();
            throw e;
        }
    }
    
    private boolean isOpen() {
        return state == State.OPEN;
    }
    
    private boolean canAttemptReset() {
        return System.currentTimeMillis() - lastFailureTime >= resetTimeoutMs;
    }
    
    private void handleFailure() {
        lastFailureTime = System.currentTimeMillis();
        
        if (state == State.HALF_OPEN) {
            // If we fail during half-open state, go back to open
            tripBreaker();
            return;
        }
        
        if (failureCount.incrementAndGet() >= failureThreshold) {
            tripBreaker();
        }
    }
    
    private void tripBreaker() {
        state = State.OPEN;
        log.warn("Circuit breaker tripped to OPEN state");
    }
    
    private void reset() {
        state = State.CLOSED;
        failureCount.set(0);
        log.info("Circuit breaker reset to CLOSED state");
    }
}
package com.onboarding.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Handled Runtime Exception: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("message", ex.getMessage());
        body.put("user", "tenissonjr");
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Check for rate limit errors
        if (ex.getMessage() != null && ex.getMessage().contains("Rate limit exceeded")) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        }
        
        return new ResponseEntity<>(body, status);
    }
}
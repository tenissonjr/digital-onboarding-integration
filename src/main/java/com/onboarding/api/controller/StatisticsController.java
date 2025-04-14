package com.onboarding.api.controller;

import com.onboarding.api.dto.response.StatisticsResponse;
import com.onboarding.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse stats = statisticsService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
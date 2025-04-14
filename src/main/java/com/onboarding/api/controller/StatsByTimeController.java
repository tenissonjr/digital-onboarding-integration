package com.onboarding.api.controller;

import com.onboarding.api.dto.request.StatsByTimeRequest;
import com.onboarding.api.dto.response.StatsByTimeResponse;
import com.onboarding.service.StatsByTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statsbytime")
@RequiredArgsConstructor
public class StatsByTimeController {

    private final StatsByTimeService statsByTimeService;

    @PostMapping
    public ResponseEntity<StatsByTimeResponse> getStatsByTime(@Valid @RequestBody StatsByTimeRequest request) {
        StatsByTimeResponse response = statsByTimeService.getStatsByTime(request);
        return ResponseEntity.ok(response);
    }
}
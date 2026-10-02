package com.elanor.analytics.controller;

import com.elanor.analytics.dto.RecordAnalyticsEventRequest;
import com.elanor.analytics.service.AnalyticsService;
import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Void>> recordEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RecordAnalyticsEventRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        analyticsService.recordEvent(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Event captured successfully"));
    }
}

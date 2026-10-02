package com.elanor.analytics.controller;

import com.elanor.analytics.dto.AnalyticsOverviewResponse;
import com.elanor.analytics.service.AnalyticsService;
import com.elanor.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    public AdminAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> getOverview() {
        AnalyticsOverviewResponse response = analyticsService.getOverview();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

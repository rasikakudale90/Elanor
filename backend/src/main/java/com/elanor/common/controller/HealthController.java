package com.elanor.common.controller;

import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Application health and status endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check backend service health")
    public ApiResponse<Map<String, String>> checkHealth() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "service", "elanor-backend",
                "version", "1.0.0"
        ), "Service is healthy");
    }
}

package com.elanor.shipping.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.shipping.dto.ShipmentResponse;
import com.elanor.shipping.dto.TrackingResponse;
import com.elanor.shipping.service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ApiResponse<TrackingResponse>> getTracking(@PathVariable String trackingNumber) {
        TrackingResponse response = shippingService.getTrackingByNumber(trackingNumber);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getOrderShipments(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserPrincipal principal) {

        UUID userId = principal != null ? principal.getId() : null;
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        List<ShipmentResponse> response = shippingService.getShipmentsByOrderId(orderId, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

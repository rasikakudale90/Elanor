package com.elanor.shipping.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.shipping.dto.AddShipmentEventRequest;
import com.elanor.shipping.dto.CreateShipmentRequest;
import com.elanor.shipping.dto.ShipmentResponse;
import com.elanor.shipping.service.ShippingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/shipments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminShippingController {

    private final ShippingService shippingService;

    public AdminShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        ShipmentResponse response = shippingService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Shipment created successfully"));
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<ApiResponse<ShipmentResponse>> addShipmentEvent(
            @PathVariable UUID id,
            @Valid @RequestBody AddShipmentEventRequest request) {
        ShipmentResponse response = shippingService.addShipmentEvent(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Shipment event recorded successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShipmentResponse>>> getAllShipments(Pageable pageable) {
        Page<ShipmentResponse> response = shippingService.getAllShipments(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable UUID id) {
        ShipmentResponse response = shippingService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

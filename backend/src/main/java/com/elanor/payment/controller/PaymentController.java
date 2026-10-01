package com.elanor.payment.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.payment.dto.DemoPaymentResultRequest;
import com.elanor.payment.dto.InitiatePaymentRequest;
import com.elanor.payment.dto.PaymentDto;
import com.elanor.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment Initiation and Sandbox Outcome Simulation APIs")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment attempt against an order")
    public ResponseEntity<ApiResponse<PaymentDto>> initiatePayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody InitiatePaymentRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        PaymentDto payment = paymentService.initiatePayment(request, userId, isAdmin, idempotencyKey);
        return new ResponseEntity<>(ApiResponse.ok(payment, "Payment initiated"), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/demo-result")
    @Operation(summary = "Complete demo payment with simulated Success or Failure outcome")
    public ResponseEntity<ApiResponse<PaymentDto>> processDemoResult(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) DemoPaymentResultRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        PaymentDto result = paymentService.processDemoResult(id, request, userId, isAdmin);
        String message = result.getStatus().name().equals("SUCCESSFUL")
                ? "Payment successful! Order is now confirmed."
                : "Payment failed. Please retry payment.";
        return ResponseEntity.ok(ApiResponse.ok(result, message));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment transaction details by ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.getId() : null;
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        PaymentDto payment = paymentService.getPaymentById(id, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get all payment attempts for a specific order")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID orderId) {
        UUID userId = principal != null ? principal.getId() : null;
        boolean isAdmin = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        List<PaymentDto> payments = paymentService.getPaymentsForOrder(orderId, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }
}

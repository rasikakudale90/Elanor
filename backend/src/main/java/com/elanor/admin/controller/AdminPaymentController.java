package com.elanor.admin.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.payment.dto.PaymentDto;
import com.elanor.payment.entity.PaymentStatus;
import com.elanor.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/payments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Payments", description = "Administrative Payment Oversight and Cash on Delivery Confirmation")
public class AdminPaymentController {

    private final PaymentService paymentService;

    public AdminPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @Operation(summary = "Get paginated payment transactions with optional status and search filter")
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getPayments(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentDto> payments = paymentService.getAllPaymentsForAdmin(status, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed payment transaction by ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable UUID id) {
        PaymentDto payment = paymentService.getPaymentById(id, null, true);
        return ResponseEntity.ok(ApiResponse.ok(payment));
    }

    @PostMapping("/{id}/cod-collect")
    @Operation(summary = "Confirm collection of Cash on Delivery payment after delivery")
    public ResponseEntity<ApiResponse<PaymentDto>> confirmCodCollected(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        String actor = principal != null ? principal.getUsername() : "ADMIN";
        PaymentDto updated = paymentService.confirmCodPaymentCollected(id, actor);
        return ResponseEntity.ok(ApiResponse.ok(updated, "COD payment marked as successfully collected."));
    }
}

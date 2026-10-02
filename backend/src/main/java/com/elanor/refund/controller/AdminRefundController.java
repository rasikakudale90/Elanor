package com.elanor.refund.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.refund.dto.ProcessRefundRequest;
import com.elanor.refund.dto.RefundResponse;
import com.elanor.refund.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminRefundController {

    private final RefundService refundService;

    public AdminRefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponse>> processRefund(
            @Valid @RequestBody ProcessRefundRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String adminActor = principal != null ? principal.getEmail() : "ADMIN";
        RefundResponse response = refundService.processRefund(request, adminActor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Refund processed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RefundResponse>>> getAllRefunds(Pageable pageable) {
        Page<RefundResponse> response = refundService.getAllRefunds(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getRefundsByOrderId(@PathVariable UUID orderId) {
        List<RefundResponse> response = refundService.getOrderRefunds(orderId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

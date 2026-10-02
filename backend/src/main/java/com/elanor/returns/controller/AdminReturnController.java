package com.elanor.returns.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.returns.dto.ReturnRequestResponse;
import com.elanor.returns.dto.UpdateReturnStatusRequest;
import com.elanor.returns.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/returns")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminReturnController {

    private final ReturnService returnService;

    public AdminReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReturnRequestResponse>>> getAllReturns(Pageable pageable) {
        Page<ReturnRequestResponse> response = returnService.getAllReturns(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> getReturnById(@PathVariable UUID id) {
        ReturnRequestResponse response = returnService.getReturnById(id, null, true);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> updateReturnStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReturnStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String adminActor = principal != null ? principal.getEmail() : "ADMIN";
        ReturnRequestResponse response = returnService.updateReturnStatus(id, request, adminActor);
        return ResponseEntity.ok(ApiResponse.ok(response, "Return request status updated successfully"));
    }
}

package com.elanor.returns.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.returns.dto.CreateReturnRequestDto;
import com.elanor.returns.dto.ReturnRequestResponse;
import com.elanor.returns.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> createReturnRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReturnRequestDto request) {
        ReturnRequestResponse response = returnService.createReturnRequest(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Return request submitted successfully"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReturnRequestResponse>>> getMyReturns(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ReturnRequestResponse> response = returnService.getCustomerReturns(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> getReturnById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        ReturnRequestResponse response = returnService.getReturnById(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

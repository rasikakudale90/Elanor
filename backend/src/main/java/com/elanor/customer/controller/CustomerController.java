package com.elanor.customer.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.customer.dto.*;
import com.elanor.customer.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Authenticated Customer Profile and Address Management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current customer profile")
    public ResponseEntity<ApiResponse<CustomerProfileDto>> getCurrentProfile(@AuthenticationPrincipal UserPrincipal principal) {
        CustomerProfileDto profile = customerService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current customer profile")
    public ResponseEntity<ApiResponse<CustomerProfileDto>> updateCurrentProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        CustomerProfileDto updated = customerService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Profile updated successfully"));
    }

    @GetMapping("/me/addresses")
    @Operation(summary = "Get current customer addresses")
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAddresses(@AuthenticationPrincipal UserPrincipal principal) {
        List<AddressDto> addresses = customerService.getAddresses(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(addresses));
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Add a new customer address")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAddressRequest request) {
        AddressDto address = customerService.createAddress(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.ok(address, "Address added successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/me/addresses/{id}")
    @Operation(summary = "Update an existing customer address")
    public ResponseEntity<ApiResponse<AddressDto>> updateAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAddressRequest request) {
        AddressDto updated = customerService.updateAddress(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Address updated successfully"));
    }

    @DeleteMapping("/me/addresses/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        customerService.deleteAddress(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted successfully"));
    }
}

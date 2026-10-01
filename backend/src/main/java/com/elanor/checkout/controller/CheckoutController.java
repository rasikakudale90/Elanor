package com.elanor.checkout.controller;

import com.elanor.checkout.dto.*;
import com.elanor.checkout.service.CheckoutService;
import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
@Tag(name = "Checkout", description = "Checkout Validation, Shipping Quotes, and Order Creation APIs")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/quote")
    @Operation(summary = "Get dynamic shipping quote and estimated delivery timeframe")
    public ResponseEntity<ApiResponse<ShippingQuoteResponse>> getShippingQuote(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @RequestBody(required = false) ShippingQuoteRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        ShippingQuoteResponse quote = checkoutService.getShippingQuote(request, userId, guestToken);
        return ResponseEntity.ok(ApiResponse.ok(quote));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate cart items, available stock, address, and coupon prior to placing order")
    public ResponseEntity<ApiResponse<CheckoutValidateResponse>> validateCheckout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @RequestBody(required = false) CheckoutValidateRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        CheckoutValidateResponse validation = checkoutService.validateCheckout(request, userId, guestToken);
        return ResponseEntity.ok(ApiResponse.ok(validation));
    }

    @PostMapping("/order")
    @Operation(summary = "Place order, create immutable item/address snapshots, reserve inventory, and initialize payment")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody(required = false) CreateOrderRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        CheckoutResponse response = checkoutService.createOrder(request, userId, guestToken, idempotencyKey);
        return new ResponseEntity<>(ApiResponse.ok(response, "Order placed successfully"), HttpStatus.CREATED);
    }
}

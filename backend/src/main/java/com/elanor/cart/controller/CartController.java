package com.elanor.cart.controller;

import com.elanor.cart.dto.*;
import com.elanor.cart.service.CartService;
import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Customer and Guest Cart Management APIs")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get cart with calculated totals for authenticated customer or guest token")
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken) {
        UUID userId = principal != null ? principal.getId() : null;
        CartDto cart = cartService.getCartDto(userId, guestToken);
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @Valid @RequestBody AddToCartRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        CartDto updated = cartService.addItem(userId, guestToken, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Item added to cart"));
    }

    @PatchMapping("/items/{variantId}")
    @Operation(summary = "Update item quantity in cart")
    public ResponseEntity<ApiResponse<CartDto>> updateItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        CartDto updated = cartService.updateItem(userId, guestToken, variantId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Cart updated"));
    }

    @DeleteMapping("/items/{variantId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @PathVariable UUID variantId) {
        UUID userId = principal != null ? principal.getId() : null;
        CartDto updated = cartService.removeItem(userId, guestToken, variantId);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Item removed from cart"));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart into customer cart upon login")
    public ResponseEntity<ApiResponse<CartDto>> mergeCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MergeCartRequest request) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        CartDto merged = cartService.mergeCart(principal.getId(), request.getGuestToken());
        return ResponseEntity.ok(ApiResponse.ok(merged, "Cart merged successfully"));
    }
}

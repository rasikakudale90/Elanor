package com.elanor.wishlist.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.wishlist.dto.AddWishlistItemRequest;
import com.elanor.wishlist.dto.MergeWishlistRequest;
import com.elanor.wishlist.dto.WishlistDto;
import com.elanor.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@Tag(name = "Wishlist", description = "Customer and Guest Wishlist APIs")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get wishlist for authenticated customer or guest token")
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken) {
        UUID userId = principal != null ? principal.getId() : null;
        WishlistDto wishlist = wishlistService.getWishlistDto(userId, guestToken);
        return ResponseEntity.ok(ApiResponse.ok(wishlist));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to wishlist")
    public ResponseEntity<ApiResponse<WishlistDto>> addItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @Valid @RequestBody AddWishlistItemRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        WishlistDto updated = wishlistService.addItem(userId, guestToken, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Item added to wishlist"));
    }

    @DeleteMapping("/items/{variantId}")
    @Operation(summary = "Remove item from wishlist")
    public ResponseEntity<ApiResponse<WishlistDto>> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @PathVariable UUID variantId) {
        UUID userId = principal != null ? principal.getId() : null;
        WishlistDto updated = wishlistService.removeItem(userId, guestToken, variantId);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Item removed from wishlist"));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest wishlist into authenticated customer wishlist upon login")
    public ResponseEntity<ApiResponse<WishlistDto>> mergeWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MergeWishlistRequest request) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        WishlistDto merged = wishlistService.mergeWishlist(principal.getId(), request.getGuestToken());
        return ResponseEntity.ok(ApiResponse.ok(merged, "Wishlist merged successfully"));
    }
}

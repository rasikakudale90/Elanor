package com.elanor.coupon.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.coupon.dto.CouponDto;
import com.elanor.coupon.dto.CreateCouponRequest;
import com.elanor.coupon.dto.UpdateCouponRequest;
import com.elanor.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Coupons", description = "Backoffice coupon and promotion management")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    @Operation(summary = "List coupons with pagination and search")
    public ResponseEntity<ApiResponse<Page<CouponDto>>> listCoupons(
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CouponDto> coupons = couponService.getAllCoupons(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(coupons));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon details by ID")
    public ResponseEntity<ApiResponse<CouponDto>> getCouponById(@PathVariable UUID id) {
        CouponDto coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.ok(coupon));
    }

    @PostMapping
    @Operation(summary = "Create a new coupon code")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(
            @Valid @RequestBody CreateCouponRequest request) {
        CouponDto coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(coupon, "Coupon created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing coupon")
    public ResponseEntity<ApiResponse<CouponDto>> updateCoupon(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCouponRequest request) {
        CouponDto coupon = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.ok(coupon, "Coupon updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a coupon")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok("Coupon deleted successfully"));
    }
}

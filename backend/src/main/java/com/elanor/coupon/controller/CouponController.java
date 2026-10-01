package com.elanor.coupon.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.coupon.dto.CouponDto;
import com.elanor.coupon.dto.CouponValidationResponse;
import com.elanor.coupon.dto.ValidateCouponRequest;
import com.elanor.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupons", description = "Public & customer coupon validation and active promotions")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon code against order subtotal")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @Valid @RequestBody ValidateCouponRequest request) {
        CouponValidationResponse response = couponService.validateCoupon(request.getCode(), request.getOrderAmount());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get list of active public coupons and promo offers")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getActiveCoupons() {
        List<CouponDto> activeCoupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(ApiResponse.ok(activeCoupons));
    }
}

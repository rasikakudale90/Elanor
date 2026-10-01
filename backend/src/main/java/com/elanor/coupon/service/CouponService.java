package com.elanor.coupon.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.exception.ResourceNotFoundException;
import com.elanor.coupon.dto.*;
import com.elanor.coupon.entity.Coupon;
import com.elanor.coupon.entity.DiscountType;
import com.elanor.coupon.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(String code, BigDecimal orderAmount) {
        if (code == null || code.trim().isEmpty()) {
            return CouponValidationResponse.invalid(code, "Coupon code is required");
        }

        String normalizedCode = code.trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeIgnoreCase(normalizedCode)
                .orElse(null);

        if (coupon == null) {
            return CouponValidationResponse.invalid(normalizedCode, "Coupon code not found");
        }

        if (!coupon.isActive()) {
            return CouponValidationResponse.invalid(normalizedCode, "This coupon is no longer active");
        }

        Instant now = Instant.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return CouponValidationResponse.invalid(normalizedCode, "Coupon is not yet active");
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return CouponValidationResponse.invalid(normalizedCode, "Coupon has expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return CouponValidationResponse.invalid(normalizedCode, "Coupon usage limit has been reached");
        }

        BigDecimal subtotal = orderAmount != null ? orderAmount : BigDecimal.ZERO;

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            return CouponValidationResponse.invalid(normalizedCode,
                    "Minimum order subtotal of ₹" + coupon.getMinOrderAmount() + " required to use this coupon");
        }

        BigDecimal discountAmount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = subtotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        } else {
            discountAmount = coupon.getDiscountValue();
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }

        BigDecimal finalAmount = subtotal.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return CouponValidationResponse.valid(
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                discountAmount,
                finalAmount,
                "Coupon applied successfully"
        );
    }

    @Transactional(readOnly = true)
    public List<CouponDto> getActiveCoupons() {
        return couponRepository.findActiveCoupons(Instant.now())
                .stream()
                .map(CouponDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CouponDto> getAllCoupons(String search, Pageable pageable) {
        return couponRepository.findAllWithFilter(search, pageable)
                .map(CouponDto::new);
    }

    @Transactional(readOnly = true)
    public CouponDto getCouponById(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        return new CouponDto(coupon);
    }

    @Transactional
    public CouponDto createCoupon(CreateCouponRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Coupon code already exists: " + code);
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Start date must be before end date");
        }

        Coupon coupon = new Coupon(
                code,
                request.getDescription(),
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getMinOrderAmount(),
                request.getMaxDiscountAmount(),
                request.getStartDate(),
                request.getEndDate(),
                request.getUsageLimit(),
                request.getIsActive() != null ? request.getIsActive() : true
        );

        Coupon saved = couponRepository.save(coupon);
        log.info("Created coupon {} with id {}", saved.getCode(), saved.getId());
        return new CouponDto(saved);
    }

    @Transactional
    public CouponDto updateCoupon(UUID id, UpdateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));

        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getDiscountType() != null) coupon.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderAmount() != null) coupon.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getMaxDiscountAmount() != null) coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) coupon.setEndDate(request.getEndDate());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getIsActive() != null) coupon.setActive(request.getIsActive());

        if (coupon.getStartDate().isAfter(coupon.getEndDate())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Start date must be before end date");
        }

        Coupon updated = couponRepository.save(coupon);
        log.info("Updated coupon {}", updated.getCode());
        return new CouponDto(updated);
    }

    @Transactional
    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
        log.info("Deleted coupon with id {}", id);
    }

    @Transactional
    public void incrementUsage(String code) {
        couponRepository.findByCodeIgnoreCase(code.trim().toUpperCase())
                .ifPresent(coupon -> {
                    coupon.setUsageCount(coupon.getUsageCount() + 1);
                    couponRepository.save(coupon);
                    log.info("Incremented usage count for coupon {} to {}", coupon.getCode(), coupon.getUsageCount());
                });
    }
}

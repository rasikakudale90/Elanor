package com.elanor;

import com.elanor.coupon.dto.CreateCouponRequest;
import com.elanor.coupon.dto.ValidateCouponRequest;
import com.elanor.coupon.entity.Coupon;
import com.elanor.coupon.entity.DiscountType;
import com.elanor.coupon.repository.CouponRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();

        // 1. Percentage coupon (15% off, max 500, min order 1000)
        Coupon pctCoupon = new Coupon(
                "GLOW15",
                "15% off on orders above 1000",
                DiscountType.PERCENTAGE,
                BigDecimal.valueOf(15.00),
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(500.00),
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(30, ChronoUnit.DAYS),
                100,
                true
        );
        couponRepository.save(pctCoupon);

        // 2. Fixed amount coupon (₹200 off, min order 500)
        Coupon flatCoupon = new Coupon(
                "FLAT200",
                "Flat ₹200 off",
                DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(500.00),
                null,
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(30, ChronoUnit.DAYS),
                null,
                true
        );
        couponRepository.save(flatCoupon);

        // 3. Expired coupon
        Coupon expCoupon = new Coupon(
                "EXPIRED10",
                "Expired coupon",
                DiscountType.PERCENTAGE,
                BigDecimal.valueOf(10.00),
                BigDecimal.ZERO,
                null,
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().minus(1, ChronoUnit.DAYS),
                10,
                true
        );
        couponRepository.save(expCoupon);
    }

    @Test
    @DisplayName("Validate percentage coupon GLOW15 on 2000 order -> ₹300 discount")
    void testValidatePercentageCouponSuccess() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("GLOW15", BigDecimal.valueOf(2000.00));

        mockMvc.perform(post("/api/v1/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.code").value("GLOW15"))
                .andExpect(jsonPath("$.data.discountAmount").value(300.00))
                .andExpect(jsonPath("$.data.finalAmount").value(1700.00));
    }

    @Test
    @DisplayName("Validate percentage coupon GLOW15 below minOrderAmount -> invalid")
    void testValidateCouponBelowMinOrder() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("GLOW15", BigDecimal.valueOf(800.00));

        mockMvc.perform(post("/api/v1/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("Minimum order subtotal of ₹1000.00 required")));
    }

    @Test
    @DisplayName("Validate flat amount coupon FLAT200 on 1000 order -> ₹200 discount")
    void testValidateFlatCouponSuccess() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("FLAT200", BigDecimal.valueOf(1000.00));

        mockMvc.perform(post("/api/v1/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.discountAmount").value(200.00))
                .andExpect(jsonPath("$.data.finalAmount").value(800.00));
    }

    @Test
    @DisplayName("Validate expired coupon EXPIRED10 -> invalid")
    void testValidateExpiredCoupon() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("EXPIRED10", BigDecimal.valueOf(1000.00));

        mockMvc.perform(post("/api/v1/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.message").value("Coupon has expired"));
    }

    @Test
    @DisplayName("Get active coupons list returns only non-expired active coupons")
    void testGetActiveCoupons() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Admin creates a new coupon")
    @WithMockUser(roles = {"ADMIN"})
    void testAdminCreateCoupon() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest();
        request.setCode("WELCOME50");
        request.setDescription("50% off on first luxury order");
        request.setDiscountType(DiscountType.PERCENTAGE);
        request.setDiscountValue(BigDecimal.valueOf(50.00));
        request.setMinOrderAmount(BigDecimal.valueOf(500.00));
        request.setMaxDiscountAmount(BigDecimal.valueOf(1000.00));
        request.setStartDate(Instant.now().minus(1, ChronoUnit.HOURS));
        request.setEndDate(Instant.now().plus(60, ChronoUnit.DAYS));
        request.setUsageLimit(500);

        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("WELCOME50"))
                .andExpect(jsonPath("$.data.discountValue").value(50.00));
    }
}

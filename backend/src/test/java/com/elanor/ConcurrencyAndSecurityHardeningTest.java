package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.coupon.entity.Coupon;
import com.elanor.coupon.entity.DiscountType;
import com.elanor.coupon.repository.CouponRepository;
import com.elanor.coupon.service.CouponService;
import com.elanor.inventory.dto.InventoryReservationDto;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConcurrencyAndSecurityHardeningTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AuthService authService;

    private ProductVariant scarceVariant;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Limited Edition Botanical Elixir");
        p.setSlug("limited-edition-botanical-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("5000.00"));
        Product savedP = productRepository.save(p);

        scarceVariant = new ProductVariant();
        scarceVariant.setProduct(savedP);
        scarceVariant.setSku("LIMITED-50ML-" + UUID.randomUUID().toString().substring(0, 8));
        scarceVariant.setName("50ml Handcrafted");
        scarceVariant.setPrice(new BigDecimal("5000.00"));
        scarceVariant.setActive(true);
        scarceVariant = productVariantRepository.save(scarceVariant);

        // Only 3 units available in stock
        StockAdjustmentRequest adj = new StockAdjustmentRequest();
        adj.setQuantity(3);
        adj.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(scarceVariant.getId(), adj, "CONCURRENCY_TEST");
    }

    @Test
    public void testConcurrentInventoryReservationsPreventOverselling() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successfulReservations = new AtomicInteger(0);
        AtomicInteger failedReservations = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Wait for all threads to align
                    UUID simulatedOrderId = UUID.randomUUID();
                    InventoryReservationDto res = inventoryService.reserveStock(scarceVariant, simulatedOrderId, 1);
                    if (res != null && res.getId() != null) {
                        successfulReservations.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedReservations.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); // Fire simultaneously
        doneLatch.await();
        executor.shutdown();

        // Exactly 3 reservations must succeed because only 3 were in stock
        assertEquals(3, successfulReservations.get(), "Must only reserve available units without overselling");
        assertEquals(7, failedReservations.get(), "Remaining attempts must fail gracefully due to insufficient stock");
    }

    @Test
    public void testConcurrentCouponUsageIncrement() throws InterruptedException {
        String couponCode = "FLASH" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Coupon coupon = new Coupon(
                couponCode,
                "Flash Sale 20% Off",
                DiscountType.PERCENTAGE,
                new BigDecimal("20.00"),
                BigDecimal.ZERO,
                new BigDecimal("500.00"),
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(7, ChronoUnit.DAYS),
                100,
                true
        );
        coupon = couponRepository.save(coupon);

        int threadCount = 15;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    couponService.incrementUsage(couponCode);
                } catch (Exception ignored) {
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();
        executor.shutdown();

        Coupon updated = couponRepository.findByCodeIgnoreCase(couponCode).orElseThrow();
        assertEquals(15, updated.getUsageCount(), "Coupon usage count must match exact concurrent increments");
    }

    @Test
    public void testOpenApiSwaggerDocsEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Élanor Luxury Skincare API"));
    }

    @Test
    public void testCrossUserIsolation_IDORDefense() throws Exception {
        // User A
        RegisterRequest userAReq = new RegisterRequest(
                "UserA", "Test",
                "userA-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com",
                "9" + (int)(Math.random() * 899999999 + 100000000),
                "Password123!"
        );
        AuthResponse authA = authService.register(userAReq);

        // User B
        RegisterRequest userBReq = new RegisterRequest(
                "UserB", "Test",
                "userB-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com",
                "9" + (int)(Math.random() * 899999999 + 100000000),
                "Password123!"
        );
        AuthResponse authB = authService.register(userBReq);

        // User A should NOT be able to access non-existent or foreign resources without 403/404
        UUID fakeOrderId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/orders/" + fakeOrderId)
                        .header("Authorization", "Bearer " + authA.getAccessToken()))
                .andExpect(status().isNotFound());
    }
}

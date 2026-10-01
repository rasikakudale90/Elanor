package com.elanor;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.inventory.dto.InventoryDto;
import com.elanor.inventory.dto.InventoryReservationDto;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private ProductVariant testVariant;

    @BeforeEach
    public void setupProductAndVariant() {
        Product product = new Product();
        product.setName("Rose Radiance Creme");
        product.setSlug("rose-radiance-creme-" + UUID.randomUUID());
        product.setBasePrice(new BigDecimal("1899.00"));
        product.setStatus(ProductStatus.ACTIVE);
        Product savedProduct = productRepository.save(product);

        ProductVariant variant = new ProductVariant(
                savedProduct,
                "SKU-ROSE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Rose Radiance Creme 50ml",
                new BigDecimal("1899.00"),
                null,
                "{\"size\":\"50ml\"}"
        );
        testVariant = productVariantRepository.save(variant);
    }

    @Test
    public void shouldInitializeAndAdjustStock() {
        // 1. Initial stock is 0
        InventoryDto initial = inventoryService.getInventoryByVariantId(testVariant.getId());
        assertEquals(0, initial.getOnHand());
        assertEquals(0, initial.getReserved());
        assertEquals(0, initial.getAvailable());

        // 2. Restock 20 units
        StockAdjustmentRequest restockReq = new StockAdjustmentRequest(MovementType.RESTOCK, 20, "Initial batch", "PO-1001");
        InventoryDto restocked = inventoryService.adjustStock(testVariant.getId(), restockReq, "AdminUser");
        assertEquals(20, restocked.getOnHand());
        assertEquals(0, restocked.getReserved());
        assertEquals(20, restocked.getAvailable());
        assertFalse(restocked.isLowStock()); // threshold is 10
    }

    @Test
    public void shouldReserveCommitAndReleaseStockAccurately() {
        // 1. Restock 10 units
        inventoryService.adjustStock(testVariant.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 10, "Restock", "PO-1"), "Admin");

        // 2. Reserve 3 units
        UUID orderId = UUID.randomUUID();
        InventoryReservationDto reservation = inventoryService.reserveStock(testVariant, orderId, 3);
        assertNotNull(reservation.getId());
        assertEquals(3, reservation.getQuantity());

        // Check Inventory Invariants
        InventoryDto afterRes = inventoryService.getInventoryByVariantId(testVariant.getId());
        assertEquals(10, afterRes.getOnHand());
        assertEquals(3, afterRes.getReserved());
        assertEquals(7, afterRes.getAvailable()); // onHand (10) - reserved (3) = 7

        // 3. Commit Reservation (Payment Success)
        inventoryService.commitReservation(reservation.getId());

        InventoryDto afterCommit = inventoryService.getInventoryByVariantId(testVariant.getId());
        assertEquals(7, afterCommit.getOnHand());
        assertEquals(0, afterCommit.getReserved());
        assertEquals(7, afterCommit.getAvailable());

        // 4. Reserve 5 more units then release (Payment Failure)
        InventoryReservationDto failedReservation = inventoryService.reserveStock(testVariant, UUID.randomUUID(), 5);
        InventoryDto duringFailed = inventoryService.getInventoryByVariantId(testVariant.getId());
        assertEquals(7, duringFailed.getOnHand());
        assertEquals(5, duringFailed.getReserved());
        assertEquals(2, duringFailed.getAvailable());

        inventoryService.releaseReservation(failedReservation.getId());

        InventoryDto afterRelease = inventoryService.getInventoryByVariantId(testVariant.getId());
        assertEquals(7, afterRelease.getOnHand());
        assertEquals(0, afterRelease.getReserved());
        assertEquals(7, afterRelease.getAvailable());
    }

    @Test
    public void shouldRejectReservationWhenInsufficientStock() {
        // Stock is 2
        inventoryService.adjustStock(testVariant.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 2, "Restock", "PO-2"), "Admin");

        // Attempt reserving 5
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inventoryService.reserveStock(testVariant, UUID.randomUUID(), 5);
        });

        assertEquals(ErrorCode.PRODUCT_OUT_OF_STOCK, exception.getErrorCode());
    }
}

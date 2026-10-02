package com.elanor.inventory.service;

import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.inventory.dto.*;
import com.elanor.inventory.entity.*;
import com.elanor.inventory.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryReservationRepository reservationRepository;
    private final ProductVariantRepository productVariantRepository;
    private final int defaultReservationTimeoutMinutes;
    private final int defaultLowStockThreshold;

    public InventoryService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            InventoryReservationRepository reservationRepository,
            ProductVariantRepository productVariantRepository,
            @Value("${elanor.inventory.reservation-timeout-minutes:15}") int defaultReservationTimeoutMinutes,
            @Value("${elanor.inventory.low-stock-threshold:10}") int defaultLowStockThreshold) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.reservationRepository = reservationRepository;
        this.productVariantRepository = productVariantRepository;
        this.defaultReservationTimeoutMinutes = defaultReservationTimeoutMinutes;
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }

    @Transactional
    public Inventory getOrCreateInventory(ProductVariant variant) {
        return inventoryRepository.findByVariant(variant)
                .orElseGet(() -> {
                    Inventory inv = new Inventory(variant, 0, defaultLowStockThreshold);
                    return inventoryRepository.save(inv);
                });
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryByVariantId(UUID variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        Inventory inventory = getOrCreateInventory(variant);
        return mapToDto(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockInventories() {
        return inventoryRepository.findLowStockInventories().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDto adjustStock(UUID variantId, StockAdjustmentRequest request, String actor) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        Inventory inventory = getOrCreateInventory(variant);
        int qty = request.getQuantity();

        if (request.getMovementType() == MovementType.RESTOCK || request.getMovementType() == MovementType.RETURN || request.getMovementType() == MovementType.CANCEL) {
            inventory.setOnHand(inventory.getOnHand() + Math.abs(qty));
        } else if (request.getMovementType() == MovementType.DAMAGED || request.getMovementType() == MovementType.ADJUSTMENT) {
            int newOnHand = inventory.getOnHand() + qty;
            if (newOnHand < inventory.getReserved()) {
                throw new BusinessException(ErrorCode.CONFLICT, "Cannot adjust stock below currently reserved quantity (" + inventory.getReserved() + ").");
            }
            inventory.setOnHand(Math.max(0, newOnHand));
        }

        Inventory saved = inventoryRepository.save(inventory);

        InventoryMovement movement = new InventoryMovement(
                variant,
                request.getMovementType(),
                qty,
                request.getReferenceId(),
                request.getReason(),
                actor != null ? actor : "SYSTEM"
        );
        movementRepository.save(movement);

        return mapToDto(saved);
    }

    @Transactional
    public InventoryReservationDto reserveStock(ProductVariant variant, UUID orderId, int quantity) {
        return reserveStock(variant, orderId, quantity, defaultReservationTimeoutMinutes);
    }

    @Transactional
    public InventoryReservationDto reserveStock(ProductVariant variant, UUID orderId, int quantity, int timeoutMinutes) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Reservation quantity must be greater than zero.");
        }

        Inventory inventory = getOrCreateInventory(variant);

        if (inventory.getAvailable() < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                    "Insufficient stock for SKU " + variant.getSku() + ". Available: " + inventory.getAvailable() + ", requested: " + quantity);
        }

        inventory.setReserved(inventory.getReserved() + quantity);
        inventoryRepository.save(inventory);

        Instant expiresAt = Instant.now().plus(timeoutMinutes, ChronoUnit.MINUTES);
        InventoryReservation reservation = new InventoryReservation(variant, orderId, quantity, expiresAt);
        InventoryReservation savedRes = reservationRepository.save(reservation);

        InventoryMovement movement = new InventoryMovement(
                variant,
                MovementType.RESERVATION,
                quantity,
                savedRes.getId().toString(),
                "Temporary reservation for checkout",
                "SYSTEM"
        );
        movementRepository.save(movement);

        return mapToReservationDto(savedRes);
    }

    @Transactional
    public void commitReservation(UUID reservationId) {
        InventoryReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return; // Already processed
        }

        ProductVariant variant = reservation.getVariant();
        Inventory inventory = getOrCreateInventory(variant);

        int qty = reservation.getQuantity();
        inventory.setOnHand(Math.max(0, inventory.getOnHand() - qty));
        inventory.setReserved(Math.max(0, inventory.getReserved() - qty));
        inventoryRepository.save(inventory);

        reservation.setStatus(ReservationStatus.COMMITTED);
        reservationRepository.save(reservation);

        InventoryMovement movement = new InventoryMovement(
                variant,
                MovementType.SALE,
                -qty,
                reservation.getOrderId() != null ? reservation.getOrderId().toString() : reservation.getId().toString(),
                "Order confirmed and stock committed",
                "SYSTEM"
        );
        movementRepository.save(movement);
    }

    @Transactional
    public void releaseReservation(UUID reservationId) {
        InventoryReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return;
        }

        ProductVariant variant = reservation.getVariant();
        Inventory inventory = getOrCreateInventory(variant);

        int qty = reservation.getQuantity();
        inventory.setReserved(Math.max(0, inventory.getReserved() - qty));
        inventoryRepository.save(inventory);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepository.save(reservation);

        InventoryMovement movement = new InventoryMovement(
                variant,
                MovementType.RELEASE,
                qty,
                reservation.getId().toString(),
                "Reservation released",
                "SYSTEM"
        );
        movementRepository.save(movement);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredReservations() {
        Instant now = Instant.now();
        List<InventoryReservation> expired = reservationRepository.findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now);
        for (InventoryReservation res : expired) {
            log.info("[INVENTORY CLEANUP] Releasing expired reservation [{}] for SKU [{}]", res.getId(), res.getVariant().getSku());
            releaseReservation(res.getId());
        }
    }

    @Transactional(readOnly = true)
    public Page<InventoryMovementDto> getMovements(UUID variantId, Pageable pageable) {
        return movementRepository.findByVariantIdOrderByCreatedAtDesc(variantId, pageable)
                .map(this::mapToMovementDto);
    }

    private InventoryDto mapToDto(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        return new InventoryDto(
                inventory.getId(),
                variant.getId(),
                variant.getSku(),
                variant.getProduct() != null ? variant.getProduct().getName() : "",
                variant.getName(),
                inventory.getOnHand(),
                inventory.getReserved(),
                inventory.getAvailable(),
                inventory.getLowStockThreshold(),
                inventory.isLowStock(),
                inventory.getUpdatedAt()
        );
    }

    private InventoryReservationDto mapToReservationDto(InventoryReservation reservation) {
        return new InventoryReservationDto(
                reservation.getId(),
                reservation.getVariant().getId(),
                reservation.getVariant().getSku(),
                reservation.getOrderId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getExpiresAt(),
                reservation.getCreatedAt()
        );
    }

    private InventoryMovementDto mapToMovementDto(InventoryMovement movement) {
        return new InventoryMovementDto(
                movement.getId(),
                movement.getVariant().getId(),
                movement.getVariant().getSku(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getReferenceId(),
                movement.getReason(),
                movement.getActor(),
                movement.getCreatedAt()
        );
    }
}

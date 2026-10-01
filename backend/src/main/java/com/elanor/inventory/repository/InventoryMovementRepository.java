package com.elanor.inventory.repository;

import com.elanor.catalog.entity.ProductVariant;
import com.elanor.inventory.entity.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    Page<InventoryMovement> findByVariantOrderByCreatedAtDesc(ProductVariant variant, Pageable pageable);
    Page<InventoryMovement> findByVariantIdOrderByCreatedAtDesc(UUID variantId, Pageable pageable);
}

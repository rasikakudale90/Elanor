package com.elanor.inventory.repository;

import com.elanor.catalog.entity.ProductVariant;
import com.elanor.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByVariant(ProductVariant variant);
    Optional<Inventory> findByVariantId(UUID variantId);

    @Query("SELECT i FROM Inventory i WHERE (i.onHand - i.reserved) <= i.lowStockThreshold")
    List<Inventory> findLowStockInventories();
}

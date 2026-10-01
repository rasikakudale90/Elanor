package com.elanor.catalog.repository;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findBySku(String sku);
    List<ProductVariant> findByProductAndActiveTrue(Product product);
    List<ProductVariant> findByProduct(Product product);
    Optional<ProductVariant> findByIdAndProduct(UUID id, Product product);
    boolean existsBySku(String sku);
}

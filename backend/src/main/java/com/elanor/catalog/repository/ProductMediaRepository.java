package com.elanor.catalog.repository;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, UUID> {
    List<ProductMedia> findByProductOrderByDisplayOrderAsc(Product product);
    Optional<ProductMedia> findByIdAndProduct(UUID id, Product product);
}

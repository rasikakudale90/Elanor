package com.elanor.catalog.repository;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.slug = :slug AND " +
           "(p.status = 'ACTIVE' OR (p.status = 'SCHEDULED' AND (p.publishAt IS NULL OR p.publishAt <= :now) AND (p.unpublishAt IS NULL OR p.unpublishAt > :now)))")
    Optional<Product> findPublicBySlug(@Param("slug") String slug, @Param("now") Instant now);

    @Query("SELECT p FROM Product p WHERE " +
           "(p.status = 'ACTIVE' OR (p.status = 'SCHEDULED' AND (p.publishAt IS NULL OR p.publishAt <= :now) AND (p.unpublishAt IS NULL OR p.unpublishAt > :now))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findPublicProducts(@Param("categoryId") UUID categoryId, @Param("now") Instant now, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.collections c WHERE c.slug = :collectionSlug AND " +
           "(p.status = 'ACTIVE' OR (p.status = 'SCHEDULED' AND (p.publishAt IS NULL OR p.publishAt <= :now) AND (p.unpublishAt IS NULL OR p.unpublishAt > :now)))")
    Page<Product> findPublicByCollectionSlug(@Param("collectionSlug") String collectionSlug, @Param("now") Instant now, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(p.status = 'ACTIVE' OR (p.status = 'SCHEDULED' AND (p.publishAt IS NULL OR p.publishAt <= :now) AND (p.unpublishAt IS NULL OR p.unpublishAt > :now))) AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> findTopSuggestions(@Param("query") String query, @Param("now") Instant now, Pageable pageable);
}

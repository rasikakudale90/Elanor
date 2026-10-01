package com.elanor.catalog.controller;

import com.elanor.catalog.dto.ProductDetailDto;
import com.elanor.catalog.dto.ProductSummaryDto;
import com.elanor.catalog.service.CatalogService;
import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Catalog", description = "Public Product Catalog APIs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @Operation(summary = "Get paginated public products with category and collection filters")
    public ResponseEntity<ApiResponse<Page<ProductSummaryDto>>> getProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String collection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        int safeSize = Math.min(size, 100);
        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(direction, property));
        Page<ProductSummaryDto> products = catalogService.getPublicProducts(categoryId, collection, pageable);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get detailed product information by slug")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProductBySlug(@PathVariable String slug) {
        ProductDetailDto product = catalogService.getPublicProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }
}

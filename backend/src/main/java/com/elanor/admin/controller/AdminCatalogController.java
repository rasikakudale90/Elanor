package com.elanor.admin.controller;

import com.elanor.catalog.dto.*;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.service.CatalogService;
import com.elanor.category.dto.CategoryDto;
import com.elanor.category.dto.CreateCategoryRequest;
import com.elanor.category.dto.UpdateCategoryRequest;
import com.elanor.category.service.CategoryService;
import com.elanor.collection.dto.CollectionDto;
import com.elanor.collection.dto.CreateCollectionRequest;
import com.elanor.collection.dto.UpdateCollectionRequest;
import com.elanor.collection.service.CollectionService;
import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Catalog", description = "Admin Management for Categories, Collections, Products, and Variants")
public class AdminCatalogController {

    private final CategoryService categoryService;
    private final CollectionService collectionService;
    private final CatalogService catalogService;

    public AdminCatalogController(CategoryService categoryService, CollectionService collectionService, CatalogService catalogService) {
        this.categoryService = categoryService;
        this.collectionService = collectionService;
        this.catalogService = catalogService;
    }

    // --- Category Management ---
    @PostMapping("/categories")
    @Operation(summary = "Create category")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto category = categoryService.createCategory(request);
        return new ResponseEntity<>(ApiResponse.ok(category, "Category created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok(category, "Category updated successfully"));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted successfully"));
    }

    // --- Collection Management ---
    @PostMapping("/collections")
    @Operation(summary = "Create collection")
    public ResponseEntity<ApiResponse<CollectionDto>> createCollection(@Valid @RequestBody CreateCollectionRequest request) {
        CollectionDto collection = collectionService.createCollection(request);
        return new ResponseEntity<>(ApiResponse.ok(collection, "Collection created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/collections/{id}")
    @Operation(summary = "Update collection")
    public ResponseEntity<ApiResponse<CollectionDto>> updateCollection(@PathVariable UUID id, @Valid @RequestBody UpdateCollectionRequest request) {
        CollectionDto collection = collectionService.updateCollection(id, request);
        return ResponseEntity.ok(ApiResponse.ok(collection, "Collection updated successfully"));
    }

    @DeleteMapping("/collections/{id}")
    @Operation(summary = "Delete collection")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(@PathVariable UUID id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.ok(ApiResponse.ok("Collection deleted successfully"));
    }

    // --- Product Management ---
    @GetMapping("/products/{id}")
    @Operation(summary = "Get product by ID for admin view")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProductById(@PathVariable UUID id) {
        ProductDetailDto product = catalogService.getAdminProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @PostMapping("/products")
    @Operation(summary = "Create product")
    public ResponseEntity<ApiResponse<ProductDetailDto>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDetailDto product = catalogService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.ok(product, "Product created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<ProductDetailDto>> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        ProductDetailDto product = catalogService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok(product, "Product updated successfully"));
    }

    @PatchMapping("/products/{id}/status")
    @Operation(summary = "Update product status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(@PathVariable UUID id, @RequestParam ProductStatus status) {
        catalogService.updateProductStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Product status updated successfully"));
    }

    // --- Variant Management ---
    @PostMapping("/products/{id}/variants")
    @Operation(summary = "Add variant to product")
    public ResponseEntity<ApiResponse<ProductVariantDto>> createVariant(@PathVariable UUID id, @Valid @RequestBody CreateVariantRequest request) {
        ProductVariantDto variant = catalogService.createVariant(id, request);
        return new ResponseEntity<>(ApiResponse.ok(variant, "Variant created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/products/{productId}/variants/{variantId}")
    @Operation(summary = "Update product variant")
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateVariantRequest request) {
        ProductVariantDto variant = catalogService.updateVariant(productId, variantId, request);
        return ResponseEntity.ok(ApiResponse.ok(variant, "Variant updated successfully"));
    }

    @DeleteMapping("/products/{productId}/variants/{variantId}")
    @Operation(summary = "Delete product variant")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable UUID productId, @PathVariable UUID variantId) {
        catalogService.deleteVariant(productId, variantId);
        return ResponseEntity.ok(ApiResponse.ok("Variant deleted successfully"));
    }

    // --- Media Management ---
    @PostMapping("/products/{id}/media")
    @Operation(summary = "Add media asset to product")
    public ResponseEntity<ApiResponse<ProductMediaDto>> addMedia(@PathVariable UUID id, @Valid @RequestBody CreateMediaRequest request) {
        ProductMediaDto media = catalogService.addMedia(id, request);
        return new ResponseEntity<>(ApiResponse.ok(media, "Media asset added successfully"), HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{productId}/media/{mediaId}")
    @Operation(summary = "Delete product media asset")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable UUID productId, @PathVariable UUID mediaId) {
        catalogService.deleteMedia(productId, mediaId);
        return ResponseEntity.ok(ApiResponse.ok("Media asset deleted successfully"));
    }
}

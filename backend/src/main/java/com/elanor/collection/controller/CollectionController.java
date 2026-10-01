package com.elanor.collection.controller;

import com.elanor.collection.dto.CollectionDto;
import com.elanor.collection.service.CollectionService;
import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collections")
@Tag(name = "Collection", description = "Public Collection APIs")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    @Operation(summary = "Get all active merchandising collections")
    public ResponseEntity<ApiResponse<List<CollectionDto>>> getCollections() {
        List<CollectionDto> collections = collectionService.getActiveCollections();
        return ResponseEntity.ok(ApiResponse.ok(collections));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get collection details by slug")
    public ResponseEntity<ApiResponse<CollectionDto>> getBySlug(@PathVariable String slug) {
        CollectionDto collection = collectionService.getBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(collection));
    }
}

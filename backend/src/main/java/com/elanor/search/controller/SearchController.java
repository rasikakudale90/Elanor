package com.elanor.search.controller;

import com.elanor.catalog.dto.ProductSummaryDto;
import com.elanor.common.response.ApiResponse;
import com.elanor.search.dto.SearchFilterRequest;
import com.elanor.search.dto.SearchSuggestionDto;
import com.elanor.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search & Discovery", description = "Multi-attribute product search and real-time autocomplete suggestions")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    @Operation(summary = "Search products with multi-attribute filtering, sorting, and pagination")
    public ResponseEntity<ApiResponse<Page<ProductSummaryDto>>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String collection,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String badge,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchFilterRequest filter = new SearchFilterRequest();
        filter.setQuery(q);
        filter.setCategorySlug(category);
        filter.setCollectionSlug(collection);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setBadge(badge);
        filter.setSortBy(sortBy);
        filter.setPage(page);
        filter.setSize(size);

        Page<ProductSummaryDto> results = searchService.search(filter);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get instant typeahead suggestions for products and categories")
    public ResponseEntity<ApiResponse<SearchSuggestionDto>> getSuggestions(@RequestParam String q) {
        SearchSuggestionDto suggestions = searchService.getSuggestions(q);
        return ResponseEntity.ok(ApiResponse.ok(suggestions));
    }
}

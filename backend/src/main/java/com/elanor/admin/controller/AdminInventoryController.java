package com.elanor.admin.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.inventory.dto.InventoryDto;
import com.elanor.inventory.dto.InventoryMovementDto;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Inventory", description = "Stock Management, Restocking, and Inventory Audit APIs")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get all variant inventory levels")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getAllInventory() {
        List<InventoryDto> list = inventoryService.getAllInventories();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{variantId}")
    @Operation(summary = "Get inventory level for a specific variant")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryByVariantId(@PathVariable UUID variantId) {
        InventoryDto inventory = inventoryService.getInventoryByVariantId(variantId);
        return ResponseEntity.ok(ApiResponse.ok(inventory));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get all low-stock variants")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockInventories() {
        List<InventoryDto> lowStock = inventoryService.getLowStockInventories();
        return ResponseEntity.ok(ApiResponse.ok(lowStock));
    }

    @PostMapping("/{variantId}/adjust")
    @Operation(summary = "Adjust stock (Restock, manual adjustment, damage deduction)")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(
            @PathVariable UUID variantId,
            @Valid @RequestBody StockAdjustmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String actor = principal != null ? principal.getEmail() : "ADMIN";
        InventoryDto updated = inventoryService.adjustStock(variantId, request, actor);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Stock adjusted successfully"));
    }

    @GetMapping("/{variantId}/movements")
    @Operation(summary = "Get paginated audit log of inventory movements for a variant")
    public ResponseEntity<ApiResponse<Page<InventoryMovementDto>>> getMovements(
            @PathVariable UUID variantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<InventoryMovementDto> movements = inventoryService.getMovements(variantId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(movements));
    }
}

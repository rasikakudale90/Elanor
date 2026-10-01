package com.elanor.admin.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.order.dto.OrderDto;
import com.elanor.order.dto.UpdateOrderStatusRequest;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Orders", description = "Administrative Order Management and Status Transitions")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get paginated orders with optional status and search filter")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderDto> orders = orderService.getAllOrdersForAdmin(status, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed order by ID for administration")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable UUID id) {
        OrderDto order = orderService.getOrderById(id, null, true);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status with lifecycle history entry")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        String actor = principal != null ? principal.getUsername() : "ADMIN";
        OrderDto updated = orderService.updateOrderStatus(id, request, actor);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Order status updated successfully"));
    }
}

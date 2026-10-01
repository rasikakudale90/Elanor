package com.elanor.order.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.order.dto.OrderDto;
import com.elanor.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Customer Order Management APIs")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get paginated order history for authenticated customer")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OrderDto> orders = orderService.getCustomerOrders(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed order by ID")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        OrderDto order = orderService.getOrderById(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get detailed order by order number")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderByNumber(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String orderNumber) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        OrderDto order = orderService.getOrderByOrderNumber(orderNumber, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order before it has been shipped")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) com.elanor.order.dto.CancelOrderRequest request) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        OrderDto cancelled = orderService.cancelOrder(id, request, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(cancelled, "Order cancelled successfully"));
    }
}

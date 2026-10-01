package com.elanor.order.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.order.dto.OrderDto;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final com.elanor.inventory.service.InventoryService inventoryService;
    private final com.elanor.inventory.repository.InventoryReservationRepository reservationRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerProfileRepository customerProfileRepository,
            com.elanor.inventory.service.InventoryService inventoryService,
            com.elanor.inventory.repository.InventoryReservationRepository reservationRepository) {
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.inventoryService = inventoryService;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getCustomerOrders(UUID userId, Pageable pageable) {
        CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer, pageable)
                .map(OrderDto::new);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID orderId, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin) {
            if (order.getCustomer() == null || order.getCustomer().getUser() == null ||
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to view this order.");
            }
        }

        return new OrderDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderNumber(String orderNumber, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin) {
            if (order.getCustomer() == null || order.getCustomer().getUser() == null ||
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to view this order.");
            }
        }

        return new OrderDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersForAdmin(OrderStatus status, String search, Pageable pageable) {
        return orderRepository.findAllWithFilter(status, search, pageable)
                .map(OrderDto::new);
    }

    @Transactional
    public OrderDto updateOrderStatus(UUID orderId, com.elanor.order.dto.UpdateOrderStatusRequest request, String actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (oldStatus != newStatus) {
            order.setStatus(newStatus);
            order.addStatusHistory(oldStatus, newStatus, request.getNote() != null ? request.getNote() : "Status updated by admin", actor != null ? actor : "ADMIN");
            orderRepository.save(order);
        }

        return new OrderDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId, com.elanor.order.dto.CancelOrderRequest request, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin && userId != null) {
            if (order.getCustomer() == null || order.getCustomer().getUser() == null ||
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to cancel this order.");
            }
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELLED, "Order has already been cancelled.");
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL, "Order cannot be cancelled after shipment.");
        }

        if (order.getStatus() == OrderStatus.RETURNED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL, "Order cannot be cancelled in its current state.");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);

        // 1. Release any pending reservations
        List<com.elanor.inventory.entity.InventoryReservation> pendingRes =
                reservationRepository.findByOrderId(order.getId());
        for (com.elanor.inventory.entity.InventoryReservation res : pendingRes) {
            if (res.getStatus() == com.elanor.inventory.entity.ReservationStatus.PENDING) {
                inventoryService.releaseReservation(res.getId());
            }
        }

        // 2. Restock committed inventory if order was CONFIRMED, PROCESSING, or PACKED
        if (oldStatus == OrderStatus.CONFIRMED || oldStatus == OrderStatus.PROCESSING || oldStatus == OrderStatus.PACKED) {
            for (com.elanor.order.entity.OrderItem item : order.getItems()) {
                if (item.getVariant() != null) {
                    com.elanor.inventory.dto.StockAdjustmentRequest adj = new com.elanor.inventory.dto.StockAdjustmentRequest();
                    adj.setQuantity(item.getQuantity());
                    adj.setMovementType(com.elanor.inventory.entity.MovementType.RESTOCK);
                    adj.setReason("Order cancellation: " + (request != null && request.getReason() != null ? request.getReason() : "Customer cancelled"));
                    adj.setReferenceId(order.getId().toString());
                    inventoryService.adjustStock(item.getVariant().getId(), adj, "CANCEL_ORDER");
                }
            }
        }

        String actor = isAdmin ? "ADMIN" : (userId != null ? "CUSTOMER:" + userId : "GUEST");
        String note = request != null && request.getReason() != null && !request.getReason().isBlank()
                ? "Cancelled: " + request.getReason()
                : "Order cancelled";
        order.addStatusHistory(oldStatus, OrderStatus.CANCELLED, note, actor);

        Order saved = orderRepository.save(order);
        return new OrderDto(saved);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}

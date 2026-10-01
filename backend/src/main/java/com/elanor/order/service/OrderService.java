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

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public OrderService(OrderRepository orderRepository, CustomerProfileRepository customerProfileRepository) {
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
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
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}

package com.elanor.returns.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderItem;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.returns.dto.*;
import com.elanor.returns.entity.ReturnItem;
import com.elanor.returns.entity.ReturnRequest;
import com.elanor.returns.enums.ReturnStatus;
import com.elanor.returns.repository.ReturnItemRepository;
import com.elanor.returns.repository.ReturnRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    private static final Logger log = LoggerFactory.getLogger(ReturnService.class);
    private static final long RETURN_WINDOW_DAYS = 7;

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnItemRepository returnItemRepository;
    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final InventoryService inventoryService;

    public ReturnService(ReturnRequestRepository returnRequestRepository,
                         ReturnItemRepository returnItemRepository,
                         OrderRepository orderRepository,
                         CustomerProfileRepository customerProfileRepository,
                         InventoryService inventoryService) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnItemRepository = returnItemRepository;
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public ReturnRequestResponse createReturnRequest(UUID userId, CreateReturnRequestDto request) {
        CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + request.getOrderId()));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "You are not authorized to request a return for this order.");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorCode.RETURN_NOT_ELIGIBLE, "Returns can only be requested for DELIVERED orders.");
        }

        // 7-day return eligibility window check
        Instant deliveryReferenceTime = order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt();
        long daysSinceDelivery = Duration.between(deliveryReferenceTime, Instant.now()).toDays();
        if (daysSinceDelivery > RETURN_WINDOW_DAYS) {
            throw new BusinessException(ErrorCode.RETURN_WINDOW_EXPIRED,
                    "The " + RETURN_WINDOW_DAYS + "-day return window for this order has expired.");
        }

        ReturnRequest returnRequest = new ReturnRequest(
                order,
                customer,
                request.getReason(),
                request.getComments(),
                request.isReplacement()
        );

        for (CreateReturnItemDto itemDto : request.getItems()) {
            OrderItem orderItem = order.getItems().stream()
                    .filter(i -> i.getId().equals(itemDto.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Order item not found in this order: " + itemDto.getOrderItemId()));

            if (itemDto.getQuantity() > orderItem.getQuantity()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Return quantity cannot exceed purchased quantity.");
            }

            // Check if return is already pending/approved for this order item
            List<ReturnItem> existingReturns = returnItemRepository.findByOrderItemId(orderItem.getId());
            boolean alreadyActive = existingReturns.stream().anyMatch(r ->
                    r.getReturnRequest().getStatus() != ReturnStatus.REJECTED);
            if (alreadyActive) {
                throw new BusinessException(ErrorCode.RETURN_ALREADY_REQUESTED,
                        "A return request has already been submitted for item: " + orderItem.getProductName());
            }

            ReturnItem returnItem = new ReturnItem(
                    returnRequest,
                    orderItem,
                    itemDto.getQuantity(),
                    itemDto.getConditionNote()
            );
            returnRequest.addItem(returnItem);
        }

        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Created return request [{}] for order [{}] by customer [{}]",
                saved.getId(), order.getOrderNumber(), customer.getId());

        return new ReturnRequestResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReturnRequestResponse> getCustomerReturns(UUID userId) {
        CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found"));

        return returnRequestRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(ReturnRequestResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReturnRequestResponse getReturnById(UUID returnId, UUID userId, boolean isAdmin) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Return request not found with ID: " + returnId));

        if (!isAdmin && userId != null) {
            if (returnRequest.getCustomer() == null || returnRequest.getCustomer().getUser() == null ||
                    !returnRequest.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to view this return request.");
            }
        }

        return new ReturnRequestResponse(returnRequest);
    }

    @Transactional
    public ReturnRequestResponse updateReturnStatus(UUID returnId, UpdateReturnStatusRequest request, String adminActor) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Return request not found with ID: " + returnId));

        ReturnStatus oldStatus = returnRequest.getStatus();
        returnRequest.setStatus(request.getStatus());

        // Restock inventory upon physical receipt / inspection
        if ((request.getStatus() == ReturnStatus.RECEIVED || request.getStatus() == ReturnStatus.INSPECTED)
                && request.isRestockInventory() && oldStatus != ReturnStatus.RECEIVED && oldStatus != ReturnStatus.INSPECTED) {
            for (ReturnItem item : returnRequest.getItems()) {
                if (item.getOrderItem() != null && item.getOrderItem().getVariant() != null) {
                    StockAdjustmentRequest adj = new StockAdjustmentRequest();
                    adj.setQuantity(item.getQuantity());
                    adj.setMovementType(MovementType.RETURN);
                    adj.setReason("Return restock for Return ID: " + returnRequest.getId() + (request.getNote() != null ? " - " + request.getNote() : ""));
                    inventoryService.adjustStock(item.getOrderItem().getVariant().getId(), adj, adminActor != null ? adminActor : "ADMIN");
                }
            }
        }

        Order order = returnRequest.getOrder();
        if (order != null) {
            order.addStatusHistory(
                    order.getStatus(),
                    order.getStatus(),
                    "Return request [" + returnRequest.getId() + "] status updated to " + request.getStatus() + (request.getNote() != null ? " (" + request.getNote() + ")" : ""),
                    adminActor != null ? adminActor : "ADMIN"
            );
            orderRepository.save(order);
        }

        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Updated return request [{}] status to [{}] by [{}]", returnId, request.getStatus(), adminActor);
        return new ReturnRequestResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReturnRequestResponse> getAllReturns(Pageable pageable) {
        return returnRequestRepository.findAllByOrderByCreatedAtDesc(pageable).map(ReturnRequestResponse::new);
    }
}

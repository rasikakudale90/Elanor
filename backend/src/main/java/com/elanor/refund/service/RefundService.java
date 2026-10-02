package com.elanor.refund.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.refund.dto.ProcessRefundRequest;
import com.elanor.refund.dto.RefundResponse;
import com.elanor.refund.entity.Refund;
import com.elanor.refund.enums.RefundStatus;
import com.elanor.refund.repository.RefundRepository;
import com.elanor.returns.entity.ReturnRequest;
import com.elanor.returns.enums.ReturnStatus;
import com.elanor.returns.repository.ReturnRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundService.class);

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public RefundService(RefundRepository refundRepository,
                         OrderRepository orderRepository,
                         ReturnRequestRepository returnRequestRepository) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    @Transactional
    public RefundResponse processRefund(ProcessRefundRequest request, String adminActor) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + request.getOrderId()));

        List<Refund> existingRefunds = refundRepository.findByOrderId(order.getId());
        BigDecimal totalRefundedSoFar = existingRefunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.REFUND_COMPLETED)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cumulativeAfterNewRefund = totalRefundedSoFar.add(request.getAmount());
        if (cumulativeAfterNewRefund.compareTo(order.getTotalAmount()) > 0) {
            throw new BusinessException(ErrorCode.REFUND_AMOUNT_EXCEEDED,
                    "Total refund amount (? " + cumulativeAfterNewRefund + ") cannot exceed order total (? " + order.getTotalAmount() + ").");
        }

        ReturnRequest returnRequest = null;
        if (request.getReturnRequestId() != null) {
            returnRequest = returnRequestRepository.findById(request.getReturnRequestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Return request not found with ID: " + request.getReturnRequestId()));
        }

        Refund refund = new Refund(
                order,
                returnRequest,
                request.getAmount(),
                RefundStatus.REFUND_COMPLETED,
                request.getRefundMethod(),
                request.getReferenceId() != null ? request.getReferenceId() : "REF-" + System.currentTimeMillis(),
                adminActor != null ? adminActor : "ADMIN"
        );

        Refund savedRefund = refundRepository.save(refund);

        // Synchronize Order status
        if (cumulativeAfterNewRefund.compareTo(order.getTotalAmount()) == 0) {
            OrderStatus prevStatus = order.getStatus();
            order.setStatus(OrderStatus.REFUNDED);
            order.addStatusHistory(
                    prevStatus,
                    OrderStatus.REFUNDED,
                    "Full refund of ? " + request.getAmount() + " processed via " + request.getRefundMethod(),
                    adminActor != null ? adminActor : "ADMIN"
            );
            orderRepository.save(order);

            if (returnRequest != null) {
                returnRequest.setStatus(ReturnStatus.REFUND_COMPLETED);
                returnRequestRepository.save(returnRequest);
            }
        } else {
            order.addStatusHistory(
                    order.getStatus(),
                    order.getStatus(),
                    "Partial refund of ? " + request.getAmount() + " processed via " + request.getRefundMethod(),
                    adminActor != null ? adminActor : "ADMIN"
            );
            orderRepository.save(order);
        }

        log.info("Processed refund [{}] of ? [{}] for order [{}]",
                savedRefund.getId(), request.getAmount(), order.getOrderNumber());

        return new RefundResponse(savedRefund);
    }

    @Transactional(readOnly = true)
    public List<RefundResponse> getOrderRefunds(UUID orderId) {
        return refundRepository.findByOrderId(orderId).stream()
                .map(RefundResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RefundResponse> getAllRefunds(Pageable pageable) {
        return refundRepository.findAllByOrderByCreatedAtDesc(pageable).map(RefundResponse::new);
    }
}

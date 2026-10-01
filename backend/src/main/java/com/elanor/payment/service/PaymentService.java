package com.elanor.payment.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.idempotency.service.IdempotencyService;
import com.elanor.inventory.entity.InventoryReservation;
import com.elanor.inventory.entity.ReservationStatus;
import com.elanor.inventory.repository.InventoryReservationRepository;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.payment.dto.DemoPaymentResultRequest;
import com.elanor.payment.dto.InitiatePaymentRequest;
import com.elanor.payment.dto.PaymentDto;
import com.elanor.payment.entity.Payment;
import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.entity.PaymentStatus;
import com.elanor.payment.provider.*;
import com.elanor.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProviderFactory paymentProviderFactory;
    private final InventoryService inventoryService;
    private final InventoryReservationRepository reservationRepository;
    private final IdempotencyService idempotencyService;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentProviderFactory paymentProviderFactory,
            InventoryService inventoryService,
            InventoryReservationRepository reservationRepository,
            IdempotencyService idempotencyService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentProviderFactory = paymentProviderFactory;
        this.inventoryService = inventoryService;
        this.reservationRepository = reservationRepository;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public PaymentDto initiatePayment(InitiatePaymentRequest request, UUID userId, boolean isAdmin, String idempotencyKey) {
        // Idempotency check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<PaymentDto> cached = idempotencyService.getCachedResponse(idempotencyKey, PaymentDto.class);
            if (cached.isPresent()) {
                log.info("Returning cached payment response for idempotency key [{}]", idempotencyKey);
                return cached.get();
            }
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin && userId != null) {
            if (order.getCustomer() != null && order.getCustomer().getUser() != null &&
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You cannot initiate payment for another customer's order.");
            }
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELLED, "Cannot pay for a cancelled order.");
        }

        // Check if already paid
        List<Payment> existingPayments = paymentRepository.findByOrderOrderByCreatedAtDesc(order);
        boolean alreadyPaid = existingPayments.stream().anyMatch(p -> p.getStatus() == PaymentStatus.SUCCESSFUL);
        if (alreadyPaid) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED, "This order has already been paid for.");
        }

        PaymentMethod method = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.DEMO_ONLINE;
        PaymentProviderType providerType = request.getProvider();
        if (providerType == null) {
            providerType = (method == PaymentMethod.COD) ? PaymentProviderType.COD : PaymentProviderType.DEMO;
        }

        PaymentProvider provider = paymentProviderFactory.getProvider(providerType);

        Payment payment = new Payment(order, providerType, method, order.getTotalAmount());
        Payment savedPayment = paymentRepository.save(payment);

        String email = order.getCustomer() != null && order.getCustomer().getUser() != null
                ? order.getCustomer().getUser().getEmail() : null;
        String phone = order.getAddress() != null ? order.getAddress().getPhone() : null;

        PaymentRequest providerReq = new PaymentRequest(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                "INR",
                method,
                providerType,
                email,
                phone
        );

        PaymentInitiationResult result = provider.initiate(providerReq);

        savedPayment.setTransactionRef(result.getTransactionRef());
        savedPayment.setStatus(result.getInitialStatus());
        paymentRepository.save(savedPayment);

        // If COD: update order to CONFIRMED and commit inventory
        if (providerType == PaymentProviderType.COD) {
            if (order.getStatus() == OrderStatus.CREATED) {
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(OrderStatus.CONFIRMED);
                order.addStatusHistory(oldStatus, OrderStatus.CONFIRMED, "Order confirmed with Cash on Delivery", "SYSTEM");
                orderRepository.save(order);

                commitOrderReservations(order.getId());
            }
        }

        PaymentDto dto = new PaymentDto(savedPayment);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveRecord(idempotencyKey, "PAYMENT", savedPayment.getId().toString(), dto, 200);
        }

        log.info("Initiated payment [{}] for order [{}] via provider [{}]", savedPayment.getId(), order.getOrderNumber(), providerType);
        return dto;
    }

    @Transactional
    public PaymentDto processDemoResult(UUID paymentId, DemoPaymentResultRequest request, UUID userId, boolean isAdmin) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Payment transaction not found."));

        if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED, "Payment has already succeeded.");
        }

        Order order = payment.getOrder();
        if (!isAdmin && userId != null) {
            if (order.getCustomer() != null && order.getCustomer().getUser() != null &&
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to verify this payment.");
            }
        }

        PaymentProvider provider = paymentProviderFactory.getProvider(payment.getPaymentProvider());
        PaymentVerificationRequest verifyReq = new PaymentVerificationRequest(
                payment.getId(),
                payment.getTransactionRef(),
                request != null ? request.isSuccess() : true,
                null
        );

        PaymentVerificationResult result = provider.verify(verifyReq);

        if (result.isSuccessful()) {
            payment.setStatus(PaymentStatus.SUCCESSFUL);
            payment.setErrorMessage(null);

            if (order.getStatus() == OrderStatus.CREATED) {
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(OrderStatus.CONFIRMED);
                order.addStatusHistory(oldStatus, OrderStatus.CONFIRMED, "Payment completed successfully via Demo Provider", "SYSTEM");
                orderRepository.save(order);

                // Commit stock reservations
                commitOrderReservations(order.getId());
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(result.getErrorMessage());
        }

        paymentRepository.save(payment);
        return new PaymentDto(payment);
    }

    @Transactional
    public PaymentDto confirmCodPaymentCollected(UUID paymentId, String actor) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Payment transaction not found."));

        if (payment.getPaymentProvider() != PaymentProviderType.COD) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "This action is only valid for COD payments.");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED, "COD payment is already marked as collected.");
        }

        payment.setStatus(PaymentStatus.SUCCESSFUL);
        Payment saved = paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.addStatusHistory(order.getStatus(), order.getStatus(), "COD Cash collected and confirmed by admin", actor != null ? actor : "ADMIN");
        orderRepository.save(order);

        log.info("Admin [{}] confirmed COD collection for payment [{}] order [{}]", actor, paymentId, order.getOrderNumber());
        return new PaymentDto(saved);
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(UUID paymentId, UUID userId, boolean isAdmin) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Payment transaction not found."));

        if (!isAdmin && userId != null) {
            Order order = payment.getOrder();
            if (order.getCustomer() != null && order.getCustomer().getUser() != null &&
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to view this payment.");
            }
        }

        return new PaymentDto(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsForOrder(UUID orderId, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin && userId != null) {
            if (order.getCustomer() != null && order.getCustomer().getUser() != null &&
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission to view payments for this order.");
            }
        }

        return paymentRepository.findByOrderOrderByCreatedAtDesc(order).stream()
                .map(PaymentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPaymentsForAdmin(PaymentStatus status, String search, Pageable pageable) {
        return paymentRepository.findAllWithFilter(status, search, pageable)
                .map(PaymentDto::new);
    }

    private void commitOrderReservations(UUID orderId) {
        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        for (InventoryReservation res : reservations) {
            if (res.getStatus() == ReservationStatus.PENDING) {
                inventoryService.commitReservation(res.getId());
            }
        }
    }
}

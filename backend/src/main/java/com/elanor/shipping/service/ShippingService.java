package com.elanor.shipping.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.shipping.dto.*;
import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.entity.ShipmentEvent;
import com.elanor.shipping.enums.ShipmentProviderType;
import com.elanor.shipping.enums.ShipmentStatus;
import com.elanor.shipping.provider.ShippingProvider;
import com.elanor.shipping.provider.ShippingProviderFactory;
import com.elanor.shipping.repository.ShipmentEventRepository;
import com.elanor.shipping.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShippingService {

    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final OrderRepository orderRepository;
    private final ShippingProviderFactory providerFactory;

    public ShippingService(ShipmentRepository shipmentRepository,
                           ShipmentEventRepository shipmentEventRepository,
                           OrderRepository orderRepository,
                           ShippingProviderFactory providerFactory) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.orderRepository = orderRepository;
        this.providerFactory = providerFactory;
    }

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + request.getOrderId()));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL, "Cannot ship a cancelled order.");
        }

        ShipmentProviderType providerType = request.getProvider() != null ? request.getProvider() : ShipmentProviderType.MANUAL;
        ShippingProvider provider = providerFactory.getProvider(providerType);

        Shipment shipment = provider.createShipment(
                order,
                request.getCarrierName(),
                request.getTrackingNumber(),
                request.getTrackingUrl()
        );

        if (request.getStatus() != null) {
            shipment.setStatus(request.getStatus());
        }

        shipment = shipmentRepository.save(shipment);

        // Synchronize Order status
        OrderStatus previousOrderStatus = order.getStatus();
        order.setStatus(OrderStatus.SHIPPED);
        order.addStatusHistory(
                previousOrderStatus,
                OrderStatus.SHIPPED,
                "Shipment created with carrier " + shipment.getCarrierName() + " (Tracking: " + shipment.getTrackingNumber() + ")",
                "ADMIN"
        );
        orderRepository.save(order);

        // Add initial event
        String initialLocation = request.getInitialLocation() != null ? request.getInitialLocation() : "Fulfillment Center";
        String initialDesc = request.getInitialDescription() != null ? request.getInitialDescription() : "Package processed and handed to carrier";
        ShipmentEvent initialEvent = new ShipmentEvent(
                shipment,
                shipment.getStatus().name(),
                initialLocation,
                initialDesc,
                Instant.now()
        );
        shipment.addEvent(initialEvent);
        shipmentEventRepository.save(initialEvent);

        log.info("Created shipment [{}] for order [{}] with tracking [{}]",
                shipment.getId(), order.getOrderNumber(), shipment.getTrackingNumber());

        return new ShipmentResponse(shipment);
    }

    @Transactional
    public ShipmentResponse addShipmentEvent(UUID shipmentId, AddShipmentEventRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Shipment not found with ID: " + shipmentId));

        Instant eventTime = request.getEventTime() != null ? request.getEventTime() : Instant.now();
        ShipmentEvent event = new ShipmentEvent(
                shipment,
                request.getStatus(),
                request.getLocation(),
                request.getDescription(),
                eventTime
        );

        shipment.addEvent(event);
        shipmentEventRepository.save(event);

        // Status alignment based on tracking milestone
        String normalizedStatus = request.getStatus().toUpperCase();
        Order order = shipment.getOrder();

        if (normalizedStatus.contains("DELIVERED")) {
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipment.setDeliveredAt(eventTime);

            if (order != null && order.getStatus() != OrderStatus.DELIVERED) {
                OrderStatus prev = order.getStatus();
                order.setStatus(OrderStatus.DELIVERED);
                order.addStatusHistory(prev, OrderStatus.DELIVERED, "Order delivered to customer", "CARRIER");
                orderRepository.save(order);
            }
        } else if (normalizedStatus.contains("OUT_FOR_DELIVERY")) {
            shipment.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);

            if (order != null && order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
                OrderStatus prev = order.getStatus();
                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                order.addStatusHistory(prev, OrderStatus.OUT_FOR_DELIVERY, "Package is out for delivery", "CARRIER");
                orderRepository.save(order);
            }
        } else if (normalizedStatus.contains("IN_TRANSIT") || normalizedStatus.contains("PICKED_UP")) {
            shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        }

        shipment = shipmentRepository.save(shipment);
        return new ShipmentResponse(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByOrderId(UUID orderId, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (!isAdmin && userId != null) {
            if (order.getCustomer() == null || order.getCustomer().getUser() == null ||
                    !order.getCustomer().getUser().getId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have access to this order's shipping details.");
            }
        }

        List<Shipment> shipments = shipmentRepository.findByOrderId(orderId);
        return shipments.stream().map(ShipmentResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrackingResponse getTrackingByNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Tracking details not found for number: " + trackingNumber));

        return new TrackingResponse(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Shipment not found with ID: " + shipmentId));
        return new ShipmentResponse(shipment);
    }

    @Transactional
    public ShipmentResponse processTrackingWebhook(String trackingNumber, String status, String location, String description, Instant eventTime) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Shipment not found for tracking: " + trackingNumber));

        AddShipmentEventRequest req = new AddShipmentEventRequest(
                status,
                location != null ? location : "In Transit Hub",
                description != null ? description : "Milestone updated via carrier webhook",
                eventTime != null ? eventTime : Instant.now()
        );

        return addShipmentEvent(shipment.getId(), req);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAllByOrderByCreatedAtDesc(pageable).map(ShipmentResponse::new);
    }
}

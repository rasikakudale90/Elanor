package com.elanor.analytics.service;

import com.elanor.analytics.dto.AnalyticsOverviewResponse;
import com.elanor.analytics.dto.RecordAnalyticsEventRequest;
import com.elanor.analytics.entity.AnalyticsEvent;
import com.elanor.analytics.repository.AnalyticsEventRepository;
import com.elanor.auth.entity.User;
import com.elanor.auth.repository.UserRepository;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final AnalyticsEventRepository analyticsEventRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public AnalyticsService(AnalyticsEventRepository analyticsEventRepository,
                            UserRepository userRepository,
                            OrderRepository orderRepository,
                            CustomerProfileRepository customerProfileRepository) {
        this.analyticsEventRepository = analyticsEventRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    @Transactional
    public void recordEvent(UUID userId, RecordAnalyticsEventRequest request) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AnalyticsEvent event = new AnalyticsEvent(
                request.getEventType(),
                user,
                request.getGuestToken(),
                request.getSessionId(),
                request.getMetadataJson()
        );

        analyticsEventRepository.save(event);
        log.info("[ANALYTICS] Event recorded [{}] for user [{}] guest [{}]",
                request.getEventType(), userId, request.getGuestToken());
    }

    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse getOverview() {
        List<Order> orders = orderRepository.findAll();
        long totalOrders = orders.size();

        // Calculate revenue from non-cancelled orders
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long validPaidOrderCount = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .count();

        BigDecimal aov = BigDecimal.ZERO;
        if (validPaidOrderCount > 0) {
            aov = totalRevenue.divide(BigDecimal.valueOf(validPaidOrderCount), 2, RoundingMode.HALF_UP);
        }

        long totalCustomers = customerProfileRepository.count();
        long totalEvents = analyticsEventRepository.count();

        Map<String, Long> statusBreakdown = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        return new AnalyticsOverviewResponse(
                totalRevenue,
                totalOrders,
                aov,
                totalCustomers,
                totalEvents,
                statusBreakdown
        );
    }
}

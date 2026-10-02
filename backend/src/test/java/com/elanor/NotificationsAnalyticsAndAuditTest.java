package com.elanor;

import com.elanor.analytics.dto.RecordAnalyticsEventRequest;
import com.elanor.audit.service.AuditLogService;
import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.entity.Role;
import com.elanor.auth.entity.User;
import com.elanor.auth.repository.RoleRepository;
import com.elanor.auth.repository.UserRepository;
import com.elanor.auth.service.AuthService;
import com.elanor.common.security.JwtTokenProvider;
import com.elanor.notification.provider.impl.DevelopmentEmailProvider;
import com.elanor.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NotificationsAnalyticsAndAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DevelopmentEmailProvider emailProvider;

    @Autowired
    private AuditLogService auditLogService;

    private String customerToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        emailProvider.clearSentEmails();

        // Customer Setup
        String email = "analytics-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Claire");
        reg.setLastName("Dubois");
        reg.setPhone("9911223344");
        AuthResponse authRes = authService.register(reg);
        customerToken = authRes.getAccessToken();

        // Admin Setup
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        User adminUser = new User();
        adminUser.setEmail("admin-audit-" + UUID.randomUUID().toString().substring(0, 8) + "@elanor.com");
        adminUser.setPasswordHash("hash");
        adminUser.setEmailVerified(true);
        adminUser.getRoles().add(adminRole);
        adminUser = userRepository.save(adminUser);

        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getId(), adminUser.getEmail(), List.of("ROLE_ADMIN"));
    }

    @Test
    public void testNotificationTemplatesAndDispatch() {
        notificationService.sendOrderConfirmation("claire@example.com", "ELN-1002", "?3,200.00");
        notificationService.sendShipmentDispatched("claire@example.com", "ELN-1002", "BlueDart", "BD-9988", "https://track.com/BD-9988");
        notificationService.sendReturnApproved("claire@example.com", "RET-501", "ELN-1002");
        notificationService.sendRefundProcessed("claire@example.com", "ELN-1002", "?3,200.00", "ORIGINAL_SOURCE");

        List<DevelopmentEmailProvider.SentEmailRecord> emails = emailProvider.getSentEmails();
        assertEquals(4, emails.size());
        assertEquals("ORDER_CONFIRMED", emails.get(0).templateName());
        assertEquals("SHIPMENT_DISPATCHED", emails.get(1).templateName());
        assertEquals("RETURN_APPROVED", emails.get(2).templateName());
        assertEquals("REFUND_PROCESSED", emails.get(3).templateName());
    }

    @Test
    public void testAuditLogCreationAndAdminQuery() throws Exception {
        auditLogService.record("admin@elanor.com", "PRICE_UPDATE", "PRODUCT_VARIANT", "VAR-778", "{\"oldPrice\":2000,\"newPrice\":2200}", "192.168.1.1");

        mockMvc.perform(get("/api/v1/admin/audit-logs?entityType=PRODUCT_VARIANT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].action").value("PRICE_UPDATE"))
                .andExpect(jsonPath("$.data.content[0].entityType").value("PRODUCT_VARIANT"));
    }

    @Test
    public void testAnalyticsEventCaptureAndAdminOverview() throws Exception {
        // 1. Record event via public API
        RecordAnalyticsEventRequest eventReq = new RecordAnalyticsEventRequest(
                "PRODUCT_VIEW", "guest-token-123", "session-xyz", "{\"productId\":\"prod-456\"}"
        );

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Fetch Admin Analytics Overview
        mockMvc.perform(get("/api/v1/admin/analytics/overview")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEventsTracked", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.totalRevenue", notNullValue()))
                .andExpect(jsonPath("$.data.totalOrders", notNullValue()));
    }

    @Test
    public void testCustomerForbiddenFromAdminTelemetry() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/analytics/overview")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }
}

package com.elanor;

import com.elanor.auth.dto.LoginRequest;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.dto.RequestOtpRequest;
import com.elanor.auth.entity.OtpChallenge;
import com.elanor.auth.entity.Role;
import com.elanor.auth.repository.OtpChallengeRepository;
import com.elanor.auth.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OtpChallengeRepository otpChallengeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setupRoles() {
        if (roleRepository.findByName("ROLE_CUSTOMER").isEmpty()) {
            roleRepository.save(new Role("ROLE_CUSTOMER", "Standard B2C Customer"));
        }
    }

    @Test
    public void shouldRegisterNewCustomerSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Elena",
                "Gilbert",
                "elena@example.com",
                "+919876543210",
                "StrongPassword123!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("elena@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Elena"));
    }

    @Test
    public void shouldFailRegistrationWhenEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Elena",
                "Gilbert",
                "duplicate@example.com",
                "+919876543210",
                "StrongPassword123!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Attempt duplicate
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }

    @Test
    public void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Stefan",
                "Salvatore",
                "stefan@example.com",
                "+919876543211",
                "Password1234!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("stefan@example.com", "Password1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("stefan@example.com"));
    }

    @Test
    public void shouldFailLoginWithInvalidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "WrongPassword!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_CREDENTIALS"));
    }

    @Test
    public void shouldHandleRequestOtpFlow() throws Exception {
        RequestOtpRequest request = new RequestOtpRequest("+919988776655");

        mockMvc.perform(post("/api/v1/auth/request-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

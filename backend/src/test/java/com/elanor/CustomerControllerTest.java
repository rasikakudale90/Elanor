package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.customer.dto.CreateAddressRequest;
import com.elanor.customer.dto.UpdateProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    public void shouldGetAndUpdateProfile() throws Exception {
        RegisterRequest register = new RegisterRequest("Caroline", "Forbes", "caroline@example.com", "+919123456780", "Password123!");
        AuthResponse auth = authService.register(register);

        // Get Profile
        mockMvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", "Bearer " + auth.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Caroline"))
                .andExpect(jsonPath("$.data.email").value("caroline@example.com"));

        // Update Profile
        UpdateProfileRequest update = new UpdateProfileRequest("Caroline", "Salvatore", "+919123456780", null, LocalDate.of(1995, 10, 10), "FEMALE");
        mockMvc.perform(put("/api/v1/customers/me")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lastName").value("Salvatore"))
                .andExpect(jsonPath("$.data.gender").value("FEMALE"));
    }

    @Test
    public void shouldManageCustomerAddresses() throws Exception {
        RegisterRequest register = new RegisterRequest("Bonnie", "Bennett", "bonnie@example.com", "+919123456781", "Password123!");
        AuthResponse auth = authService.register(register);

        // Create 1st Address
        CreateAddressRequest addr1 = new CreateAddressRequest(
                "Bonnie Bennett",
                "+919123456781",
                "123 Mystic Falls Way",
                "Apt 4B",
                "Mystic Falls",
                "Virginia",
                "20101",
                "India",
                true
        );

        mockMvc.perform(post("/api/v1/customers/me/addresses")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addr1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullName").value("Bonnie Bennett"))
                .andExpect(jsonPath("$.data.city").value("Mystic Falls"))
                .andExpect(jsonPath("$.data.default").value(true));

        // Get Addresses
        mockMvc.perform(get("/api/v1/customers/me/addresses")
                        .header("Authorization", "Bearer " + auth.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}

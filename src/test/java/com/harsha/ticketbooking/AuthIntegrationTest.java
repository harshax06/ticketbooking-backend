package com.harsha.ticketbooking;

import com.harsha.ticketbooking.repository.RefreshTokenRepository;
import com.harsha.ticketbooking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE refresh_tokens, bookings, seats, events, users, venues
            RESTART IDENTITY CASCADE
            """);
    }

    @Test
    void registerThenLoginThenAccessProtectedEndpoint() throws Exception {
        String registerBody = """
            {"name":"Test User","email":"authtest@example.com","password":"securepass123"}
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\":1,\"seatId\":1,\"userId\":1}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void publicEndpointWorksWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk());
    }
}
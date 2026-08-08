package com.wargacare.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wargacare.auth.dto.LoginRequest;
import com.wargacare.auth.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Auth Integration Tests")
class AuthIntegrationTest {

    @Container
        static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgreSQL::getJdbcUrl);
                registry.add("spring.datasource.username", postgreSQL::getUsername);
                registry.add("spring.datasource.password", postgreSQL::getPassword);
                registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("jwt.secret", () -> "test-secret-key-for-integration-testing-256bits-long");
        registry.add("jwt.expiration", () -> "86400000");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register - Berhasil mendaftarkan pengguna baru")
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Budi Santoso",
                "budi@example.com",
                "password123",
                "001",
                "005",
                "08123456789",
                "Jl. Merdeka No. 1"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("budi@example.com"))
                .andExpect(jsonPath("$.data.user.role").value("WARGA"))
                .andExpect(jsonPath("$.data.user.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/register - Gagal jika email sudah terdaftar")
    void register_DuplicateEmail_Conflict() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Ani Susanti",
                "ani@example.com",
                "password123",
                "002",
                "005",
                null,
                null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/register - Gagal jika request tidak valid")
    void register_InvalidRequest_BadRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest(
                "",
                "invalid-email",
                "123",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login - Berhasil login")
    void login_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Citra Dewi",
                "citra@example.com",
                "password123",
                "003",
                "005",
                null,
                null
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("citra@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("citra@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Gagal jika password salah")
    void login_WrongPassword_Unauthorized() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Doni Prasetyo",
                "doni@example.com",
                "correctpassword",
                null, null, null, null
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("doni@example.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/me - Berhasil mendapatkan data user dengan token valid")
    void getMe_WithValidToken_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Eka Putri",
                "eka@example.com",
                "password123",
                "004",
                "005",
                "08198765432",
                "Jl. Pahlawan No. 10"
        );
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody)
                .path("data")
                .path("accessToken")
                .asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("eka@example.com"))
                .andExpect(jsonPath("$.data.fullName").value("Eka Putri"))
                .andExpect(jsonPath("$.data.role").value("WARGA"));
    }

    @Test
    @DisplayName("GET /api/auth/me - Gagal tanpa token")
    void getMe_WithoutToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }
}

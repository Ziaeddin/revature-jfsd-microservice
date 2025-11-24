package com.zia.auth.jwt.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zia.auth.jwt.service.payload.LoginDto;
import com.zia.auth.jwt.service.payload.RegisterDto;
import com.zia.auth.jwt.service.security.CustomUserDetailsService;
import com.zia.auth.jwt.service.security.JwtTokenProvider;
import com.zia.auth.jwt.service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Simple Integration Tests for AuthController
 * We use MockMvc to test HTTP requests and responses
 * This tests the controller without starting the full server
 */
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    // MockMvc helps us test REST endpoints
    @Autowired
    private MockMvc mockMvc;

    // Create a fake (mock) AuthService
    @MockitoBean
    private AuthService authService;

    // Mock security beans needed by SecurityConfig
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // ObjectMapper converts Java objects to JSON
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test 1: POST /api/auth/register with valid data should return 201 CREATED
     */
    @Test
    @WithMockUser // This simulates a logged-in user
    void testRegister_WithValidData_ShouldReturn201() throws Exception {
        // ARRANGE
        RegisterDto registerDto = new RegisterDto(
                "John Doe",
                "john",
                "john@example.com",
                "password123",
                "ROLE_USER"
        );

        // Mock the service to return success message
        when(authService.register(any()))
                .thenReturn("User registered successfully");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf()) // Include CSRF token for security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(content().string("User registered successfully"));
    }

    /**
     * Test 2: POST /api/auth/login with valid credentials should return token
     */
    @Test
    @WithMockUser
    void testLogin_WithValidCredentials_ShouldReturnToken() throws Exception {
        // ARRANGE
        LoginDto loginDto = new LoginDto("john", "password123");
        String expectedToken = "jwt.token.here";

        // Mock the service to return a token
        when(authService.login(any()))
                .thenReturn(expectedToken);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(content().string(expectedToken));
    }

    /**
     * Test 3: Register with existing username should return error
     */
    @Test
    @WithMockUser
    void testRegister_WithExistingUsername_ShouldReturnError() throws Exception {
        // ARRANGE
        RegisterDto registerDto = new RegisterDto(
                "John Doe",
                "john",
                "john@example.com",
                "password123",
                "ROLE_USER"
        );

        // Mock the service to throw an exception
        when(authService.register(any()))
                .thenThrow(new RuntimeException("Username is already taken!"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400
                .andExpect(content().string("Username is already taken!"));
    }
}


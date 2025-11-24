package com.zia.auth.jwt.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple Unit Tests for JwtTokenProvider
 * Tests token generation and validation
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Create a real JwtTokenProvider (not a mock)
        jwtTokenProvider = new JwtTokenProvider();
    }

    /**
     * Test 1: Generate token should return a non-null token
     */
    @Test
    void testGenerateToken_ShouldReturnToken() {
        // ARRANGE
        // Create a fake Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        // ACT
        String token = jwtTokenProvider.generateToken(authentication);

        // ASSERT
        assertNotNull(token); // Token should not be null
        assertTrue(token.length() > 0); // Token should have some content
    }

    /**
     * Test 2: Get username from valid token should return correct username
     */
    @Test
    void testGetUsernameFromJWT_WithValidToken_ShouldReturnUsername() {
        // ARRANGE
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        // First generate a token
        String token = jwtTokenProvider.generateToken(authentication);

        // ACT
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        // ASSERT
        assertEquals("john", username);
    }

    /**
     * Test 3: Validate valid token should return true
     */
    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // ARRANGE
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        // Generate a valid token
        String token = jwtTokenProvider.generateToken(authentication);

        // ACT
        boolean isValid = jwtTokenProvider.validateToken(token);

        // ASSERT
        assertTrue(isValid);
    }

    /**
     * Test 4: Validate invalid token should throw exception
     */
    @Test
    void testValidateToken_WithInvalidToken_ShouldThrowException() {
        // ARRANGE
        String invalidToken = "this.is.not.a.valid.token";

        // ACT & ASSERT
        // This should throw a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtTokenProvider.validateToken(invalidToken);
        });

        // Check that exception message contains "Invalid" or "JWT"
        assertTrue(exception.getMessage().contains("JWT") ||
                   exception.getMessage().contains("Invalid"));
    }

    /**
     * Test 5: Validate empty token should throw exception
     */
    @Test
    void testValidateToken_WithEmptyToken_ShouldThrowException() {
        // ARRANGE
        String emptyToken = "";

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> {
            jwtTokenProvider.validateToken(emptyToken);
        });
    }

    /**
     * Test 6: Get username from invalid token should throw exception
     */
    @Test
    void testGetUsernameFromJWT_WithInvalidToken_ShouldThrowException() {
        // ARRANGE
        String invalidToken = "invalid.token.here";

        // ACT & ASSERT
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.getUsernameFromJWT(invalidToken);
        });
    }
}


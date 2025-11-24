package com.zia.auth.jwt.service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {
        // ARRANGE
        RuntimeException exception = new RuntimeException("Username is already taken!");

        // ACT
        ResponseEntity<String> response = handler.handleRuntimeException(exception);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken!", response.getBody());
    }

    @Test
    void testHandleGeneralException() {
        // ARRANGE
        Exception exception = new Exception("Something went wrong");

        // ACT
        ResponseEntity<String> response = handler.handleGeneralException(exception);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Something went wrong"));
    }
}


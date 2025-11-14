package com.zia.auth.jwt.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Simple test to check if the application starts correctly
 * This is a "smoke test" - just checks if everything loads without errors
 */
@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
class AuthServiceApplicationTests {

    /**
     * Test that the Spring application context loads successfully
     * If this test passes, it means all your beans and configurations are correct
     */
    @Test
    void contextLoads() {
        // If we get here without errors, the test passes!
        // This verifies that Spring can start your application
    }

}


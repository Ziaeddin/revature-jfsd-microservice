package com.zia.auth.jwt.service.repository;

import com.zia.auth.jwt.service.entity.Role;
import com.zia.auth.jwt.service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple Repository Tests for UserRepository
 * @DataJpaTest configures an in-memory database for testing
 * This is an integration test that actually interacts with a database
 */
@DataJpaTest
class UserRepositoryTest {

    // TestEntityManager helps us save test data to the database
    @Autowired
    private TestEntityManager entityManager;

    // The repository we want to test
    @Autowired
    private UserRepository userRepository;

    /**
     * Test 1: Save and find user by username
     */
    @Test
    void testFindByUsername_ShouldReturnUser() {
        // ARRANGE - Create and save a test user
        User user = new User();
        user.setName("John Doe");
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRoles(new HashSet<>());

        entityManager.persist(user); // Save to test database
        entityManager.flush(); // Make sure it's saved

        // ACT - Find the user by username
        Optional<User> foundUser = userRepository.findByUsername("john");

        // ASSERT
        assertTrue(foundUser.isPresent()); // User should be found
        assertEquals("john", foundUser.get().getUsername());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    /**
     * Test 2: Find by email should return user
     */
    @Test
    void testFindByEmail_ShouldReturnUser() {
        // ARRANGE
        User user = new User();
        user.setName("Jane Doe");
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        user.setPassword("password123");
        user.setRoles(new HashSet<>());

        entityManager.persist(user);
        entityManager.flush();

        // ACT
        Optional<User> foundUser = userRepository.findByEmail("jane@example.com");

        // ASSERT
        assertTrue(foundUser.isPresent());
        assertEquals("jane", foundUser.get().getUsername());
        assertEquals("jane@example.com", foundUser.get().getEmail());
    }

    /**
     * Test 3: Check if username exists
     */
    @Test
    void testExistsByUsername_ShouldReturnTrue() {
        // ARRANGE
        User user = new User();
        user.setName("Bob Smith");
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("password123");
        user.setRoles(new HashSet<>());

        entityManager.persist(user);
        entityManager.flush();

        // ACT
        Boolean exists = userRepository.existsByUsername("bob");

        // ASSERT
        assertTrue(exists);
    }

    /**
     * Test 4: Check if email exists
     */
    @Test
    void testExistsByEmail_ShouldReturnTrue() {
        // ARRANGE
        User user = new User();
        user.setName("Alice Brown");
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("password123");
        user.setRoles(new HashSet<>());

        entityManager.persist(user);
        entityManager.flush();

        // ACT
        Boolean exists = userRepository.existsByEmail("alice@example.com");

        // ASSERT
        assertTrue(exists);
    }

    /**
     * Test 5: Username does not exist should return false
     */
    @Test
    void testExistsByUsername_WhenNotExists_ShouldReturnFalse() {
        // ACT
        Boolean exists = userRepository.existsByUsername("nonexistent");

        // ASSERT
        assertFalse(exists);
    }

    /**
     * Test 6: Find by username or email should work
     */
    @Test
    void testFindByUsernameOrEmail_ShouldReturnUser() {
        // ARRANGE
        User user = new User();
        user.setName("Charlie Green");
        user.setUsername("charlie");
        user.setEmail("charlie@example.com");
        user.setPassword("password123");
        user.setRoles(new HashSet<>());

        entityManager.persist(user);
        entityManager.flush();

        // ACT - Search by username
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("charlie", "charlie");

        // ASSERT
        assertTrue(foundByUsername.isPresent());
        assertEquals("charlie", foundByUsername.get().getUsername());

        // ACT - Search by email
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail("charlie@example.com", "charlie@example.com");

        // ASSERT
        assertTrue(foundByEmail.isPresent());
        assertEquals("charlie@example.com", foundByEmail.get().getEmail());
    }
}


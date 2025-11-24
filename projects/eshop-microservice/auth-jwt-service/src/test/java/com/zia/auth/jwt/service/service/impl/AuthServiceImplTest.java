package com.zia.auth.jwt.service.service.impl;

import com.zia.auth.jwt.service.entity.Role;
import com.zia.auth.jwt.service.entity.User;
import com.zia.auth.jwt.service.payload.LoginDto;
import com.zia.auth.jwt.service.payload.RegisterDto;
import com.zia.auth.jwt.service.repository.RoleRepository;
import com.zia.auth.jwt.service.repository.UserRepository;
import com.zia.auth.jwt.service.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Simple Unit Tests for AuthServiceImpl
 * We use @Mock to create fake objects (mocks) for dependencies
 * We use @InjectMocks to inject those mocks into our service
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    // Create mock (fake) objects for dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    // The actual service we want to test (with mocks injected)
    @InjectMocks
    private AuthServiceImpl authService;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Any setup code can go here
    }

    /**
     * Test 1: Login with valid credentials should return a token
     */
    @Test
    void testLogin_WithValidCredentials_ShouldReturnToken() {
        // ARRANGE (Setup test data)
        LoginDto loginDto = new LoginDto("john", "password123");
        String expectedToken = "jwt.token.here";

        // Tell the mock what to return when methods are called
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication))
                .thenReturn(expectedToken);

        // ACT (Execute the method we're testing)
        String actualToken = authService.login(loginDto);

        // ASSERT (Check if the result is what we expected)
        assertNotNull(actualToken); // Token should not be null
        assertEquals(expectedToken, actualToken); // Token should match what we expected

        // Verify that our mocked methods were actually called
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    /**
     * Test 2: Register with new username should succeed
     */
    @Test
    void testRegister_WithNewUsername_ShouldSucceed() {
        // ARRANGE
        RegisterDto registerDto = new RegisterDto(
                "John Doe",
                "john",
                "john@example.com",
                "password123",
                "ROLE_USER"
        );

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        // Setup mock behavior
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // ACT
        String result = authService.register(registerDto);

        // ASSERT
        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Test 3: Register with existing username should throw exception
     */
    @Test
    void testRegister_WithExistingUsername_ShouldThrowException() {
        // ARRANGE
        RegisterDto registerDto = new RegisterDto(
                "John Doe",
                "john",
                "john@example.com",
                "password123",
                "ROLE_USER"
        );

        // Mock returns true - username already exists
        when(userRepository.existsByUsername("john")).thenReturn(true);

        // ACT & ASSERT
        // This test expects an exception to be thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerDto);
        });

        // Check the exception message
        assertEquals("Username is already taken!", exception.getMessage());

        // Verify that save was never called (because it should fail before that)
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test 4: Register with existing email should throw exception
     */
    @Test
    void testRegister_WithExistingEmail_ShouldThrowException() {
        // ARRANGE
        RegisterDto registerDto = new RegisterDto(
                "John Doe",
                "john",
                "john@example.com",
                "password123",
                "ROLE_USER"
        );

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true); // Email exists

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerDto);
        });

        assertEquals("Email is already taken!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test 5: Add new role should succeed
     */
    @Test
    void testAddNewRole_ShouldSucceed() {
        // ARRANGE
        String roleName = "ROLE_ADMIN";

        when(roleRepository.existsByName(roleName)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(new Role());

        // ACT
        String result = authService.addNewRole(roleName);

        // ASSERT
        assertEquals("Role added successfully", result);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    /**
     * Test 6: Add existing role should throw exception
     */
    @Test
    void testAddNewRole_WithExistingRole_ShouldThrowException() {
        // ARRANGE
        String roleName = "ROLE_ADMIN";
        when(roleRepository.existsByName(roleName)).thenReturn(true); // Role exists

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.addNewRole(roleName);
        });

        assertTrue(exception.getMessage().contains("is already taken!"));
        verify(roleRepository, never()).save(any(Role.class));
    }
}


package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        // Setup test data before each test
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("CANDIDATE");
        registerRequest.setPhone("9876543210");
        registerRequest.setLocation("Bangalore");
        
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CANDIDATE);
    }
    
    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        User result = authService.register(registerRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@test.com", result.getEmail());
        assertEquals(Role.CANDIDATE, result.getRole());
        
        // Verify interactions
        verify(userRepository, times(1)).findByEmail("john@test.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        
        assertEquals("Email already registered", exception.getMessage());
        
        // Verify
        verify(userRepository, times(1)).findByEmail("john@test.com");
        verify(userRepository, never()).save(any(User.class));
    }
}
package com.ibs.userservice.controller;

import com.ibs.userservice.dtos.requestDtos.AuthRequest;
import com.ibs.userservice.dtos.responseDtos.AuthResponse;
import com.ibs.userservice.entity.User; // adjust import if your User class is elsewhere
import com.ibs.userservice.security.AppUserDetails;
import com.ibs.userservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUserDetails appUserDetails;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnAuthResponse_whenAuthenticationSucceeds() {
        // Arrange
        AuthRequest request = new AuthRequest("testUser", "testPass");

        // Mock user entity inside AppUserDetails
        User userEntity = new User();
        userEntity.setUserId(1001);
        userEntity.setUserName("testUser");
        userEntity.setFullName("Test User");

        // Mock authorities
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        // Mock other behavior
        when(appUserDetails.getUsername()).thenReturn("testUser");
        when(appUserDetails.user()).thenReturn(userEntity);

        // Mock authentication
        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT token generation
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("fake-jwt-token", response.getBody().getToken());
        assertEquals("USER", response.getBody().getRole());
        assertEquals(1001, response.getBody().getUserId());
        assertEquals("testUser", response.getBody().getUserName());
        assertEquals("Test User", response.getBody().getUserFullName());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(any());
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        // Arrange
        AuthRequest request = new AuthRequest("badUser", "badPass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.login(request));
        assertTrue(ex.getMessage().contains("Invalid credentials"));
    }
}

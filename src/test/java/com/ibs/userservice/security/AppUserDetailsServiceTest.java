package com.ibs.userservice.security;

import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setRoleName("USER");

        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setUserName("john_doe");
        sampleUser.setPasswordHash("password123");
        sampleUser.setRole(role);
        sampleUser.setActive(true);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByUserName("john_doe")).thenReturn(Optional.of(sampleUser));

        // When
        UserDetails userDetails = appUserDetailsService.loadUserByUsername("john_doe");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john_doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .contains("USER"); // assuming AppUserDetails returns ROLE_ prefixed role
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUserName("unknown_user")).thenReturn(Optional.empty());

        // Then
        assertThrows(UsernameNotFoundException.class, () ->
                appUserDetailsService.loadUserByUsername("unknown_user"));
    }
}

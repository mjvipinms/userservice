package com.ibs.userservice.security;

import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock UserDetails with roles
        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        userDetails = new User("john_doe", "password123", authorities);

        // Manually inject secret and expirationMs using reflection (since @Value isn’t processed in unit tests)
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "mysecretkeymysecretkeymysecretkey12"); // 32+ chars for HS256

        Field expirationField = JwtUtil.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 3600000L); // 1 hour
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        // Generate JWT
        String token = jwtUtil.generateToken(userDetails);

        assertThat(token).isNotBlank();

        // Extract username
        String extractedUsername = jwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo("john_doe");
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtil.generateToken(userDetails);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.jwt.token";
        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid, "Token should be invalid");
    }

    @Test
    void testGenerateTokenContainsRolesClaim() {
        String token = jwtUtil.generateToken(userDetails);

        String payload = new String(java.util.Base64.getDecoder().decode(token.split("\\.")[1]));
        assertThat(payload).contains("roles");
        assertThat(payload).contains("ROLE_ADMIN");
    }
}

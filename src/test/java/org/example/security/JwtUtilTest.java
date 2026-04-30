package org.example.security;

import org.example.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String username = "testuser";

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void testGenerateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token).contains(".");  // JWT format has dots
    }

    @Test
    public void testExtractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    public void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    public void testValidateToken_WithInvalidUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "differentuser");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    public void testValidateToken_WithNullToken_ShouldThrowException() {
        // Assert
        assertThatThrownBy(() -> jwtUtil.validateToken(null, username))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void testGenerateToken_DifferentUsers_ShouldGenerateDifferentTokens() {
        // Arrange
        String username2 = "differentuser";

        // Act
        String token1 = jwtUtil.generateToken(username);
        String token2 = jwtUtil.generateToken(username2) ;

        // Assert
        assertThat(token1).isNotEqualTo(token2);
    }
}


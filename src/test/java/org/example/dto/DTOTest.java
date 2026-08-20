package org.example.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class DTOTest {

    private AppUserDTO appUserDTO;
    private AuthDTO authDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    public void setUp() {
        appUserDTO = new AppUserDTO();
        authDTO = new AuthDTO();
    }

    // AppUserDTO Tests
    @Test
    public void testAppUserDTOConstruction_ShouldHaveDefaultValues() {
        // Assert
        assertThat(appUserDTO.getFullName()).isNull();
        assertThat(appUserDTO.getEmail()).isNull();
        assertThat(appUserDTO.getPassword()).isNull();
    }

    @Test
    public void testAppUserDTOSetAndGetFullName_ShouldWorkCorrectly() {
        // Arrange
        String fullName = "John Doe";

        // Act
        appUserDTO.setFullName(fullName);

        // Assert
        assertThat(appUserDTO.getFullName()).isEqualTo(fullName);
    }

    @Test
    public void testAppUserDTOSetAndGetEmail_ShouldWorkCorrectly() {
        // Arrange
        String email = "john@example.com";

        // Act
        appUserDTO.setEmail(email);

        // Assert
        assertThat(appUserDTO.getEmail()).isEqualTo(email);
    }

    @Test
    public void testAppUserDTOSetAndGetPassword_ShouldWorkCorrectly() {
        // Arrange
        String password = "password123";

        // Act
        appUserDTO.setPassword(password);

        // Assert
        assertThat(appUserDTO.getPassword()).isEqualTo(password);
    }

    @Test
    public void testAppUserDTOSetAllFields_ShouldWorkCorrectly() {
        // Arrange
        String fullName = "Jane Doe";
        String email = "jane@example.com";
        String password = "securePass456";

        // Act
        appUserDTO.setFullName(fullName);
        appUserDTO.setEmail(email);
        appUserDTO.setPassword(password);

        // Assert
        assertThat(appUserDTO.getFullName()).isEqualTo(fullName);
        assertThat(appUserDTO.getEmail()).isEqualTo(email);
        assertThat(appUserDTO.getPassword()).isEqualTo(password);
    }

    // AuthDTO Tests
    @Test
    public void testAuthDTOConstruction_ShouldHaveDefaultValues() {
        // Assert
        assertThat(authDTO.getEmail()).isNull();
        assertThat(authDTO.getPassword()).isNull();
    }

    @Test
    public void testAuthDTOSetAndGetEmail_ShouldWorkCorrectly() {
        // Arrange
        String email = "test@example.com";

        // Act
        authDTO.setEmail(email);

        // Assert
        assertThat(authDTO.getEmail()).isEqualTo(email);
    }

    @Test
    public void testAuthDTOSetAndGetPassword_ShouldWorkCorrectly() {
        // Arrange
        String password = "password123";

        // Act
        authDTO.setPassword(password);

        // Assert
        assertThat(authDTO.getPassword()).isEqualTo(password);
    }

    @Test
    public void testAuthDTOSetAllFields_ShouldWorkCorrectly() {
        // Arrange
        String email = "user123@example.com";
        String password = "pass456";

        // Act
        authDTO.setEmail(email);
        authDTO.setPassword(password);

        // Assert
        assertThat(authDTO.getEmail()).isEqualTo(email);
        assertThat(authDTO.getPassword()).isEqualTo(password);
    }

    // AuthResponseDTO Tests
    @Test
    public void testAuthResponseDTOConstructionWithArgs_ShouldSetValues() {
        // Arrange
        String token = "jwt-token";
        String message = "Success";

        // Act
        AuthResponseDTO response = new AuthResponseDTO(token, message);

        // Assert
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    public void testAuthResponseDTOSetAndGetToken_ShouldWorkCorrectly() {
        // Arrange
        authResponseDTO = new AuthResponseDTO(null, null);
        String token = "jwt-token-string";

        // Act
        authResponseDTO.setToken(token);

        // Assert
        assertThat(authResponseDTO.getToken()).isEqualTo(token);
    }

    @Test
    public void testAuthResponseDTOSetAndGetMessage_ShouldWorkCorrectly() {
        // Arrange
        authResponseDTO = new AuthResponseDTO(null, null);
        String message = "Login successful";

        // Act
        authResponseDTO.setMessage(message);

        // Assert
        assertThat(authResponseDTO.getMessage()).isEqualTo(message);
    }

    @Test
    public void testAuthResponseDTOSetAllFields_ShouldWorkCorrectly() {
        // Arrange
        authResponseDTO = new AuthResponseDTO(null, null);
        String token = "long-jwt-token-string";
        String message = "Authentication successful";

        // Act
        authResponseDTO.setToken(token);
        authResponseDTO.setMessage(message);

        // Assert
        assertThat(authResponseDTO.getToken()).isEqualTo(token);
        assertThat(authResponseDTO.getMessage()).isEqualTo(message);
    }

    @Test
    public void testAuthResponseDTOWithNullToken_ShouldAllowNull() {
        // Arrange
        authResponseDTO = new AuthResponseDTO("some-token", "message");

        // Act
        authResponseDTO.setToken(null);

        // Assert
        assertThat(authResponseDTO.getToken()).isNull();
    }

    @Test
    public void testAuthResponseDTOWithErrorMessage_ShouldStoreMessage() {
        // Arrange
        authResponseDTO = new AuthResponseDTO(null, null);
        String errorMessage = "Error: Invalid credentials";

        // Act
        authResponseDTO.setMessage(errorMessage);

        // Assert
        assertThat(authResponseDTO.getMessage()).isEqualTo(errorMessage);
    }
}




package org.example.service;

import org.example.config.CurrentUser;
import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;
import org.example.model.ConfirmationToken;
import org.example.model.Role;
import org.example.repository.ConfirmationTokenRepository;
import org.example.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private AppUserDTO testUserDTO;
    private AuthDTO testAuthDTO;
    private AppUser testUser;
    private String email = "testuser@example.com";

    @BeforeEach
    public void setUp() {
        testUserDTO = new AppUserDTO();
        testUserDTO.setFullName("Test User");
        testUserDTO.setEmail(email);
        testUserDTO.setPassword("password123");

        testAuthDTO = new AuthDTO();
        testAuthDTO.setEmail(email);
        testAuthDTO.setPassword("password123");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail(email);
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
        testUser.setConfirmed(true);
        testUser.setActive(true);
    }

    @Test
    public void testRegisterUser_WithNewUser_ShouldReturnSuccessResponse() {
        // Arrange
        when(userService.findByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(AppUser.class))).thenReturn(testUser);

        // Act
        ResponseDTO response = authService.registerUser(testUserDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).contains("Success");
        verify(userService, times(1)).saveUser(any(AppUser.class));
        verify(passwordEncoder, times(1)).encode("password123");
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
        verify(emailService, times(1)).sendConfirmationEmail(any(AppUser.class), anyString());
    }

    @Test
    public void testRegisterUser_WithExistingEmail_ShouldReturnErrorResponse() {
        // Arrange
        when(userService.findByEmail(email)).thenReturn(testUser);

        // Act
        ResponseDTO response = authService.registerUser(testUserDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).contains("already taken");
        verify(userService, never()).saveUser(any());
    }

    @Test
    public void testLoginUser_WithValidCredentials_ShouldReturnSuccessResponse() {
        // Arrange
        when(userService.findByEmail(email)).thenReturn(testUser);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(email)).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.loginUser(testAuthDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getMessage()).isEqualTo("Success");
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(email);
    }

    @Test
    public void testLoginUser_WithInvalidCredentials_ShouldReturnErrorResponse() {
        // Arrange
        when(userService.findByEmail(email)).thenReturn(testUser);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        AuthResponseDTO response = authService.loginUser(testAuthDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("Error");
        assertThat(response.getToken()).isNull();
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    public void testLoginUser_WithUnconfirmedAccount_ShouldReturnErrorResponse() {
        // Arrange
        AppUser unconfirmedUser = new AppUser();
        unconfirmedUser.setEmail(email);
        unconfirmedUser.setConfirmed(false);
        when(userService.findByEmail(email)).thenReturn(unconfirmedUser);

        // Act
        AuthResponseDTO response = authService.loginUser(testAuthDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("not confirmed");
        assertThat(response.getToken()).isNull();
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    public void testRegisterUser_ShouldEncodePassword() {
        // Arrange
        when(userService.findByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(AppUser.class))).thenReturn(testUser);

        // Act
        authService.registerUser(testUserDTO);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userService, times(1)).saveUser(argThat(user ->
                user.getPassword().equals("encodedPassword")
        ));
    }
}

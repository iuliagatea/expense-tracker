package org.example.service;

import org.example.config.CurrentUser;
import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Role;
import org.example.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private AuthServiceImpl authService;

    private AppUserDTO testUserDTO;
    private AuthDTO testAuthDTO;
    private AppUser testUser;
    private Category testCategory;
    private String username = "testuser";

    @BeforeEach
    public void setUp() {
        testUserDTO = new AppUserDTO();
        testUserDTO.setFullName("Test User");
        testUserDTO.setUsername(username);
        testUserDTO.setPassword("password123");

        testAuthDTO = new AuthDTO();
        testAuthDTO.setUsername(username);
        testAuthDTO.setPassword("password123");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername(username);
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        currentUser = new CurrentUser();
        currentUser.setCurrentUser(testUser);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setUser(testUser);
    }

    @Test
    public void testRegisterUser_WithNewUser_ShouldReturnSuccessResponse() {
        // Arrange
        when(userService.findByUsename(username)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(AppUser.class))).thenReturn(testUser);
        when(categoryService.addCategory(any(Category.class))).thenReturn(testCategory);
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.registerUser(testUserDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getMessage()).isEqualTo("Success");
        verify(userService, times(1)).saveUser(any(AppUser.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    public void testRegisterUser_WithExistingUsername_ShouldReturnErrorResponse() {
        // Arrange
        when(userService.findByUsename(username)).thenReturn(testUser);

        // Act
        AuthResponseDTO response = authService.registerUser(testUserDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("error");
        assertThat(response.getMessage()).contains("already taken");
        assertThat(response.getToken()).isNull();
        verify(userService, never()).saveUser(any());
    }

    @Test
    public void testLoginUser_WithValidCredentials_ShouldReturnSuccessResponse() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.loginUser(testAuthDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getMessage()).isEqualTo("Success");
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(username);
    }

    @Test
    public void testLoginUser_WithInvalidCredentials_ShouldReturnErrorResponse() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        AuthResponseDTO response = authService.loginUser(testAuthDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("Error");
        assertThat(response.getMessage()).contains("invalid username or password");
        assertThat(response.getToken()).isNull();
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    public void testRegisterUser_ShouldSetUserRoleToUSER() {
        // Arrange
        when(userService.findByUsename(username)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setRole(Role.USER);

        when(userService.saveUser(any(AppUser.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        authService.registerUser(testUserDTO);

        // Assert
        verify(userService, times(1)).saveUser(argThat(user ->
                user.getRole() == Role.USER
        ));
    }

    @Test
    public void testLoginUser_ShouldGenerateTokenWithCorrectUsername() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        authService.loginUser(testAuthDTO);

        // Assert
        verify(jwtUtil, times(1)).generateToken(username);
    }

    @Test
    public void testRegisterUser_ShouldEncodePassword() {
        // Arrange
        when(userService.findByUsename(username)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(AppUser.class))).thenReturn(testUser);
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        authService.registerUser(testUserDTO);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userService, times(1)).saveUser(argThat(user ->
                user.getPassword().equals("encodedPassword")
        ));
    }
}






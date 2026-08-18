package org.example.service;

import org.example.model.AppUser;
import org.example.model.Role;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AppUser testUser;

    @BeforeEach
    public void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("testuser@test.com");
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    public void testSaveUser_ShouldSaveAndReturnUser() {
        // Arrange
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        AppUser savedUser = userService.saveUser(testUser);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("testuser@test.com");
        assertThat(savedUser.getFullName()).isEqualTo("Test User");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testFindByUsername_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("testuser@test.com")).thenReturn(Optional.of(testUser));

        // Act
        AppUser foundUser = userService.findByEmail("testuser@test.com");

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("testuser@test.com");
        verify(userRepository, times(1)).findByEmail("testuser@test.com");
    }

    @Test
    public void testFindByEmail_WithNonExistingUser_ShouldReturnNull() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // Act
        AppUser foundUser = userService.findByEmail("nonexistent@test.com");

        // Assert
        assertThat(foundUser).isNull();
        verify(userRepository, times(1)).findByEmail("nonexistent@test.com");
    }

    @Test
    public void testFindUserById_ShouldReturnEmptyOptional() {
        // Act
        Optional<AppUser> result = userService.findUserById(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void testSaveUser_WithMultipleUsers_ShouldSaveAll() {
        // Arrange
        AppUser user2 = new AppUser();
        user2.setId(2L);
        user2.setEmail("user2@test.com");
        user2.setFullName("User Two");
        user2.setRole(Role.USER);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(user2)).thenReturn(user2);

        // Act
        AppUser saved1 = userService.saveUser(testUser);
        AppUser saved2 = userService.saveUser(user2);

        // Assert
        assertThat(saved1.getEmail()).isEqualTo("testuser@test.com");
        assertThat(saved2.getEmail()).isEqualTo("user2@test.com");
        verify(userRepository, times(2)).save(any(AppUser.class));
    }
}


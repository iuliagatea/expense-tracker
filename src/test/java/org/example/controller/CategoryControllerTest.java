package org.example.controller;

import org.example.config.CurrentUser;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Role;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryController categoryController;

    private AppUser testUser;
    private Category category;
    private Category category2;
    private Category updatedCategory;

    @BeforeEach
    public void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setUser(testUser);
        category2 = new Category();
        category2.setId(2L);
        category2.setName("Transport");
        category2.setUser(testUser);

        updatedCategory = new Category();
        updatedCategory.setName("Updated Food");

        CurrentUser currentUser = new CurrentUser();
        currentUser.setCurrentUser(testUser);
    }

    @Test
    public void testGetCategories_ShouldReturnAllUserCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(category, category2);
        when(categoryService.getAllUserCategories(1L)).thenReturn(categories);

        // Act
        ResponseEntity<List<Category>> response = categoryController.getCategories(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(category, category2);
        verify(categoryService, times(1)).getAllUserCategories(1L);
    }

    @Test
    public void testAddCategory_ShouldReturnCreatedExpense() {
        // Arrange
        when(categoryService.addCategory(any(Category.class))).thenReturn(category);

        // Act
        ResponseEntity<Category> response = categoryController.addCategory(category, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(categoryService, times(1)).addCategory(any(Category.class));
    }

    @Test
    public void testUpdateCategory_WithValidExpense_ShouldReturnUpdatedExpense() {
        // Arrange
//        when(categoryService.getCategoryById(1L, 1L)).thenReturn(Optional.ofNullable(category));
        when(categoryService.updateCategory(any(Category.class), eq(1L))).thenReturn(true);

        // Act
        ResponseEntity<Category> response = categoryController.updateCategory(1L, updatedCategory, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(categoryService, times(1)).updateCategory(any(Category.class), eq(1L));
    }

    @Test
//    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpdateCategory_WithInvalidExpense_ShouldReturnNotFound() {
        // Arrange
//        when(categoryService.getCategoryById(999L, 1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Category> response = categoryController.updateCategory(999L, updatedCategory, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteCategory_WithValidExpense_ShouldReturnNoContent() {
        // Arrange
        when(categoryService.deleteCategory(1L, 1L)).thenReturn(true);

        // Act
        ResponseEntity<Category> response = categoryController.deleteCategory(1L, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService, times(1)).deleteCategory(1L, 1L);
    }

    @Test
    public void testDeleteCategory_WithInvalidExpense_ShouldReturnNotFound() {
        // Arrange
        when(categoryService.deleteCategory(999L, 1L)).thenReturn(false);

        // Act
        ResponseEntity<Category> response = categoryController.deleteCategory(999L, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(categoryService, times(1)).deleteCategory(999L, 1L);
    }
}

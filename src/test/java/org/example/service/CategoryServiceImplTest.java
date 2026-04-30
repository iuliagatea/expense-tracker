package org.example.service;

import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Role;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category foodCategory;
    private Category transportCategory;
    private AppUser testUser;

    @BeforeEach
    public void setUp() {
        foodCategory = new Category();
        foodCategory.setId(1L);
        foodCategory.setName("Food");
        foodCategory.setDescription("Food and groceries");

        transportCategory = new Category();
        transportCategory.setId(2L);
        transportCategory.setName("Transport");
        transportCategory.setDescription("Transportation expenses");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);

        foodCategory.setUser(testUser);
        transportCategory.setUser(testUser);
    }

    @Test
    public void testAddCategory_ShouldSaveAndReturnCategory() {
        // Arrange
        when(categoryRepository.save(foodCategory)).thenReturn(foodCategory);

        // Act
        Category savedCategory = categoryService.addCategory(foodCategory);

        // Assert
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isEqualTo(1L);
        assertThat(savedCategory.getName()).isEqualTo("Food");
        verify(categoryRepository, times(1)).save(foodCategory);
    }

    @Test
    public void testGetCategoryById_WithExistingCategory_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(foodCategory));

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryById(1L, 1L);

        // Assert
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(1L);
        assertThat(foundCategory.get().getName()).isEqualTo("Food");
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    public void testGetCategoryById_WithNonExistingCategory_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryById(999L, 1L);

        // Assert
        assertThat(foundCategory).isEmpty();
        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
    }

    @Test
    public void testGetAllUserCategories_ShouldReturnAllCategoriesForUser() {
        // Arrange
        List<Category> categories = Arrays.asList(foodCategory, transportCategory);
        when(categoryRepository.findByUserId(1L)).thenReturn(categories);

        // Act
        List<Category> userCategories = categoryService.getAllUserCategories(1L);

        // Assert
        assertThat(userCategories).hasSize(2);
        assertThat(userCategories).contains(foodCategory, transportCategory);
        verify(categoryRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetAllUserCategories_WithNoCategories_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findByUserId(999L)).thenReturn(Arrays.asList());

        // Act
        List<Category> userCategories = categoryService.getAllUserCategories(999L);

        // Assert
        assertThat(userCategories).isEmpty();
        verify(categoryRepository, times(1)).findByUserId(999L);
    }

    @Test
    public void testUpdateCategory_WithValidCategory_ShouldReturnTrue() {
        // Arrange
        foodCategory.setDescription("Updated Food Category");
        when(categoryRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(foodCategory));

        // Act
        boolean isUpdated = categoryService.updateCategory(foodCategory, 1L);

        // Assert
        assertThat(isUpdated).isTrue();
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(categoryRepository, times(1)).save(foodCategory);
    }

    @Test
    public void testUpdateCategory_WithNonExistingCategory_ShouldReturnFalse() {
        // Arrange
        Category nonExistingCategory = new Category();
        nonExistingCategory.setId(999L);
        nonExistingCategory.setName("Non Existing");
        when(categoryRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // Act
        boolean isUpdated = categoryService.updateCategory(nonExistingCategory, 1L);

        // Assert
        assertThat(isUpdated).isFalse();
        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void testDeleteCategory_WithValidCategory_ShouldReturnTrue() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(foodCategory));

        // Act
        boolean isDeleted = categoryService.deleteCategory(1L, 1L);

        // Assert
        assertThat(isDeleted).isTrue();
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCategory_WithNonExistingCategory_ShouldReturnFalse() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // Act
        boolean isDeleted = categoryService.deleteCategory(999L, 1L);

        // Assert
        assertThat(isDeleted).isFalse();
        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    public void testUpdateCategory_ShouldPreserveUserOwnership() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Food");
        AppUser anotherUser = new AppUser();
        anotherUser.setId(999L);
        updatedCategory.setUser(anotherUser); // Try to change user

        when(categoryRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(foodCategory));

        // Act
        boolean isUpdated = categoryService.updateCategory(updatedCategory, 1L);

        // Assert
        assertThat(isUpdated).isTrue();
        verify(categoryRepository, times(1)).save(argThat(category ->
                category.getUser().getId().equals(1L) // Should preserve original user
        ));
    }
}


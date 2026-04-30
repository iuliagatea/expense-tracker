package org.example.controller;

import org.example.dto.AuthResponseDTO;
import org.example.dto.ExpenseDTO;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.Role;
import org.example.service.CategoryService;
import org.example.service.ExpenseService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseController expenseController;

    private Category foodCategory;
    private Category transportCategory;
    private AppUser testUser;
    private Expense testExpense1;
    private Expense testExpense2;
    private ExpenseDTO testExpenseDTO1;
    private ExpenseDTO testExpenseDTO2;

    @BeforeEach
    public void setUp() {
        foodCategory = new Category();
        foodCategory.setName("Food");
        transportCategory = new Category();
        transportCategory.setName("Transport");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);

        testExpense1 = new Expense();
        testExpense1.setId(1L);
        testExpense1.setDate("2024-04-07");
        testExpense1.setCategory(foodCategory);
        testExpense1.setAmount(25.50);
        testExpense1.setUser(testUser);

        testExpense2 = new Expense();
        testExpense2.setId(2L);
        testExpense2.setDate("2024-04-06");
        testExpense2.setCategory(transportCategory);
        testExpense2.setAmount(15.00);
        testExpense2.setUser(testUser);

        testExpenseDTO1 = new ExpenseDTO();
        testExpenseDTO1.setDate("2024-04-07");
        testExpenseDTO1.setCategoryId(1L);
        testExpenseDTO1.setAmount("25.50");
        testExpenseDTO1.setExpenseType(0);

        testExpenseDTO2 = new ExpenseDTO();
        testExpenseDTO2.setDate("2024-04-06");
        testExpenseDTO2.setCategoryId(2L);
        testExpenseDTO2.setAmount("15.00");
        testExpenseDTO2.setExpenseType(0);
    }

    @Test
    public void testGetExpenseById_ShouldReturnExpense() {
        // Arrange
        when(expenseService.getExpenseById(1L, 1L)).thenReturn(Optional.of(testExpense1));

        // Act
        ResponseEntity<Optional<Expense>> response = expenseController.getExpenseById(1L, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent();
        assertThat(response.getBody().get().getId()).isEqualTo(1L);
        verify(expenseService, times(1)).getExpenseById(1L, 1L);
    }

    @Test
    public void testGetExpenses_ShouldReturnAllUserExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseService.getAllUserExpenses(1L)).thenReturn(expenses);

        // Act
        ResponseEntity<List<Expense>> response = expenseController.getExpenses(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(testExpense1, testExpense2);
        verify(expenseService, times(1)).getAllUserExpenses(1L);
    }

    @Test
    public void testAddExpense_ShouldReturnCreatedExpense() {
        // Arrange
        when(categoryService.getCategoryById(1L, 1L)).thenReturn(Optional.of(foodCategory));
        when(expenseService.addExpense(any(Expense.class))).thenReturn(testExpense1);

        // Act
        ResponseEntity<Expense> response = expenseController.addExpense(testExpenseDTO1, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(expenseService, times(1)).addExpense(any(Expense.class));
    }

    @Test
    public void testUpdateExpense_WithValidExpense_ShouldReturnUpdatedExpense() {
        // Arrange
        when(expenseService.getExpenseById(1L, 1L)).thenReturn(Optional.of(testExpense1));
        when(categoryService.getCategoryById(1L, 1L)).thenReturn(Optional.of(foodCategory));
        when(expenseService.updateExpense(any(Expense.class), eq(1L))).thenReturn(true);

        // Act
        ResponseEntity<Expense> response = expenseController.updateExpense(1L, testExpenseDTO1, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(expenseService, times(1)).updateExpense(any(Expense.class), eq(1L));
    }

    @Test
    public void testUpdateExpense_WithInvalidExpense_ShouldReturnNotFound() {
        // Arrange
        when(expenseService.getExpenseById(999L, 1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Expense> response = expenseController.updateExpense(999L, testExpenseDTO1, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteExpense_WithValidExpense_ShouldReturnNoContent() {
        // Arrange
        when(expenseService.deleteExpense(1L, 1L)).thenReturn(true);

        // Act
        ResponseEntity<Expense> response = expenseController.deleteExpense(1L, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expenseService, times(1)).deleteExpense(1L, 1L);
    }

    @Test
    public void testDeleteExpense_WithInvalidExpense_ShouldReturnNotFound() {
        // Arrange
        when(expenseService.deleteExpense(999L, 1L)).thenReturn(false);

        // Act
        ResponseEntity<Expense> response = expenseController.deleteExpense(999L, testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(expenseService, times(1)).deleteExpense(999L, 1L);
    }

    @Test
    public void testGetAllExpenseCategories_ShouldReturnCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(foodCategory, transportCategory);
        when(expenseService.getAllExpenseCategories(1L)).thenReturn(categories);

        // Act
        ResponseEntity<List<String>> response = expenseController.getAllExpenseCategories(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains("Food", "Transport");
        verify(expenseService, times(1)).getAllExpenseCategories(1L);
    }

    @Test
    public void testGetAllExpenseCategories_WithNoCategories_ShouldReturnNoContent() {
        // Arrange
        when(expenseService.getAllExpenseCategories(1L)).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<String>> response = expenseController.getAllExpenseCategories(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void testGetExpenseByDay_ShouldReturnExpensesForDate() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1);
        when(expenseService.getExpenseByDate("2024-04-07", 1L)).thenReturn(expenses);

        // Act
        ResponseEntity<List<Expense>> response = expenseController.getExpenseByDay("2024-04-07", testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getDate()).isEqualTo("2024-04-07");
        verify(expenseService, times(1)).getExpenseByDate("2024-04-07", 1L);
    }

    @Test
    public void testGetExpenseByCategoryAndMonth_ShouldReturnFilteredExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1);
        when(expenseService.getExpenseByCategoryIdAndMonth(1L, "2024-04", 1L))
                .thenReturn(expenses);

        // Act
        ResponseEntity<List<Expense>> response = expenseController.getExpenseByCategoryIdAndMonth(
                1L, "2024-04", testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCategory().getName()).isEqualTo("Food");
        verify(expenseService, times(1)).getExpenseByCategoryIdAndMonth(1L, "2024-04", 1L);
    }
}


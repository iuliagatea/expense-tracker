package org.example.service;

import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.Role;
import org.example.repository.ExpenseRepository;
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
public class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Category foodCategory;
    private Category transportCategory;
    private AppUser testUser;
    private Expense testExpense1;
    private Expense testExpense2;

    @BeforeEach
    public void setUp() {
        foodCategory = new Category();
        foodCategory.setId(1L);
        foodCategory.setName("Food");
        transportCategory = new Category();
        transportCategory.setId(2L);
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
    }

    @Test
    public void testAddExpense_ShouldSaveAndReturnExpense() {
        // Arrange
        when(expenseRepository.save(testExpense1)).thenReturn(testExpense1);

        // Act
        Expense savedExpense = expenseService.addExpense(testExpense1);

        // Assert
        assertThat(savedExpense).isNotNull();
        assertThat(savedExpense.getId()).isEqualTo(1L);
        assertThat(savedExpense.getCategory().getName()).isEqualTo("Food");
        verify(expenseRepository, times(1)).save(testExpense1);
    }

    @Test
    public void testGetExpenseById_WithExistingExpense_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testExpense1));

        // Act
        Optional<Expense> foundExpense = expenseService.getExpenseById(1L, 1L);

        // Assert
        assertThat(foundExpense).isPresent();
        assertThat(foundExpense.get().getId()).isEqualTo(1L);
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    public void testGetExpenseById_WithNonExistingExpense_ShouldReturnEmpty() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Expense> foundExpense = expenseService.getExpenseById(999L, 1L);

        // Assert
        assertThat(foundExpense).isEmpty();
        verify(expenseRepository, times(1)).findByIdAndUserId(999L, 1L);
    }

    @Test
    public void testGetAllUserExpenses_ShouldReturnAllExpensesForUser() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Expense> userExpenses = expenseService.getAllUserExpenses(1L);

        // Assert
        assertThat(userExpenses).hasSize(2);
        assertThat(userExpenses).contains(testExpense1, testExpense2);
        verify(expenseRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    public void testGetExpenseByDate_ShouldReturnExpensesForSpecificDate() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Expense> filteredExpenses = expenseService.getExpenseByDate("2024-04-07", 1L);

        // Assert
        assertThat(filteredExpenses).hasSize(1);
        assertThat(filteredExpenses.get(0).getDate()).isEqualTo("2024-04-07");
        verify(expenseRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    public void testGetExpenseByDate_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Expense> filteredExpenses = expenseService.getExpenseByDate("2024-01-01", 1L);

        // Assert
        assertThat(filteredExpenses).isEmpty();
    }

    @Test
    public void testGetAllExpenseCategories_ShouldReturnDistinctCategories() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Category> categories = expenseService.getAllExpenseCategories(1L);

        // Assert
        assertThat(categories).hasSize(2);
//        assertThat(categories).contains("Food", "Transport");
        verify(expenseRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    public void testGetExpenseByCategoryAndMonth_ShouldReturnFilteredExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Expense> filteredExpenses = expenseService.getExpenseByCategoryIdAndMonth(1L, "2024-04", 1L);

        // Assert
        assertThat(filteredExpenses).hasSize(1);
        assertThat(filteredExpenses.get(0).getCategory().getName()).isEqualTo("Food");
        assertThat(filteredExpenses.get(0).getDate()).startsWith("2024-04");
    }

    @Test
    public void testUpdateExpense_WithValidExpense_ShouldReturnTrue() {
        // Arrange
        testExpense1.setAmount(30.00);
        when(expenseRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testExpense1));

        // Act
        boolean isUpdated = expenseService.updateExpense(testExpense1, 1L);

        // Assert
        assertThat(isUpdated).isTrue();
        verify(expenseRepository, times(1)).save(testExpense1);
    }

    @Test
    public void testUpdateExpense_WithNonExistingExpense_ShouldReturnFalse() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        testExpense1.setId(999L);

        // Act
        boolean isUpdated = expenseService.updateExpense(testExpense1, 1L);

        // Assert
        assertThat(isUpdated).isFalse();
        verify(expenseRepository, never()).save(any());
    }

    @Test
    public void testDeleteExpense_WithValidExpense_ShouldReturnTrue() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testExpense1));

        // Act
        boolean isDeleted = expenseService.deleteExpense(1L, 1L);

        // Assert
        assertThat(isDeleted).isTrue();
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteExpense_WithNonExistingExpense_ShouldReturnFalse() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // Act
        boolean isDeleted = expenseService.deleteExpense(999L, 1L);

        // Assert
        assertThat(isDeleted).isFalse();
        verify(expenseRepository, never()).deleteById(any());
    }

    @Test
    public void testGetAllExpenseCategories_WithDuplicateCategories_ShouldReturnDistinct() {
        // Arrange
        Expense expense3 = new Expense();
        expense3.setId(3L);
        expense3.setCategory(foodCategory); // Duplicate category
        expense3.setDate("2024-04-05");
        expense3.setUser(testUser);

        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2, expense3);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenses);

        // Act
        List<Category> categories = expenseService.getAllExpenseCategories(1L);

        // Assert
        assertThat(categories).hasSize(2); // Should only have 2 distinct categories
        assertThat(categories).doesNotHaveDuplicates();
    }
}




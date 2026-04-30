package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CategoryTest {

    private Category category;
    private AppUser testUser;

    @BeforeEach
    public void setUp() {
        category = new Category();
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    public void testCategoryConstruction_ShouldHaveDefaultValues() {
        // Assert
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
        assertThat(category.getDescription()).isNull();
        assertThat(category.getUser()).isNull();
        assertThat(category.getExpenses()).isNull();
    }

    @Test
    public void testSetAndGetId_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;

        // Act
        category.setId(id);

        // Assert
        assertThat(category.getId()).isEqualTo(1L);
    }

    @Test
    public void testSetAndGetName_ShouldWorkCorrectly() {
        // Arrange
        String name = "Food";

        // Act
        category.setName(name);

        // Assert
        assertThat(category.getName()).isEqualTo("Food");
    }

    @Test
    public void testSetAndGetDescription_ShouldWorkCorrectly() {
        // Arrange
        String description = "Food and groceries expenses";

        // Act
        category.setDescription(description);

        // Assert
        assertThat(category.getDescription()).isEqualTo("Food and groceries expenses");
    }

    @Test
    public void testSetAndGetUser_ShouldWorkCorrectly() {
        // Act
        category.setUser(testUser);

        // Assert
        assertThat(category.getUser()).isNotNull();
        assertThat(category.getUser().getId()).isEqualTo(1L);
        assertThat(category.getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void testSetAndGetExpenses_ShouldWorkCorrectly() {
        // Arrange
        List<Expense> expenses = new ArrayList<>();
        Expense expense1 = new Expense();
        expense1.setId(1L);
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expenses.add(expense1);
        expenses.add(expense2);

        // Act
        category.setExpenses(expenses);

        // Assert
        assertThat(category.getExpenses()).hasSize(2);
        assertThat(category.getExpenses()).contains(expense1, expense2);
    }

    @Test
    public void testSetAllFields_ShouldWorkCorrectly() {
        // Arrange
        String name = "Transport";
        String description = "Transportation and taxi expenses";
        List<Expense> expenses = new ArrayList<>();

        // Act
        category.setId(2L);
        category.setName(name);
        category.setDescription(description);
        category.setUser(testUser);
        category.setExpenses(expenses);

        // Assert
        assertThat(category.getId()).isEqualTo(2L);
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getDescription()).isEqualTo(description);
        assertThat(category.getUser()).isEqualTo(testUser);
        assertThat(category.getExpenses()).isEqualTo(expenses);
    }

    @Test
    public void testCategoryWithMultipleExpenses_ShouldMaintainList() {
        // Arrange
        List<Expense> expenses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Expense expense = new Expense();
            expense.setId((long) i);
            expenses.add(expense);
        }
        category.setName("Utilities");

        // Act
        category.setExpenses(expenses);

        // Assert
        assertThat(category.getExpenses()).hasSize(5);
        assertThat(category.getName()).isEqualTo("Utilities");
    }
}


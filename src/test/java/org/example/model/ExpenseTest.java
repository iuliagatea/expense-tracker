package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ExpenseTest {

    private Expense expense;
    private AppUser testUser;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        expense = new Expense();
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
    }

    @Test
    public void testExpenseConstruction_ShouldHaveDefaultValues() {
        // Assert
        assertThat(expense.getId()).isNull();
        assertThat(expense.getExpenseType()).isEqualTo(0);
        assertThat(expense.getDate()).isNull();
        assertThat(expense.getAmount()).isEqualTo(0.0);
        assertThat(expense.getCategory()).isNull();
        assertThat(expense.getAccount()).isNull();
        assertThat(expense.getNote()).isNull();
        assertThat(expense.getUser()).isNull();
    }

    @Test
    public void testSetAndGetId_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;

        // Act
        expense.setId(id);

        // Assert
        assertThat(expense.getId()).isEqualTo(1L);
    }

    @Test
    public void testSetAndGetExpenseType_ShouldWorkCorrectly() {
        // Arrange
        int expenseType = 5;

        // Act
        expense.setExpenseType(expenseType);

        // Assert
        assertThat(expense.getExpenseType()).isEqualTo(5);
    }

    @Test
    public void testSetAndGetDate_ShouldWorkCorrectly() {
        // Arrange
        String date = "2024-04-07";

        // Act
        expense.setDate(date);

        // Assert
        assertThat(expense.getDate()).isEqualTo("2024-04-07");
    }

    @Test
    public void testSetAndGetAmount_ShouldWorkCorrectly() {
        // Arrange
        double amount = 25.50;

        // Act
        expense.setAmount(amount);

        // Assert
        assertThat(expense.getAmount()).isEqualTo(25.50);
    }

    @Test
    public void testSetAndGetCategory_ShouldWorkCorrectly() {
        // Act
        expense.setCategory(testCategory);

        // Assert
        assertThat(expense.getCategory()).isEqualTo(testCategory);
        assertThat(expense.getCategory().getName()).isEqualTo("Food");
    }

    @Test
    public void testSetAndGetAccount_ShouldWorkCorrectly() {
        // Arrange
        String account = "Credit Card";

        // Act
        expense.setAccount(account);

        // Assert
        assertThat(expense.getAccount()).isEqualTo("Credit Card");
    }

    @Test
    public void testSetAndGetNote_ShouldWorkCorrectly() {
        // Arrange
        String note = "Lunch with friends";

        // Act
        expense.setNote(note);

        // Assert
        assertThat(expense.getNote()).isEqualTo("Lunch with friends");
    }

    @Test
    public void testSetAndGetUser_ShouldWorkCorrectly() {
        // Act
        expense.setUser(testUser);

        // Assert
        assertThat(expense.getUser()).isNotNull();
        assertThat(expense.getUser().getId()).isEqualTo(1L);
        assertThat(expense.getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void testSetAllFields_ShouldWorkCorrectly() {
        // Arrange
        String date = "2024-04-07";
        double amount = 35.75;
        String account = "Cash";
        String note = "Grocery shopping";
        int expenseType = 1;

        // Act
        expense.setId(1L);
        expense.setDate(date);
        expense.setCategory(testCategory);
        expense.setAmount(amount);
        expense.setAccount(account);
        expense.setNote(note);
        expense.setExpenseType(expenseType);
        expense.setUser(testUser);

        // Assert
        assertThat(expense.getId()).isEqualTo(1L);
        assertThat(expense.getDate()).isEqualTo(date);
        assertThat(expense.getCategory()).isEqualTo(testCategory);
        assertThat(expense.getAmount()).isEqualTo(amount);
        assertThat(expense.getAccount()).isEqualTo(account);
        assertThat(expense.getNote()).isEqualTo(note);
        assertThat(expense.getExpenseType()).isEqualTo(expenseType);
        assertThat(expense.getUser()).isEqualTo(testUser);
    }

    @Test
    public void testNegativeAmount_ShouldBeAccepted() {
        // Act
        expense.setAmount(-10.00);

        // Assert
        assertThat(expense.getAmount()).isEqualTo(-10.00);
    }

    @Test
    public void testZeroAmount_ShouldBeAccepted() {
        // Act
        expense.setAmount(0.0);

        // Assert
        assertThat(expense.getAmount()).isEqualTo(0.0);
    }

    @Test
    public void testLargeAmount_ShouldBeAccepted() {
        // Act
        expense.setAmount(9999.99);

        // Assert
        assertThat(expense.getAmount()).isEqualTo(9999.99);
    }
}


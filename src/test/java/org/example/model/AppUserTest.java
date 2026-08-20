package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class AppUserTest {

    private AppUser appUser;

    @BeforeEach
    public void setUp() {
        appUser = new AppUser();
    }

    @Test
    public void testAppUserConstruction_ShouldHaveDefaultValues() {
        // Assert
        assertThat(appUser.getId()).isNull();
        assertThat(appUser.getFullName()).isNull();
        assertThat(appUser.getEmail()).isNull();
        assertThat(appUser.getPassword()).isNull();
        assertThat(appUser.getRole()).isNull();
    }

    @Test
    public void testSetAndGetEmail_ShouldWorkCorrectly() {
        // Arrange
        String email = "test@example.com";

        // Act
        appUser.setEmail(email);

        // Assert
        assertThat(appUser.getEmail()).isEqualTo(email);
    }

    @Test
    public void testSetAndGetPassword_ShouldWorkCorrectly() {
        // Arrange
        String password = "encodedPassword";

        // Act
        appUser.setPassword(password);

        // Assert
        assertThat(appUser.getPassword()).isEqualTo(password);
    }

    @Test
    public void testSetAndGetFullName_ShouldWorkCorrectly() {
        // Arrange
        String fullName = "John Doe";

        // Act
        appUser.setFullName(fullName);

        // Assert
        assertThat(appUser.getFullName()).isEqualTo(fullName);
    }

    @Test
    public void testSetAndGetRole_ShouldWorkCorrectly() {
        // Arrange
        Role role = Role.ADMIN;

        // Act
        appUser.setRole(role);

        // Assert
        assertThat(appUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    public void testSetAndGetExpenses_ShouldWorkCorrectly() {
        // Arrange
        List<Expense> expenses = new ArrayList<>();
        Expense expense = new Expense();
        expense.setId(1L);
        expenses.add(expense);

        // Act
        appUser.setExpenses(expenses);

        // Assert
        assertThat(appUser.getExpenses()).hasSize(1);
        assertThat(appUser.getExpenses().get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void testAppUserWithMultipleExpenses_ShouldContainAll() {
        // Arrange
        List<Expense> expenses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Expense expense = new Expense();
            expense.setId((long) i);
            expenses.add(expense);
        }

        // Act
        appUser.setExpenses(expenses);

        // Assert
        assertThat(appUser.getExpenses()).hasSize(5);
    }

    @Test
    public void testSetId_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;

        // Act
        appUser.setId(id);

        // Assert
        assertThat(appUser.getId()).isEqualTo(1L);
    }
}


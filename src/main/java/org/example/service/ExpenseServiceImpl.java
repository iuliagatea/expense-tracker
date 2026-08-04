package org.example.service;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService{

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

//    private static final AtomicLong idCounter = new AtomicLong();

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }


    @Override
    public List<Expense> getExpenseByDate(String date, Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getDate().equalsIgnoreCase(date))
                .toList();
    }

    @Override
    public List<Expense> getExpenseByCategoryIdAndMonth(Long categoryId, String month, Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getCategory().getId().equals(categoryId) &&
                        expense.getDate().startsWith(month)).toList();
    }

    @Override
    public List<Expense> getExpenseByMonth(String month, Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getDate().startsWith(month)).toList();
    }

    @Override
    public List<Category> getAllExpenseCategories(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(Expense::getCategory)
                .distinct()
                .toList();
    }

    @Override
    public Optional<Expense> getExpenseById(Long id, Long userId) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .stream().filter(
                expense -> expense.getId().equals(id)
        ).findFirst();
    }

    @Override
    public List<Expense> getAllUserExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<Expense> getExpenseByType(Integer type, Long userId) {
        return expenseRepository.findByExpenseTypeAndUserId(type, userId);
    }

    @Override
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public boolean updateExpense(Expense updatedExpense, Long userId) {
        Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(updatedExpense.getId(), userId);
        if (existingExpense.isPresent()) {
            updatedExpense.setUser(existingExpense.get().getUser());
            expenseRepository.save(updatedExpense);

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteExpense(Long id, Long userId) {
        Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(id, userId);
        if(existingExpense.isPresent()) {
            expenseRepository.deleteById(id);

            return true;
        }

        return false;
    }

}

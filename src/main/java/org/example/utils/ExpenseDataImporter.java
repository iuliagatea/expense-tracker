package org.example.utils;

import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.service.CategoryService;
import org.example.service.ExpenseService;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class ExpenseDataImporter {
    public static final String[] ACCEPTED_FILE_TYPE = {"csv", "xls", "xlsx"};
    private final CategoryService categoryService;
    private final ExpenseService expenseService;

    public ExpenseDataImporter(CategoryService categoryService, ExpenseService expenseService) {
        this.categoryService = categoryService;
        this.expenseService = expenseService;
    }

    public void importFile(String file, AppUser user) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader file_in = new BufferedReader(fr);
            String line;
            while ((line = file_in.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length >= 5) {
                        Expense expense = new Expense();
                        expense.setDate(values[0]);

                        double amount = Double.parseDouble(values[1]);
                        int expenseType = 0;
                        if ((amount < 0) || (Long.parseLong(values[5])) == 1) {
                            expenseType = 1;
                        }

                        if (amount < 0) {
                            amount = amount * (-1);
                        }

                        expense.setAmount(amount);

                        Category category = categoryService.getCategoryByName(values[2], user.getId()).orElse(null);
                        if(category == null){
                            category = new Category();
                            category.setName(values[2]);
                            category.setUser(user);
                            categoryService.addCategory(category);
                        }
                        expense.setCategory(category);
                        expense.setAccount(values[3]);
                        expense.setNote(values[4]);
                        expense.setExpenseType(expenseType);
                        expense.setUser(user);
                        expenseService.addExpense(expense);
                    }
            }
            deleteFile(file);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        try {
            boolean result = Files.deleteIfExists(fileToDelete.toPath());
            if (result) {
                System.out.println("File Deleted Successfully");
            } else {
                System.out.println("File Not Found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.annotation.CurrentUser;
import org.example.dto.ExpenseDTO;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.service.CategoryService;
import org.example.service.ExpenseService;
import org.example.utils.ExpenseDataImporter;
import org.example.utils.UserExpensePDFExporter;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    public ExpenseController(ExpenseService expenseService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Expense>> getExpenseById(@PathVariable Long id, @CurrentUser AppUser user) {
        return ResponseEntity.ok(expenseService.getExpenseById(id, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(@CurrentUser AppUser user) {
        return ResponseEntity.ok(expenseService.getAllUserExpenses(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseDTO expenseDTO, @CurrentUser AppUser user) {
        // Convert DTO to Expense entity
        Expense expense = new Expense();
        expense.setDate(expenseDTO.getDate());
        expense.setAmount(Double.parseDouble(expenseDTO.getAmount()));
        expense.setAccount(expenseDTO.getAccount());
        expense.setNote(expenseDTO.getNote());
        expense.setExpenseType(expenseDTO.getExpenseType());

        // Get the category by ID
        Optional<Category> category = categoryService.getCategoryById(expenseDTO.getCategoryId(), user.getId());
        if (category.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        expense.setCategory(category.get());
        expense.setUser(user);

        Expense newExpense = expenseService.addExpense(expense);

        return new ResponseEntity<>(newExpense, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
                                                 @RequestBody ExpenseDTO expenseDTO, @CurrentUser AppUser user) {
        // Get existing expense
        Optional<Expense> existingExpenseOpt = expenseService.getExpenseById(id, user.getId());
        if (existingExpenseOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Expense existingExpense = existingExpenseOpt.get();

        // Update fields from DTO
        existingExpense.setDate(expenseDTO.getDate());
        existingExpense.setAmount(Double.parseDouble(expenseDTO.getAmount()));
        existingExpense.setAccount(expenseDTO.getAccount());
        existingExpense.setNote(expenseDTO.getNote());
        existingExpense.setExpenseType(expenseDTO.getExpenseType());

        // Update category if provided
        if (expenseDTO.getCategoryId() != null) {
            Optional<Category> category = categoryService.getCategoryById(expenseDTO.getCategoryId(), user.getId());
            if (category.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingExpense.setCategory(category.get());
        }

        if(expenseService.updateExpense(existingExpense, user.getId())) {
            return new ResponseEntity<>(existingExpense, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable Long id, @CurrentUser AppUser user) {
        HttpStatus httpStatus = expenseService.deleteExpense(id, user.getId())
                ? HttpStatus.NO_CONTENT
                : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(httpStatus);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllExpenseCategories(@CurrentUser AppUser user) {
        List<Category> categories = expenseService.getAllExpenseCategories(user.getId());

        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(categories.stream().map(Category::getName).toList());
    }

    @GetMapping("/day/{date}")
    public ResponseEntity<List<Expense>> getExpenseByDay(@PathVariable String date, @CurrentUser AppUser user) {
        List<Expense> expenses = expenseService.getExpenseByDate(date, user.getId());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/category/{category}/month")
    public ResponseEntity<List<Expense>> getExpenseByCategoryIdAndMonth(@PathVariable Long categoryId, @RequestParam String month, @CurrentUser AppUser user) {
        return ResponseEntity.ok(expenseService.getExpenseByCategoryIdAndMonth(categoryId, month, user.getId()));
    }

    @GetMapping("expense_type/{type}")
    public ResponseEntity<List<Expense>> getExpensesByType(@PathVariable Integer type, @CurrentUser AppUser user) {
        return ResponseEntity.ok(expenseService.getExpenseByType(type, user.getId()));
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response, @CurrentUser AppUser user) throws IOException {
        response.setContentType("application/pdf");
        String currentDateTime = String.valueOf(System.currentTimeMillis());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Expense> listExpenses = expenseService.getAllUserExpenses(user.getId());

        UserExpensePDFExporter exporter = new UserExpensePDFExporter(listExpenses);
        exporter.export(response);
    }

    @PostMapping("/import")
    public String importExpenses(@RequestParam(name = "file") @NonNull MultipartFile file, @CurrentUser AppUser user) throws IOException {
        // Setting up the path of the file
        String filePath = System.getProperty("user.dir") + "/uploads" + File.separator + file.getOriginalFilename();
        String fileUploadStatus;

        // Try block to check exceptions
        try {
            // Creating an object of FileOutputStream class
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());

            // Closing the connection
            fout.close();

            ExpenseDataImporter importer = new ExpenseDataImporter(categoryService, expenseService);
            importer.importFile(filePath, user);

            fileUploadStatus = "File Imported Successfully";
        }

        // Catch block to handle exceptions
        catch (Exception e) {
            e.printStackTrace();
            fileUploadStatus =  "Error in uploading file: " + e;
        }
        return fileUploadStatus;
    }
}

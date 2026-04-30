package org.example.controller;

import org.example.annotation.CurrentUser;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(@CurrentUser AppUser user) {
        return ResponseEntity.ok(categoryService.getAllUserCategories(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category, @CurrentUser AppUser user) {
        category.setUser(user);
        Category newCategory = categoryService.addCategory(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @RequestBody Category categoryDetails,
                                                   @CurrentUser AppUser user) {
        categoryDetails.setId(id);
        if(categoryService.updateCategory(categoryDetails, user.getId())) {
            return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id, @CurrentUser AppUser user) {
        HttpStatus httpStatus = categoryService.deleteCategory(id, user.getId())
                ? HttpStatus.NO_CONTENT
                : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(httpStatus);
    }
}

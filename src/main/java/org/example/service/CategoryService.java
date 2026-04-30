package org.example.service;

import org.example.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllUserCategories(Long userId);
    Optional<Category> getCategoryById(Long id, Long userId);
    Optional<Category> getCategoryByName(String name, Long userId);
    Category addCategory(Category category);

    boolean updateCategory(Category category, Long userId);
    boolean deleteCategory(Long id, Long userId);
}

package org.example.service;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Override
    public List<Category> getAllUserCategories(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Optional<Category> getCategoryById(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Optional<Category> getCategoryByName(String name, Long userId) {
        return categoryRepository.findByNameAndUserId(name, userId);
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public boolean updateCategory(Category updatedCategory, Long userId) {
        Optional<Category> existingCategory = categoryRepository.findByIdAndUserId(updatedCategory.getId(), userId);
        if (existingCategory.isPresent()) {
            updatedCategory.setUser(existingCategory.get().getUser());
            categoryRepository.save(updatedCategory);

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteCategory(Long id, Long userId) {
        Optional<Category> existingCategory = categoryRepository.findByIdAndUserId(id, userId);
        if(existingCategory.isPresent()) {
            categoryRepository.deleteById(id);

            return true;
        }

        return false;
    }
}

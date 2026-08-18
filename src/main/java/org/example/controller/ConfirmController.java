package org.example.controller;

import org.example.model.Category;
import org.example.model.ConfirmationToken;
import org.example.model.AppUser;
import org.example.repository.ConfirmationTokenRepository;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ConfirmController {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public ConfirmController(ConfirmationTokenRepository confirmationTokenRepository, UserService userService, CategoryService categoryService) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        Optional<ConfirmationToken> opt = confirmationTokenRepository.findByToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or missing token");
        }
        ConfirmationToken ct = opt.get();
        if (ct.getExpiresAt() != null && ct.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }
        AppUser user = ct.getUser();
        user.setConfirmed(true);
        userService.saveUser(user);

        // create default categories
        Arrays.asList("Food", "Transport", "Travel", "Household", "Health",
                "Social life", "Gift", "Apparel", "Education", "Beauty", "Other").forEach(categoryName -> {
            Category category = new Category();
            category.setName(categoryName);
            category.setUser(user);
            categoryService.addCategory(category);
        });

        confirmationTokenRepository.deleteByToken(token);
        return ResponseEntity.ok("Email confirmed successfully");
    }
}

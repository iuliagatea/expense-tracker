package org.example.controller;

import org.example.model.Category;
import org.example.model.ConfirmationToken;
import org.example.model.AppUser;
import org.example.repository.ConfirmationTokenRepository;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
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
    @Transactional
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        String hashedToken = hashToken(token);
        Optional<ConfirmationToken> opt = confirmationTokenRepository.findByToken(hashedToken);
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

        Arrays.asList("Food", "Transport", "Travel", "Household", "Health",
                "Social life", "Gift", "Apparel", "Education", "Beauty", "Other").forEach(categoryName -> {
            Category category = new Category();
            category.setName(categoryName);
            category.setUser(user);
            categoryService.addCategory(category);
        });
        System.out.println("User confirmed: " + user.getEmail());
        confirmationTokenRepository.deleteByToken(hashedToken);
        return ResponseEntity.ok("Email confirmed successfully");

    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(token.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte item : bytes) {
                builder.append(String.format("%02x", item));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash confirmation token.", e);
        }
    }
}

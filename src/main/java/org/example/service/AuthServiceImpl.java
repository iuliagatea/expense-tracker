package org.example.service;

import org.example.config.CurrentUser;
import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Role;
import org.example.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CategoryService categoryService;
    private final CurrentUser currentUser;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CategoryService categoryService, CurrentUser currentUser) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.categoryService = categoryService;
        this.currentUser = currentUser;
    }

    @Override
    public AuthResponseDTO registerUser(AppUserDTO appUserDTO) {
        if(userService.findByEmail(appUserDTO.getEmail()) != null) {
            return new AuthResponseDTO(null, "error: Email is already taken");
        }

        AppUser appUser = new AppUser();

        appUser.setFullName(appUserDTO.getFullName());
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setRole(Role.USER);
        appUser.setActive(true);
        appUser.setConfirmed(false);

        userService.saveUser(appUser);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setEmail(appUserDTO.getEmail());
        authDTO.setPassword(appUserDTO.getPassword());

        Arrays.asList("Food", "Transport", "Travel", "Household", "Health",
                "Social life", "Gift", "Apparel", "Education", "Beauty", "Other").forEach(categoryName -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    category.setUser(appUser);
                    categoryService.addCategory(category);
        });
        return loginUser(authDTO);
    }

    @Override
    public AuthResponseDTO loginUser(AuthDTO authDTO) {
        try{
            String email = authDTO.getEmail().toLowerCase().trim();

            if (email.isBlank()) {
                return new AuthResponseDTO(null, "Error: invalid email or password");
            }

            AppUser appUser;

            if (email.contains("@")) {
                // Email login: validate, lookup email ->  authenticate
                if (!isValidEmail(email)) {
                    return new AuthResponseDTO(null, "Error: invalid email format");
                }
            }
            appUser = userService.findByEmail(email);
            if (appUser == null) {
                return new AuthResponseDTO(null, "Error: invalid email or password");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    appUser.getEmail(),
                    authDTO.getPassword()
            ));

            // For email path, appUser is set and validated above
            final String token = jwtUtil.generateToken(appUser.getEmail());
            currentUser.setCurrentUser(appUser);
            return new AuthResponseDTO(token, "Success");
        } catch (BadCredentialsException e) {
            return new AuthResponseDTO(null, "Error: invalid email or password");
        }
    }

    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches();
    }
}

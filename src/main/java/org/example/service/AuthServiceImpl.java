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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

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
        if(userService.findByUsename(appUserDTO.getUsername()) != null) {
            return new AuthResponseDTO(null, "error: Username is already taken");
        }

        AppUser appUser = new AppUser();

        appUser.setFullName(appUserDTO.getFullName());
        appUser.setUsername(appUserDTO.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setRole(Role.USER);

        userService.saveUser(appUser);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(appUserDTO.getUsername());
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
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            authDTO.getUsername(),
                            authDTO.getPassword()
                    ));

            AppUser appUser = userService.findByUsename(authDTO.getUsername());
            currentUser.setCurrentUser(appUser);
            final String token = jwtUtil.generateToken(authDTO.getUsername());
            return new AuthResponseDTO(token, "Success");
        } catch (BadCredentialsException e) {
            return new AuthResponseDTO(null, "Error: invalid username or password");
        }
    }
}

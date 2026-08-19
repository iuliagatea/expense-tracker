package org.example.service;

import org.example.config.CurrentUser;
import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;
import org.example.model.Role;
import org.example.model.ConfirmationToken;
import org.example.repository.ConfirmationTokenRepository;
import org.example.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CurrentUser currentUser;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CurrentUser currentUser, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.currentUser = currentUser;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public ResponseDTO registerUser(AppUserDTO appUserDTO) {
        if(userService.findByEmail(appUserDTO.getEmail()) != null) {
            return new ResponseDTO(false, "Error: Email is already taken.");
        }

        AppUser appUser = new AppUser();

        appUser.setFullName(appUserDTO.getFullName());
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setRole(Role.USER);
        appUser.setActive(true);
        appUser.setConfirmed(false);

        userService.saveUser(appUser);

        // create confirmation token
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(appUser);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        confirmationTokenRepository.save(token);

        // send email (console-based fallback)
        emailService.sendConfirmationEmail(appUser, token.getToken());

        return new ResponseDTO(true, "Success: registration complete. Please check you email to confirm your account.");
    }

    @Override
    public AuthResponseDTO loginUser(AuthDTO authDTO) {
        try{
            String email = authDTO.getEmail().toLowerCase().trim();

            if (email.isBlank()) {
                return new AuthResponseDTO(null, "Error: invalid email or password.");
            }

            if (!isValidEmail(email)) {
                return new AuthResponseDTO(null, "Error: invalid email format.");
            }
            AppUser appUser = userService.findByEmail(email);
            if (appUser == null) {
                return new AuthResponseDTO(null, "Error: invalid email or password.");
            }

            if (appUser.getConfirmed() == null || !appUser.getConfirmed()) {
                return new AuthResponseDTO(null, "Error: account not confirmed.");
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
            return new AuthResponseDTO(null, "Error: invalid email or password.");
        }
    }

    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches();
    }
}

package org.example.service;

import org.example.config.CurrentUser;
import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;
import org.example.model.ConfirmationToken;
import org.example.model.Role;
import org.example.repository.ConfirmationTokenRepository;
import org.example.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int RESET_TOKEN_EXPIRY_MINUTES = 30;

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
        if (userService.findByEmail(appUserDTO.getEmail()) != null) {
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

        String confirmationTokenValue = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(hashToken(confirmationTokenValue));
        token.setUser(appUser);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        confirmationTokenRepository.save(token);

        // Email is sent asynchronously in background thread
        emailService.sendConfirmationEmail(appUser, confirmationTokenValue);

        return new ResponseDTO(true, "Success: registration complete. Please check you email to confirm your account.");
    }

    @Override
    public AuthResponseDTO loginUser(AuthDTO authDTO) {
        try {
            String email = authDTO.getEmail() == null ? "" : authDTO.getEmail().toLowerCase().trim();

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

            final String token = jwtUtil.generateToken(appUser.getEmail());
            currentUser.setCurrentUser(appUser);
            return new AuthResponseDTO(token, "Success");
        } catch (BadCredentialsException e) {
            return new AuthResponseDTO(null, "Error: invalid email or password.");
        }
    }

    @Override
    public ResponseDTO requestPasswordReset(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (!isValidEmail(normalizedEmail)) {
            return new ResponseDTO(false, "Error: invalid email address.");
        }

        AppUser appUser = userService.findByEmail(normalizedEmail);
        if (appUser == null) {
            return new ResponseDTO(true, "If an account exists for that email, a password reset link has been sent.");
        }

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        List<ConfirmationToken> existingTokens = confirmationTokenRepository.findByUser(appUser);
        confirmationTokenRepository.deleteAll(existingTokens);

        ConfirmationToken resetToken = new ConfirmationToken();
        resetToken.setToken(tokenHash);
        resetToken.setUser(appUser);
        resetToken.setCreatedAt(Instant.now());
        resetToken.setExpiresAt(Instant.now().plus(RESET_TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES));
        confirmationTokenRepository.save(resetToken);

        // Email is sent asynchronously in background thread
        emailService.sendPasswordResetEmail(appUser, rawToken);

        return new ResponseDTO(true, "If an account exists for that email, a password reset link has been sent.");
    }

    @Override
    public ResponseDTO resetPassword(String email, String token, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        if (!isValidEmail(normalizedEmail)) {
            return new ResponseDTO(false, "Error: invalid email address.");
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            return new ResponseDTO(false, "Error: password must be at least 6 characters long.");
        }

        AppUser appUser = userService.findByEmail(normalizedEmail);
        if (appUser == null) {
            return new ResponseDTO(false, "Error: invalid or expired reset link.");
        }

        String normalizedToken = token == null ? "" : token.trim();
        if (normalizedToken.isEmpty()) {
            return new ResponseDTO(false, "Error: invalid or expired reset link.");
        }

        Optional<ConfirmationToken> resetToken = confirmationTokenRepository.findByToken(hashToken(normalizedToken));
        if (resetToken.isEmpty()) {
            return new ResponseDTO(false, "Error: invalid or expired reset link.");
        }

        ConfirmationToken matchingToken = resetToken.get();
        if (!matchingToken.getUser().getId().equals(appUser.getId())) {
            return new ResponseDTO(false, "Error: invalid or expired reset link.");
        }

        if (matchingToken.getExpiresAt().isBefore(Instant.now())) {
            confirmationTokenRepository.delete(matchingToken);
            return new ResponseDTO(false, "Error: reset link has expired. Please request a new one.");
        }

        appUser.setPassword(passwordEncoder.encode(newPassword.trim()));
        userService.saveUser(appUser);
        confirmationTokenRepository.delete(matchingToken);

        return new ResponseDTO(true, "Password reset successfully.");
    }

    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches();
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.toLowerCase().trim();
    }

    private static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(token.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash password reset token.", e);
        }
    }
}

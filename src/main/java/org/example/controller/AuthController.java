package org.example.controller;

import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.ForgotPasswordDTO;
import org.example.dto.ResetPasswordDTO;
import org.example.dto.ResponseDTO;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(
            @RequestBody AppUserDTO appUserDTO) {
        ResponseDTO response = authService.registerUser(appUserDTO);
        if ("success".equalsIgnoreCase(response.getMessage().substring(0, 7))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthDTO authDTO) {
        AuthResponseDTO response = authService.loginUser(authDTO);

        if ("success".equalsIgnoreCase(response.getMessage().substring(0, 7))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        ResponseDTO response = authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody ResetPasswordDTO request) {
        ResponseDTO response = authService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        if (Boolean.TRUE.equals(response.getSuccess())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}

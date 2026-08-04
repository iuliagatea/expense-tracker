package org.example.controller;

import org.example.dto.PasswordChangeDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.annotation.CurrentUser;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseDTO> changePassword(
            @RequestBody PasswordChangeDTO passwordChangeDTO,
            @CurrentUser AppUser currentUser) {
        ResponseDTO response = userService.changePassword(currentUser, passwordChangeDTO);

        return ResponseEntity.ok(response);
    }
}

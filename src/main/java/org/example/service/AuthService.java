package org.example.service;

import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.ResponseDTO;

public interface AuthService {
    ResponseDTO registerUser(AppUserDTO appUserDTO);
    AuthResponseDTO loginUser(AuthDTO authDTO);
}

package org.example.service;

import org.example.dto.PasswordChangeDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;

import java.util.Optional;

public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser findByUsername(String username);
    ResponseDTO changePassword(AppUser user, PasswordChangeDTO passwordChangeDTO);
    Optional<AppUser> findUserById(Long id);
}

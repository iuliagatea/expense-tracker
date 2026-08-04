package org.example.service;

import org.example.dto.PasswordChangeDTO;
import org.example.dto.ResponseDTO;
import org.example.model.AppUser;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseDTO changePassword(AppUser user, PasswordChangeDTO passwordChangeDTO) {
        if (passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
            userRepository.save(user);
            return new ResponseDTO(true, "Password changed successfully");
        }
        return new ResponseDTO(false,"Current password does not match");
    }

    @Override
    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Optional<AppUser> findUserById(Long id) {
        return Optional.empty();
    }
}

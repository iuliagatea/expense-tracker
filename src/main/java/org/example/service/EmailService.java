package org.example.service;

import org.example.model.AppUser;

public interface EmailService {
    void sendConfirmationEmail(AppUser user, String token);
}

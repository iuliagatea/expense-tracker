package org.example.service;

import org.example.model.AppUser;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> sendConfirmationEmail(AppUser user, String token);
    CompletableFuture<Void> sendPasswordResetEmail(AppUser user, String token);
}

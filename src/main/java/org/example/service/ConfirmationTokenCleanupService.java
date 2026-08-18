package org.example.service;

import org.example.repository.ConfirmationTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ConfirmationTokenCleanupService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenCleanupService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    // Run every hour, first run after 1 minute
    @Scheduled(initialDelay = 60000L, fixedRate = 3600000L)
    public void cleanupExpiredTokens() {
        confirmationTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}

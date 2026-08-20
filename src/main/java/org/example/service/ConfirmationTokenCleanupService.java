package org.example.service;

import org.example.repository.ConfirmationTokenRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ConfirmationTokenCleanupService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenCleanupService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    // Run every hour, first run after 1 minute. Cleanup is executed in background thread pool.
    @Scheduled(initialDelay = 60000L, fixedRate = 3600000L)
    @Async("taskExecutor")
    public void cleanupExpiredTokens() {
        confirmationTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}

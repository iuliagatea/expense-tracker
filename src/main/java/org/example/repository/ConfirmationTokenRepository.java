package org.example.repository;

import org.example.model.AppUser;
import org.example.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    List<ConfirmationToken> findByUser(AppUser user);
    void deleteByToken(String token);
    void deleteAllByExpiresAtBefore(Instant time);
}

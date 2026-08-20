package org.example.service;

import org.example.model.AppUser;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Asynchronously calculates user statistics (total users, active users, etc.).
     * Executes in background thread pool to avoid blocking admin endpoints.
     */
    @Async("taskExecutor")
    public CompletableFuture<Map<String, Object>> calculateUserStatistics() {
        log.info("Starting async user statistics calculation");
        List<AppUser> allUsers = userRepository.findAll();
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("total_users", allUsers.size());
        stats.put("active_users", allUsers.parallelStream()
                .filter(AppUser::getActive)
                .count());
        stats.put("confirmed_users", allUsers.parallelStream()
                .filter(AppUser::getConfirmed)
                .count());
        
        log.info("Completed user statistics calculation: {}", stats);
        return CompletableFuture.completedFuture(stats);
    }

    /**
     * Asynchronously deactivates inactive users (no expenses).
     * Useful for batch cleanup of unused accounts.
     */
    @Async("taskExecutor")
    public CompletableFuture<Integer> deactivateInactiveUsers() {
        log.info("Starting async deactivation of inactive users");
        List<AppUser> allUsers = userRepository.findAll();
        
        int deactivatedCount = (int) allUsers.parallelStream()
                .filter(user -> user.getExpenses() == null || user.getExpenses().isEmpty())
                .peek(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                })
                .count();
        
        log.info("Deactivated {} inactive users", deactivatedCount);
        return CompletableFuture.completedFuture(deactivatedCount);
    }
}

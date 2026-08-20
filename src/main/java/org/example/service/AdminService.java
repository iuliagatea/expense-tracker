package org.example.service;

import org.example.model.AppUser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AdminService {
    List<AppUser> getAllUsers();
    CompletableFuture<Map<String, Object>> calculateUserStatistics();
    CompletableFuture<Integer> deactivateInactiveUsers();
}

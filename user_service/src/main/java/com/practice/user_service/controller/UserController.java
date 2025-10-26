package com.practice.user_service.controller;

import com.practice.user_service.dto.UserResponseDto;
import com.practice.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * Get all users
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestHeader HttpHeaders header) {
        log.info("Fetching all users");
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Delete all user by username
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteAllUsers(@RequestHeader HttpHeaders header) {
//       TODO-  Placeholder for delete all users logic
        log.info("Deleting all user");
        return ResponseEntity.ok("Deleting all user");
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{userName}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId,@RequestHeader HttpHeaders header) {
        log.info("Fetching user by ID: {}", userId);
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username,@RequestHeader HttpHeaders header) {
        log.info("Fetching user by username: {}", username);
        UserResponseDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running");
    }
}
package com.practice.auth_service.controller;

import com.practice.auth_service.dto.UserDto;
import com.practice.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user-related endpoints.
 * Accessible by authenticated users with ROLE_USER or ADMIN.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Get current authenticated user's profile information.
     * 
     * @param authentication Spring Security Authentication object containing user details
     * @return ResponseEntity with UserDto containing user profile information
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserDto userDto = userService.getUserProfile(username);
        return ResponseEntity.ok(userDto);
    }
}
package com.practice.auth_service.controller;

import com.practice.auth_service.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for admin-only endpoints.
 * Accessible only by users with ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    /**
     * Test endpoint to verify admin access.
     * This endpoint demonstrates role-based access control.
     * 
     * @return ResponseEntity with a success message
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> adminTest() {
        MessageResponse response = MessageResponse.builder()
                .message("Admin access granted! This endpoint is protected.")
                .build();
        return ResponseEntity.ok(response);
    }
}
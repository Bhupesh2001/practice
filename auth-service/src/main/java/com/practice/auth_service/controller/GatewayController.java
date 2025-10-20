package com.practice.auth_service.controller;

import com.practice.auth_service.dto.ErrorResponse;
import com.practice.auth_service.dto.GatewayValidationResponse;
import com.practice.auth_service.entity.User;
import com.practice.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for API Gateway authentication validation
 * This endpoint is called by the API Gateway to validate tokens
 * and retrieve user information for downstream services
 */
@RestController
@RequestMapping("/api/auth/v1/gateway")
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final UserRepository userRepository;

    /**
     * Called by API Gateway to validate JWT and return user details.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateForGateway() {
        log.info("=== Gateway validation endpoint called ===");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return unauthorized("Anonymous authentication", "No valid JWT token provided");
        }

        String username = authentication.getName();
        log.info("Username from authentication: {}", username);

        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.warn("❌ User not found in database for username: {}", username);
                return unauthorized("User not found", "User does not exist in database");
            }

            GatewayValidationResponse response = GatewayValidationResponse.builder()
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .isAuthenticated(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            log.info("✅ Returning success response for userId: {}", user.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Exception while validating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Internal server error", e.getMessage()));
        }
    }

    private static ErrorResponse buildErrorResponse(String error, String message) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), error, message, "/api/auth/v1/gateway/validate", System.currentTimeMillis());
    }

    private ResponseEntity<ErrorResponse> unauthorized(String error, String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorResponse(error, message));
    }
}

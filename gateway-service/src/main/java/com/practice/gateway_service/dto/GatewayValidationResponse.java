package com.practice.gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Gateway validation endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayValidationResponse {
    
    private String userId;
    private String username;
    private String email;
    private String role;
    private Boolean isAuthenticated;
    private Long timestamp;
}
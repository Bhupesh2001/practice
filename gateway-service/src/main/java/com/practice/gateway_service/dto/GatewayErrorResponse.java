package com.practice.gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response for gateway validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GatewayErrorResponse {
    
    private String error;
    private String message;
    private Long timestamp;
    private Integer status;
}
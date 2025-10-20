package com.practice.gateway_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GatewayAuthResponse {
    private String userId;
    private String username;
    private String role;

}
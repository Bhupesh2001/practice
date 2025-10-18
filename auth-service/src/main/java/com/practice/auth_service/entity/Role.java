package com.practice.auth_service.entity;

/**
 * Role enum representing user roles in the system.
 * Used for Role-Based Access Control (RBAC).
 */
public enum Role {
    /**
     * Regular user role with standard permissions
     */
    USER,
    
    /**
     * Administrator role with elevated permissions
     */
    ADMIN
}
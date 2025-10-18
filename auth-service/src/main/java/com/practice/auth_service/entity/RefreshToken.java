package com.practice.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * RefreshToken entity for managing long-lived refresh tokens.
 * Refresh tokens are used to obtain new access tokens without re-authentication.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The unique token string (UUID)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String token;
    
    /**
     * One-to-one relationship with User entity
     * Each user can have only one active refresh token
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    /**
     * Token expiration timestamp
     */
    @Column(nullable = false, name = "expiry_date")
    private Instant expiryDate;
    
    /**
     * Token creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    /**
     * Automatically set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
    
    /**
     * Check if the refresh token has expired
     * @return true if token is expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
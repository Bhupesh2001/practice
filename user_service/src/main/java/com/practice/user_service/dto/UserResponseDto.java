package com.practice.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for User Profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
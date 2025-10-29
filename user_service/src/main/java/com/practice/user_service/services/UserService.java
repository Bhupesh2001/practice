package com.practice.user_service.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.user_service.dto.UserInfoDto;
import com.practice.user_service.dto.UserResponseDto;
import com.practice.user_service.entity.UserProfile;
import com.practice.user_service.mappers.UserMapper;
import com.practice.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user profiles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handleCreate(UserInfoDto userInfoDto) {
        if (userProfileRepository.existsById(userInfoDto.getUserId())) {
            log.warn("User with ID={} already exists. Skipping create.", userInfoDto.getUserId());
            return;
        }

        UserProfile newProfile = objectMapper.convertValue(userInfoDto, UserProfile.class);
        newProfile.setUserId(userInfoDto.getUserId());
        newProfile.setCreatedAt(LocalDateTime.now());
        newProfile.setUpdatedAt(LocalDateTime.now());
        newProfile.setSyncedAt(LocalDateTime.now());

        UserProfile saved = userProfileRepository.save(newProfile);
        log.info("✅ Created new user profile: userId={}, username={}", saved.getUserId(), saved.getUsername());
    }

    @Transactional
    public void handleUpdate(UserInfoDto userInfoDto) {
        UserProfile existing = userProfileRepository.findById(userInfoDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("Cannot update: user not found for ID " + userInfoDto.getUserId()));

        userMapper.updateUserProfileFromDto(userInfoDto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setSyncedAt(LocalDateTime.now());

        UserProfile saved = userProfileRepository.save(existing);
        log.info("✅ Updated user profile: userId={}, username={}", saved.getUserId(), saved.getUsername());
    }

    /**
     * Delete user from Kafka event
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user profile: userName={}", userId);
        
        if (userProfileRepository.existsById(userId)) {
            userProfileRepository.deleteById(userId);
            log.info("Successfully deleted user profile: userName={}", userId);
        } else {
            log.warn("User profile not found for deletion: userName={}", userId);
        }
    }
    
    /**
     * Get user by ID
     */
    public UserResponseDto getUserById(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToDto(userProfile);
    }
    
    /**
     * Get user by username
     */
    public UserResponseDto getUserByUsername(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToDto(userProfile);
    }
    
    /**
     * Get all users
     */
    public List<UserResponseDto> getAllUsers() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Map entity to DTO
     */
    private UserResponseDto mapToDto(UserProfile userProfile) {
        return UserResponseDto.builder()
                .userId(userProfile.getUserId())
                .username(userProfile.getUsername())
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .address(userProfile.getAddress())
                .city(userProfile.getCity())
                .state(userProfile.getState())
                .country(userProfile.getCountry())
                .postalCode(userProfile.getPostalCode())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }
}
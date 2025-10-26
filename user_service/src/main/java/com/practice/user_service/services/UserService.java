package com.practice.user_service.services;


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

    /**
     * Create or update user from Kafka event
     */
    @Transactional
    public void createOrUpdateUser(UserInfoDto userInfoDto) {
        log.info("Processing user event: userName={}, eventType={}",
                userInfoDto.getUserId(), userInfoDto.getEventType());
        
        UserProfile userProfile = userProfileRepository.findById(userInfoDto.getUserId())
                .orElse(new UserProfile());
        
        // Map DTO to Entity
        userMapper.updateUserProfileFromDto(userInfoDto, userProfile);
        
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }
        
        UserProfile savedUser = userProfileRepository.save(userProfile);
        log.info("Successfully saved user profile: userName={}, username={}",
                savedUser.getUserId(), savedUser.getUsername());
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
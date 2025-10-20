package com.practice.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for receiving user information from Auth Service via Kafka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("dateOfBirth")
    private String dateOfBirth;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("postalCode")
    private String postalCode;
    
    @JsonProperty("eventType")
    private String eventType; // CREATED, UPDATED, DELETED
    
    @JsonProperty("timestamp")
    private Long timestamp;
}
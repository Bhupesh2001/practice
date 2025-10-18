package com.practice.auth_service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.auth_service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for producing user events to Kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${spring.kafka.topic-json.name}")
    private String topicName;
    
    /**
     * Send user event to Kafka topic
     */
    public void sendUserEvent(UserInfoDto userInfoDto) {
        try {
            String eventData = objectMapper.writeValueAsString(userInfoDto);
            
            log.info("Sending user event to Kafka topic {}: {}", topicName, eventData);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(topicName, userInfoDto.getUserId().toString(), eventData);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent user event to topic {} with offset {}",
                            topicName, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send user event to Kafka topic {}: {}", 
                            topicName, ex.getMessage());
                }
            });
            
        } catch (Exception ex) {
            log.error("Error serializing user event: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Send user created event
     */
    public void sendUserCreatedEvent(UserInfoDto userInfoDto) {
        userInfoDto.setEventType("CREATED");
        userInfoDto.setTimestamp(System.currentTimeMillis());
        sendUserEvent(userInfoDto);
    }
    
    /**
     * Send user updated event
     */
    public void sendUserUpdatedEvent(UserInfoDto userInfoDto) {
        userInfoDto.setEventType("UPDATED");
        userInfoDto.setTimestamp(System.currentTimeMillis());
        sendUserEvent(userInfoDto);
    }
    
    /**
     * Send user deleted event
     */
    public void sendUserDeletedEvent(Long userId) {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(userId)
                .eventType("DELETED")
                .timestamp(System.currentTimeMillis())
                .build();
        sendUserEvent(userInfoDto);
    }
}
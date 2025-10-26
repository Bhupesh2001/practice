package com.practice.user_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.user_service.dto.UserInfoDto;
import com.practice.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer for Auth Service events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceConsumer {
    
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    /**
     * Listen to user-events topic and process messages
     */
    @KafkaListener(
        topics = "${spring.kafka.topic-json.name}", 
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            @Payload String eventData,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received event from Kafka - Partition: {}, Offset: {}", partition, offset);
        log.debug("Event data: {}", eventData);
        
        try {
            // Deserialize JSON to UserInfoDto
            UserInfoDto userInfoDto = objectMapper.readValue(eventData, UserInfoDto.class);
            
            log.info("Processing {} event for user: {} (ID: {})", 
                    userInfoDto.getEventType(), 
                    userInfoDto.getUsername(), 
                    userInfoDto.getUserId());
            
            // Process based on event type
            switch (userInfoDto.getEventType()) {
                case "CREATED":
                    log.info("Creating new user profile");
                    userService.createOrUpdateUser(userInfoDto);
                    break;
                    
                case "UPDATED":
                    log.info("Updating existing user profile");
                    userService.createOrUpdateUser(userInfoDto);
                    break;
                    
                case "DELETED":
                    log.info("Deleting user profile");
                    userService.deleteUser(userInfoDto.getUserId());
                    break;
                    
                default:
                    log.warn("Unknown event type: {}", userInfoDto.getEventType());
            }
            
            log.info("Successfully processed {} event for userName: {}",
                    userInfoDto.getEventType(), userInfoDto.getUserId());
            
        } catch (Exception ex) {
            log.error("Error processing Kafka event - Partition: {}, Offset: {}, Error: {}", 
                    partition, offset, ex.getMessage(), ex);
            // In production, you might want to send to a dead letter queue
            // or implement retry logic
        }
    }
}
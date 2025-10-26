package com.practice.chatbot_service.eventProducer;

import com.practice.chatbot_service.dto.ExpenseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseProducer {
    private final KafkaTemplate<String, ExpenseDto> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    public void sendEventToKafka(ExpenseDto expenseDto) {
        Message<ExpenseDto> message = MessageBuilder
                .withPayload(expenseDto)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
        kafkaTemplate.send(message);
        log.info("ExpenseProducer: Sent event to Kafka topic {}: {}", TOPIC_NAME, expenseDto);
    }
}

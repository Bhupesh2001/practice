package com.practice.expense_service.consumer;

import com.practice.expense_service.dto.ExpenseDto;
import com.practice.expense_service.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExpenseConsumer
{

    private final ExpenseService expenseService;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String eventData) {
        try{
            // Todo: Make it transactional, and check if duplicate event (Handle idempotency)
            log.info("ExpenseConsumer: Consumed event data from topic: {}", eventData);
            ObjectMapper mapper = new ObjectMapper();
            ExpenseDto expenseDto = mapper.readValue(eventData, ExpenseDto.class);
            expenseService.createExpense(expenseDto);
        }catch(Exception ex){
            log.info("AuthServiceConsumer: Exception is thrown while consuming kafka event : {}", ex.getMessage());
        }
    }
}

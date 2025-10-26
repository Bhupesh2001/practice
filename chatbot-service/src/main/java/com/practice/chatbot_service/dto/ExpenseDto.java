package com.practice.chatbot_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ExpenseDto {

    @JsonProperty(value = "amount")
    @NonNull
    private BigDecimal amount;

    @JsonProperty(value = "user_name")
    private String userName;

    @JsonProperty(value = "merchant")
    private String merchant;

    @JsonProperty(value = "currency")
    private String currency;

    @JsonProperty(value = "created_at")
    private Timestamp createdAt;

    public void parseJson(String json) {
        try {
            // Pre-process the response to remove markdown backticks if any
            String cleanJson = json.replaceAll("^```json\\s*", "")
                    .replaceAll("\\s*```$", "")
                    .replaceAll("^`", "")
                    .replaceAll("`$", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();
            ExpenseDto expense = mapper.readValue(cleanJson, ExpenseDto.class);
            this.amount = expense.amount;
            this.userName = expense.userName;
            this.merchant = expense.merchant;
            this.currency = expense.currency;
            this.createdAt = expense.createdAt;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ExpenseDto from JSON: " + json, e);
        }
    }
}
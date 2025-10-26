package com.practice.chatbot_service.dto;

import com.google.api.client.util.Value;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Prompt must not be empty")
    private String prompt;

    @Value("${gemini.system-instruction:}")
    private String systemInstruction;

    @Min(value = 0, message = "Temperature must be >= 0.0")
    @Max(value = 1, message = "Temperature must be <= 1.0")
    private Float temperature;

    @Min(value = 1, message = "Max output tokens must be at least 1")
    @Max(value = 2048, message = "Max output tokens must not exceed 2048")
    private Integer maxOutputTokens;

}


package com.practice.chatbot_service.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.practice.chatbot_service.config.GeminiProperties;
import com.practice.chatbot_service.dto.ChatRequest;
import com.practice.chatbot_service.dto.ExpenseDto;
import com.practice.chatbot_service.eventProducer.ExpenseProducer;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    private final Client client;
    private final GeminiProperties properties;
    private final ExpenseProducer expenseProducer;

    public ExpenseDto getResponseFromGemini(ChatRequest req, String userName) {
        log.info("GeminiService: Received request: {}", req);
        Float temp = (req.getTemperature() != null) ? req.getTemperature() : 0.2f;
        Integer tokens = (req.getMaxOutputTokens() != null) ? req.getMaxOutputTokens() : 512;

        // pick system instruction (from req or default from config)
        String effectiveSystemInstruction =
                (req.getSystemInstruction() != null && !req.getSystemInstruction().isBlank())
                        ? req.getSystemInstruction()
                        : properties.getSystemInstruction();

        // build config, call Gemini...
        GenerateContentConfig.Builder cfg = GenerateContentConfig.builder()
                .temperature(temp)
                .maxOutputTokens(tokens);

        if (effectiveSystemInstruction != null && !effectiveSystemInstruction.isBlank()) {
            Content sys = Content.fromParts(Part.fromText(effectiveSystemInstruction));
            cfg.systemInstruction(sys);
        }

        GenerateContentResponse resp =
                client.models.generateContent(properties.getModel(), req.getPrompt(), cfg.build());
        String response = resp.text();


//        String response = "{\"amount\": 450.00, \"merchant\": \"AMAZON.IN\", \"currency\": \"INR\", \"created_at\": 1719325822000}";
        log.info("GeminiService: Response from Gemini: {}", response);
        ExpenseDto expenseDto = new ExpenseDto();
        if (nonNull(response)) {
            expenseDto.parseJson(response);
            expenseDto.setUserName(userName);
            log.info("GeminiService: Detected expense: {}", expenseDto);
            expenseProducer.sendEventToKafka(expenseDto);
        }
        return expenseDto;
    }

}

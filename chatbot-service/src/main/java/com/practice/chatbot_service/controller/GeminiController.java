package com.practice.chatbot_service.controller;

import com.practice.chatbot_service.dto.ChatRequest;
import com.practice.chatbot_service.dto.ExpenseDto;
import com.practice.chatbot_service.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;


    @PostMapping(path = "/generate")
    public ExpenseDto generate(@Valid @RequestBody ChatRequest req,
                               @RequestHeader(value = "X-User-Name") String userName) {
        return geminiService.getResponseFromGemini(req, userName);
    }
}

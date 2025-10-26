package com.practice.chatbot_service.config;

import com.google.genai.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GeminiClientConfig {

    @Bean
    Client geminiClient(@Value("${gemini.apiKey:}") String key) {
        // If you pass the key here, it uses Gemini Dev API; otherwise it will read GOOGLE_API_KEY.
        log.info("Creating Gemini Client, apiKey provided: {}", key);
        return (key != null && !key.isBlank())
                ? Client.builder().apiKey(key).build()
                : new Client(); // reads GOOGLE_API_KEY automatically
    }
}

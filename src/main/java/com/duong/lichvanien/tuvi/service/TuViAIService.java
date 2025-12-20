package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.config.TuViAIConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with AI providers (OpenAI/Anthropic) to generate Tu Vi interpretations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViAIService {

    private final TuViAIConfig config;
    private final ObjectMapper objectMapper;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";

    /**
     * Generate content using the configured AI provider.
     *
     * @param systemPrompt System prompt to set context
     * @param userPrompt   User prompt with specific request
     * @return Generated content or null if error
     */
    public String generateContent(String systemPrompt, String userPrompt) {
        if (config.isOpenAI()) {
            return generateWithOpenAI(systemPrompt, userPrompt);
        } else if (config.isAnthropic()) {
            return generateWithAnthropic(systemPrompt, userPrompt);
        } else {
            log.error("Unknown AI provider: {}", config.getProvider());
            return null;
        }
    }

    /**
     * Generate content using OpenAI GPT API.
     */
    private String generateWithOpenAI(String systemPrompt, String userPrompt) {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenAI API key not configured, returning null");
            return null;
        }

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(OPENAI_API_URL)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "model", config.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "max_tokens", config.getMaxTokens(),
                    "temperature", config.getTemperature()
            );

            String response = client.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    return choices.get(0).path("message").path("content").asText();
                }
            }

            log.error("No content in OpenAI response");
            return null;

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate content using Anthropic Claude API.
     */
    private String generateWithAnthropic(String systemPrompt, String userPrompt) {
        String apiKey = config.getAnthropicApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Anthropic API key not configured, returning null");
            return null;
        }

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(ANTHROPIC_API_URL)
                    .defaultHeader("x-api-key", apiKey)
                    .defaultHeader("anthropic-version", "2023-06-01")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "model", config.getModel(),
                    "system", systemPrompt,
                    "messages", List.of(
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "max_tokens", config.getMaxTokens(),
                    "temperature", config.getTemperature()
            );

            String response = client.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode content = root.path("content");
                if (content.isArray() && !content.isEmpty()) {
                    return content.get(0).path("text").asText();
                }
            }

            log.error("No content in Anthropic response");
            return null;

        } catch (Exception e) {
            log.error("Error calling Anthropic API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Check if AI service is configured and available.
     */
    public boolean isAvailable() {
        String key = config.getEffectiveApiKey();
        return key != null && !key.isBlank();
    }

    /**
     * Get the current AI provider name.
     */
    public String getProviderName() {
        return config.getProvider() + "/" + config.getModel();
    }
}

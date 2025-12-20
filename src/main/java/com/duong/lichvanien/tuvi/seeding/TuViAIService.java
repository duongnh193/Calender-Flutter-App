package com.duong.lichvanien.tuvi.seeding;

import com.duong.lichvanien.tuvi.seeding.TuViAIConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with AI providers (OpenAI/Anthropic/Gemini) to generate Tu Vi interpretations.
 * 
 * NOTE: This service is moved to seeding package and should only be used for batch data generation,
 * not in production flow. The production flow uses database-only mode.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViAIService {

    private final TuViAIConfig config;
    private final ObjectMapper objectMapper;

    private static final String OPENAI_API_BASE = "https://api.openai.com/v1";
    private static final String OPENAI_CHAT_COMPLETIONS_PATH = "/chat/completions";
    private static final String ANTHROPIC_API_BASE = "https://api.anthropic.com/v1";
    private static final String ANTHROPIC_MESSAGES_PATH = "/messages";
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta";
    private static final String GEMINI_CHAT_COMPLETIONS_PATH = "/openai/chat/completions";

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
        } else if (config.isGemini()) {
            return generateWithGemini(systemPrompt, userPrompt);
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
                    .baseUrl(OPENAI_API_BASE)
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

            log.debug("Calling OpenAI API: model={}, max_tokens={}", config.getModel(), config.getMaxTokens());

            String response = client.post()
                    .uri(OPENAI_CHAT_COMPLETIONS_PATH)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            if (response != null && !response.isBlank()) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.debug("OpenAI API response received, content length: {}", content.length());
                    return content;
                }
            }

            log.error("No content in OpenAI response");
            return null;

        } catch (WebClientResponseException e) {
            log.error("OpenAI API error: {} {} - Response body: {}", 
                    e.getStatusCode(), 
                    e.getStatusText(),
                    e.getResponseBodyAsString());
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
                    .baseUrl(ANTHROPIC_API_BASE)
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

            log.debug("Calling Anthropic API: model={}, max_tokens={}", config.getModel(), config.getMaxTokens());

            String response = client.post()
                    .uri(ANTHROPIC_MESSAGES_PATH)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            if (response != null && !response.isBlank()) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode content = root.path("content");
                if (content.isArray() && !content.isEmpty()) {
                    String text = content.get(0).path("text").asText();
                    log.debug("Anthropic API response received, content length: {}", text.length());
                    return text;
                }
            }

            log.error("No content in Anthropic response");
            return null;

        } catch (WebClientResponseException e) {
            log.error("Anthropic API error: {} {} - Response body: {}", 
                    e.getStatusCode(), 
                    e.getStatusText(),
                    e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error calling Anthropic API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate content using Google Gemini API.
     * Gemini uses OpenAI-compatible endpoint format.
     */
    private String generateWithGemini(String systemPrompt, String userPrompt) {
        String apiKey = config.getGeminiApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key not configured, returning null");
            return null;
        }

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(GEMINI_API_BASE)
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

            log.debug("Calling Gemini API: model={}, max_tokens={}", config.getModel(), config.getMaxTokens());

            String response = client.post()
                    .uri(GEMINI_CHAT_COMPLETIONS_PATH)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            if (response != null && !response.isBlank()) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.debug("Gemini API response received, content length: {}", content.length());
                    return content;
                }
            }

            log.error("No content in Gemini response");
            return null;

        } catch (WebClientResponseException e) {
            log.error("Gemini API error: {} {} - Response body: {}", 
                    e.getStatusCode(), 
                    e.getStatusText(),
                    e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
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

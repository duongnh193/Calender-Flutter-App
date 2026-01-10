package com.duong.lichvanien.tuvi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Grok AI service.
 * Used for generating Tu Vi interpretations via xAI's Grok API.
 */
@Component
@ConfigurationProperties(prefix = "grok.api")
@Getter
@Setter
public class GrokProperties {

    /**
     * Grok API key from xAI
     */
    private String key;

    /**
     * Base URL for Grok API (default: https://api.x.ai/v1)
     */
    private String baseUrl = "https://api.x.ai/v1";

    /**
     * Model name to use (default: grok-beta)
     */
    private String model = "grok-beta";

    /**
     * Request timeout in seconds (default: 120)
     */
    private int timeoutSeconds = 120;

    /**
     * Maximum tokens in response (default: 4000)
     */
    private int maxTokens = 4000;

    /**
     * Temperature for response generation (default: 0.7)
     * Higher = more creative, Lower = more deterministic
     */
    private double temperature = 0.7;

    /**
     * Check if Grok API is configured and available.
     */
    public boolean isAvailable() {
        return key != null && !key.isBlank();
    }

    /**
     * Get the chat completions endpoint URL.
     */
    public String getChatCompletionsUrl() {
        return baseUrl + "/chat/completions";
    }
}


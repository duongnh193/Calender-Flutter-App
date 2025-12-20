package com.duong.lichvanien.tuvi.seeding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Tu Vi AI interpretation service.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tuvi.ai")
public class TuViAIConfig {

    /**
     * AI provider: "openai", "anthropic", or "gemini"
     */
    private String provider = "openai";

    /**
     * Model name (e.g., "gpt-4", "gpt-4-turbo", "claude-3-opus-20240229", "gemini-2.0-flash-exp")
     */
    private String model = "gpt-4";

    /**
     * OpenAI API key
     */
    private String apiKey;

    /**
     * Anthropic API key (if using Claude)
     */
    private String anthropicApiKey;

    /**
     * Google Gemini API key (if using Gemini)
     */
    private String geminiApiKey;

    /**
     * Maximum tokens for response
     */
    private int maxTokens = 4000;

    /**
     * Temperature for creativity (0.0 - 1.0)
     */
    private double temperature = 0.7;

    /**
     * Request timeout in seconds
     */
    private int timeoutSeconds = 120;

    public boolean isOpenAI() {
        return "openai".equalsIgnoreCase(provider);
    }

    public boolean isAnthropic() {
        return "anthropic".equalsIgnoreCase(provider);
    }

    public boolean isGemini() {
        return "gemini".equalsIgnoreCase(provider);
    }

    public String getEffectiveApiKey() {
        if (isOpenAI()) {
            return apiKey;
        } else if (isAnthropic()) {
            return anthropicApiKey;
        } else if (isGemini()) {
            return geminiApiKey;
        }
        return null;
    }
}

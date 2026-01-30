package com.duong.lichvanien.tuvi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for OpenAI Text-to-Speech service.
 * Used for generating audio from Tu Vi interpretation text.
 */
@Component
@ConfigurationProperties(prefix = "openai.tts")
@Getter
@Setter
public class OpenAITTSServiceProperties {

    /**
     * OpenAI API key for TTS
     */
    private String apiKey;

    /**
     * Base URL for OpenAI API (default: https://api.openai.com/v1)
     */
    private String baseUrl = "https://api.openai.com/v1";

    /**
     * TTS model to use (default: tts-1)
     * Options: tts-1 (fast), tts-1-hd (high quality)
     */
    private String model = "tts-1";

    /**
     * Voice to use (default: alloy)
     * Options: alloy, echo, fable, onyx, nova, shimmer
     */
    private String voice = "alloy";

    /**
     * Speed of speech (default: 1.0)
     * Range: 0.25 - 4.0
     */
    private Double speed = 1.0;

    /**
     * Check if OpenAI TTS is configured and available.
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Get the TTS speech endpoint URL.
     */
    public String getSpeechEndpoint() {
        return baseUrl + "/audio/speech";
    }
}


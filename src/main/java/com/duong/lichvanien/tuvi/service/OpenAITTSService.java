package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.config.OpenAITTSServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

/**
 * Service for interacting with OpenAI Text-to-Speech API.
 * Converts text to MP3 audio format.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAITTSService {

    private final OpenAITTSServiceProperties ttsProperties;

    /**
     * Generate audio from text using OpenAI TTS API.
     *
     * @param text  Text to convert to speech
     * @param voice Voice to use (optional, uses default from config if null)
     * @param speed Speed of speech (optional, uses default from config if null)
     * @return InputStream containing MP3 audio bytes
     * @throws RuntimeException if API call fails or service is unavailable
     */
    public InputStream generateAudio(String text, String voice, Double speed) {
        if (!ttsProperties.isAvailable()) {
            log.warn("OpenAI TTS API key not configured, cannot generate audio");
            throw new RuntimeException("Dịch vụ TTS không khả dụng. Vui lòng cấu hình API key.");
        }

        if (text == null || text.isBlank()) {
            log.warn("Empty text provided for TTS generation");
            throw new IllegalArgumentException("Văn bản không được để trống.");
        }

        // Use provided voice/speed or fallback to config defaults
        String selectedVoice = voice != null && !voice.isBlank() ? voice : ttsProperties.getVoice();
        Double selectedSpeed = speed != null ? speed : ttsProperties.getSpeed();

        // OpenAI TTS has a limit of 4096 characters
        if (text.length() > 4096) {
            log.warn("Text length {} exceeds OpenAI TTS limit (4096), truncating", text.length());
            text = text.substring(0, 4090) + "...";
        }

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(ttsProperties.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + ttsProperties.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "model", ttsProperties.getModel(),
                    "input", text,
                    "voice", selectedVoice,
                    "response_format", "mp3",
                    "speed", selectedSpeed
            );

            log.debug("Calling OpenAI TTS API: model={}, voice={}, speed={}, text_length={}",
                    ttsProperties.getModel(), selectedVoice, selectedSpeed, text.length());

            byte[] audioBytes = client.post()
                    .uri("/audio/speech")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            if (audioBytes == null || audioBytes.length == 0) {
                log.error("Empty audio response from OpenAI TTS API");
                throw new RuntimeException("Không thể tạo audio. Phản hồi từ API trống.");
            }

            log.debug("OpenAI TTS API response received, audio size: {} bytes", audioBytes.length);
            return new ByteArrayInputStream(audioBytes);

        } catch (WebClientResponseException e) {
            log.error("OpenAI TTS API error: {} {} - Response body: {}",
                    e.getStatusCode(),
                    e.getStatusText(),
                    e.getResponseBodyAsString());
            throw new RuntimeException(
                    String.format("Lỗi khi gọi OpenAI TTS API: %s %s", e.getStatusCode(), e.getStatusText()),
                    e
            );
        } catch (Exception e) {
            log.error("Error calling OpenAI TTS API: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo audio: " + e.getMessage(), e);
        }
    }

    /**
     * Generate audio using default voice and speed from config.
     *
     * @param text Text to convert to speech
     * @return InputStream containing MP3 audio bytes
     */
    public InputStream generateAudio(String text) {
        return generateAudio(text, null, null);
    }

    /**
     * Check if OpenAI TTS service is available.
     */
    public boolean isAvailable() {
        return ttsProperties.isAvailable();
    }
}


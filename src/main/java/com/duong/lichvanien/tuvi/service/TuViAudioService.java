package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Service for generating audio from Tu Vi interpretations.
 * Orchestrates interpretation retrieval, text extraction, and TTS conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViAudioService {

    private final TuViGrokInterpretationService interpretationService;
    private final TuViAudioTextExtractor textExtractor;
    private final OpenAITTSService ttsService;

    /**
     * Generate audio for a specific palace from Tu Vi interpretation.
     *
     * @param request    Chart request (birth date, hour, gender, etc.)
     * @param palaceCode Palace code (OVERVIEW, MENH, QUAN_LOC, etc.)
     * @return InputStream containing MP3 audio bytes
     * @throws RuntimeException if interpretation not found, TTS unavailable, or generation fails
     */
    public InputStream generateAudioForPalace(TuViChartRequest request, String palaceCode) {
        log.info("Generating audio for palace: {} for chart: {}", palaceCode, request.getName());

        // Check if TTS service is available
        if (!ttsService.isAvailable()) {
            log.error("OpenAI TTS service is not available");
            throw new RuntimeException("Dịch vụ TTS không khả dụng. Vui lòng thử lại sau.");
        }

        // Get interpretation (this will check cache first, then call Grok if needed)
        TuViInterpretationResponse interpretation = interpretationService.generateFullInterpretation(request);
        if (interpretation == null) {
            log.error("Failed to get interpretation for audio generation");
            throw new RuntimeException("Không thể lấy giải luận. Vui lòng thử lại sau.");
        }

        // Extract text for the requested palace
        String text;
        try {
            text = textExtractor.extractTextForPalace(interpretation, palaceCode);
        } catch (IllegalArgumentException e) {
            log.error("Invalid palace code or palace not found: {}", palaceCode);
            throw new IllegalArgumentException("Không tìm thấy cung: " + palaceCode, e);
        }

        if (text == null || text.isBlank()) {
            log.warn("Extracted text is empty for palace: {}", palaceCode);
            throw new RuntimeException("Nội dung giải luận cho cung này trống.");
        }

        log.debug("Extracted text length: {} for palace: {}", text.length(), palaceCode);

        // Generate audio from text
        try {
            InputStream audioStream = ttsService.generateAudio(text);
            log.info("Successfully generated audio for palace: {}", palaceCode);
            return audioStream;
        } catch (Exception e) {
            log.error("Error generating audio for palace {}: {}", palaceCode, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo audio: " + e.getMessage(), e);
        }
    }

    /**
     * Check if audio generation is available (TTS service configured).
     */
    public boolean isAvailable() {
        return ttsService.isAvailable();
    }
}


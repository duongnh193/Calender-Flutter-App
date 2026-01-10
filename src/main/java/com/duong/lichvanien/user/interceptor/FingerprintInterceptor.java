package com.duong.lichvanien.user.interceptor;

import com.duong.lichvanien.user.dto.FingerprintRequest;
import com.duong.lichvanien.user.dto.FingerprintResponse;
import com.duong.lichvanien.user.service.FingerprintService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that extracts and processes fingerprint data from requests.
 * Sets fingerprint ID as request attribute for use in controllers/services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FingerprintInterceptor implements HandlerInterceptor {

    public static final String FINGERPRINT_ID_ATTRIBUTE = "fingerprintId";
    public static final String FINGERPRINT_HEADER = "X-Fingerprint-Data";

    private final FingerprintService fingerprintService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // Try to get fingerprint data from header
            String fingerprintJson = request.getHeader(FINGERPRINT_HEADER);
            FingerprintRequest fingerprintRequest = null;
            
            if (fingerprintJson != null && !fingerprintJson.isBlank()) {
                try {
                    fingerprintRequest = objectMapper.readValue(fingerprintJson, FingerprintRequest.class);
                } catch (Exception e) {
                    log.debug("Failed to parse fingerprint header: {}", e.getMessage());
                }
            }
            
            // Generate fingerprint ID
            String fingerprintId = fingerprintService.generateFingerprintId(fingerprintRequest, request);
            
            // Store in request attribute
            request.setAttribute(FINGERPRINT_ID_ATTRIBUTE, fingerprintId);
            
            // Track usage if fingerprint exists
            if (fingerprintService.getFingerprintById(fingerprintId).isPresent()) {
                fingerprintService.trackFingerprintUsage(fingerprintId);
            } else {
                // Create new fingerprint record
                FingerprintResponse fingerprintResponse = fingerprintService.generateFingerprint(fingerprintRequest, request);
                log.debug("Created fingerprint: {}, isNew: {}", fingerprintResponse.getFingerprintId(), fingerprintResponse.isNew());
            }
            
        } catch (Exception e) {
            log.error("Error processing fingerprint: {}", e.getMessage());
            // Don't block the request, just use a fallback fingerprint
            String fallbackId = fingerprintService.generateFingerprintId(null, request);
            request.setAttribute(FINGERPRINT_ID_ATTRIBUTE, fallbackId);
        }
        
        return true;
    }

    /**
     * Get fingerprint ID from request attribute.
     */
    public static String getFingerprintId(HttpServletRequest request) {
        Object fingerprintId = request.getAttribute(FINGERPRINT_ID_ATTRIBUTE);
        return fingerprintId != null ? fingerprintId.toString() : null;
    }
}


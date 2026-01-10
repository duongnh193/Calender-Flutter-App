package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.dto.FingerprintRequest;
import com.duong.lichvanien.user.dto.FingerprintResponse;
import com.duong.lichvanien.user.entity.FingerprintEntity;
import com.duong.lichvanien.user.exception.FingerprintException;
import com.duong.lichvanien.user.repository.FingerprintRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for fingerprint generation and management.
 * Implements hybrid fingerprinting: client-side data + server-side data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FingerprintService {

    private final FingerprintRepository fingerprintRepository;

    /**
     * Generate or retrieve fingerprint for a request.
     */
    @Transactional
    public FingerprintResponse generateFingerprint(FingerprintRequest clientData, HttpServletRequest request) {
        // Normalize fingerprint data
        String normalizedData = normalizeFingerprint(clientData, request);
        
        // Hash the normalized data
        String fingerprintId = hashFingerprint(normalizedData);
        
        // Check if fingerprint exists
        Optional<FingerprintEntity> existingFingerprint = fingerprintRepository.findByFingerprintId(fingerprintId);
        
        if (existingFingerprint.isPresent()) {
            // Update existing fingerprint
            FingerprintEntity fingerprint = existingFingerprint.get();
            fingerprint.incrementUsage();
            fingerprintRepository.save(fingerprint);
            
            log.debug("Existing fingerprint found: {}, usage count: {}", fingerprintId, fingerprint.getUsageCount());
            
            return FingerprintResponse.builder()
                    .fingerprintId(fingerprintId)
                    .isNew(false)
                    .firstSeenAt(fingerprint.getFirstSeenAt())
                    .usageCount(fingerprint.getUsageCount())
                    .build();
        }
        
        // Create new fingerprint
        FingerprintEntity fingerprint = createFingerprint(clientData, request, fingerprintId, normalizedData);
        
        log.info("New fingerprint created: {}", fingerprintId);
        
        return FingerprintResponse.builder()
                .fingerprintId(fingerprintId)
                .isNew(true)
                .firstSeenAt(fingerprint.getFirstSeenAt())
                .usageCount(1)
                .build();
    }

    /**
     * Get fingerprint by ID.
     */
    public Optional<FingerprintEntity> getFingerprintById(String fingerprintId) {
        return fingerprintRepository.findByFingerprintId(fingerprintId);
    }

    /**
     * Track fingerprint usage.
     */
    @Transactional
    public void trackFingerprintUsage(String fingerprintId) {
        fingerprintRepository.incrementUsageCount(fingerprintId, LocalDateTime.now());
    }

    /**
     * Generate fingerprint ID from request without saving.
     * Useful for checking fingerprint without creating a record.
     */
    public String generateFingerprintId(FingerprintRequest clientData, HttpServletRequest request) {
        String normalizedData = normalizeFingerprint(clientData, request);
        return hashFingerprint(normalizedData);
    }

    /**
     * Normalize fingerprint data by combining client and server data.
     * This creates a consistent string representation for hashing.
     */
    public String normalizeFingerprint(FingerprintRequest clientData, HttpServletRequest request) {
        StringBuilder normalized = new StringBuilder();
        
        // Client-side data (if available)
        if (clientData != null) {
            normalized.append("canvas:").append(nullSafe(clientData.getCanvasHash())).append("|");
            normalized.append("webgl:").append(nullSafe(clientData.getWebglHash())).append("|");
            normalized.append("screen:").append(nullSafe(clientData.getScreenSize())).append("|");
            normalized.append("tz:").append(nullSafe(clientData.getTimezone())).append("|");
            normalized.append("lang:").append(nullSafe(clientData.getLanguage())).append("|");
        }
        
        // Server-side data
        String ipAddress = getClientIpAddress(request);
        String userAgent = nullSafe(request.getHeader("User-Agent"));
        String acceptLanguage = nullSafe(request.getHeader("Accept-Language"));
        String acceptEncoding = nullSafe(request.getHeader("Accept-Encoding"));
        
        // Normalize User-Agent (remove version numbers for more stable fingerprint)
        String normalizedUserAgent = normalizeUserAgent(userAgent);
        
        normalized.append("ip:").append(ipAddress).append("|");
        normalized.append("ua:").append(normalizedUserAgent).append("|");
        normalized.append("al:").append(acceptLanguage).append("|");
        normalized.append("ae:").append(acceptEncoding);
        
        return normalized.toString();
    }

    /**
     * Hash fingerprint data using SHA-256.
     */
    public String hashFingerprint(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new FingerprintException("Failed to hash fingerprint data", e);
        }
    }

    /**
     * Create and save a new fingerprint entity.
     */
    private FingerprintEntity createFingerprint(FingerprintRequest clientData, 
                                                 HttpServletRequest request,
                                                 String fingerprintId,
                                                 String normalizedData) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String acceptHeaders = buildAcceptHeaders(request);
        
        // Store client data as JSON
        String fingerprintDataJson = clientData != null ? clientData.toJsonString() : "{}";
        
        FingerprintEntity fingerprint = FingerprintEntity.builder()
                .fingerprintId(fingerprintId)
                .fingerprintData(fingerprintDataJson)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .acceptHeaders(acceptHeaders)
                .normalizedHash(hashFingerprint(normalizedData))
                .usageCount(1)
                .build();
        
        return fingerprintRepository.save(fingerprint);
    }

    /**
     * Get client IP address, handling proxies.
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_CLIENT_IP"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Normalize User-Agent by removing version numbers.
     * This creates a more stable fingerprint across browser updates.
     */
    private String normalizeUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "";
        }
        
        // Remove version numbers (sequences of digits and dots)
        String normalized = userAgent.replaceAll("\\d+\\.\\d+(\\.\\d+)*", "X");
        // Remove build numbers
        normalized = normalized.replaceAll("\\d{4,}", "X");
        // Normalize whitespace
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }

    /**
     * Build Accept headers string for fingerprinting.
     */
    private String buildAcceptHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        headers.append("Accept:").append(nullSafe(request.getHeader("Accept"))).append(";");
        headers.append("Accept-Language:").append(nullSafe(request.getHeader("Accept-Language"))).append(";");
        headers.append("Accept-Encoding:").append(nullSafe(request.getHeader("Accept-Encoding")));
        return headers.toString();
    }

    /**
     * Convert bytes to hex string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Return empty string for null values.
     */
    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}


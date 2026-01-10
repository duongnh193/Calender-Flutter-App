package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.entity.AccessLogEntity;
import com.duong.lichvanien.user.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for logging API access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private final FingerprintService fingerprintService;

    /**
     * Log an API access asynchronously to not block the request.
     */
    @Async
    @Transactional
    public void logAccessAsync(HttpServletRequest request,
                                String endpoint,
                                String method,
                                String requestBody,
                                Long userId,
                                Long sessionId,
                                String fingerprintId,
                                Integer responseStatus,
                                Integer responseTimeMs) {
        try {
            logAccess(request, endpoint, method, requestBody, userId, sessionId, 
                     fingerprintId, responseStatus, responseTimeMs);
        } catch (Exception e) {
            log.error("Failed to log access: {}", e.getMessage());
        }
    }

    /**
     * Log an API access synchronously.
     */
    @Transactional
    public AccessLogEntity logAccess(HttpServletRequest request,
                                      String endpoint,
                                      String method,
                                      String requestBody,
                                      Long userId,
                                      Long sessionId,
                                      String fingerprintId,
                                      Integer responseStatus,
                                      Integer responseTimeMs) {
        String ipAddress = fingerprintService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String queryParams = request.getQueryString();
        
        // Hash request body for duplicate detection
        String requestBodyHash = null;
        if (requestBody != null && !requestBody.isEmpty()) {
            requestBodyHash = hashString(requestBody);
        }
        
        AccessLogEntity logEntry = AccessLogEntity.builder()
                .userId(userId)
                .sessionId(sessionId)
                .fingerprintId(fingerprintId != null ? fingerprintId : "unknown")
                .ipAddress(ipAddress)
                .endpoint(endpoint)
                .method(method)
                .requestBodyHash(requestBodyHash)
                .queryParams(queryParams)
                .responseStatus(responseStatus)
                .responseTimeMs(responseTimeMs)
                .userAgent(userAgent)
                .referer(referer)
                .build();
        
        return accessLogRepository.save(logEntry);
    }

    /**
     * Get access history for a user.
     */
    public Page<AccessLogEntity> getAccessHistory(Long userId, int page, int size) {
        return accessLogRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    /**
     * Get access history for a fingerprint.
     */
    public Page<AccessLogEntity> getAccessHistoryByFingerprint(String fingerprintId, int page, int size) {
        return accessLogRepository.findByFingerprintIdOrderByCreatedAtDesc(fingerprintId, PageRequest.of(page, size));
    }

    /**
     * Count requests by fingerprint and endpoint in time range.
     * Useful for rate limiting.
     */
    public long countRequestsInTimeRange(String fingerprintId, String endpoint, LocalDateTime since) {
        return accessLogRepository.countByFingerprintAndEndpointSince(fingerprintId, endpoint, since);
    }

    /**
     * Check for duplicate requests.
     */
    public boolean isDuplicateRequest(String fingerprintId, String endpoint, String requestBodyHash, LocalDateTime since) {
        List<AccessLogEntity> duplicates = accessLogRepository.findDuplicateRequests(
                fingerprintId, endpoint, requestBodyHash, since);
        return !duplicates.isEmpty();
    }

    /**
     * Delete old logs.
     */
    @Transactional
    public int deleteOldLogs(LocalDateTime threshold) {
        int deleted = accessLogRepository.deleteOldLogs(threshold);
        log.info("Deleted {} old access logs before {}", deleted, threshold);
        return deleted;
    }

    /**
     * Get request count for an endpoint in date range.
     */
    public long getRequestCount(String endpoint, LocalDateTime start, LocalDateTime end) {
        return accessLogRepository.countByEndpointAndCreatedAtBetween(endpoint, start, end);
    }

    /**
     * Hash a string using SHA-256.
     */
    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash string: {}", e.getMessage());
            return null;
        }
    }
}


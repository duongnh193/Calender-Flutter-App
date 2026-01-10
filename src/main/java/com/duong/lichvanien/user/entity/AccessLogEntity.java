package com.duong.lichvanien.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Access log entity for API request auditing.
 */
@Entity
@Table(name = "access_log",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_session_id", columnList = "session_id"),
           @Index(name = "idx_fingerprint_id", columnList = "fingerprint_id"),
           @Index(name = "idx_ip_address", columnList = "ip_address"),
           @Index(name = "idx_endpoint", columnList = "endpoint"),
           @Index(name = "idx_method", columnList = "method"),
           @Index(name = "idx_response_status", columnList = "response_status"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID (not FK to avoid blocking on log inserts).
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Session ID (not FK to avoid blocking on log inserts).
     */
    @Column(name = "session_id")
    private Long sessionId;

    /**
     * Fingerprint ID for tracking.
     */
    @Column(name = "fingerprint_id", nullable = false, length = 64)
    private String fingerprintId;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * API endpoint path.
     */
    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    /**
     * HTTP method (GET, POST, etc.).
     */
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    /**
     * SHA-256 hash of request body for duplicate detection.
     */
    @Column(name = "request_body_hash", length = 64)
    private String requestBodyHash;

    /**
     * Query parameters.
     */
    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    /**
     * HTTP response status code.
     */
    @Column(name = "response_status")
    private Integer responseStatus;

    /**
     * Response time in milliseconds.
     */
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "referer", length = 500)
    private String referer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}


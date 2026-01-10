package com.duong.lichvanien.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Fingerprint entity for device/browser fingerprinting.
 * Used for tracking and anti-cheating purposes.
 */
@Entity
@Table(name = "fingerprint",
       indexes = {
           @Index(name = "idx_fingerprint_id", columnList = "fingerprint_id"),
           @Index(name = "idx_normalized_hash", columnList = "normalized_hash"),
           @Index(name = "idx_ip_address", columnList = "ip_address"),
           @Index(name = "idx_first_seen_at", columnList = "first_seen_at"),
           @Index(name = "idx_last_seen_at", columnList = "last_seen_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * SHA-256 hash of normalized fingerprint data.
     * This is the primary identifier for fingerprint lookups.
     */
    @Column(name = "fingerprint_id", nullable = false, unique = true, length = 64)
    private String fingerprintId;

    /**
     * Raw fingerprint data from client as JSON.
     * Contains: canvas_hash, webgl_hash, screen_size, timezone, language
     */
    @Column(name = "fingerprint_data", nullable = false, columnDefinition = "JSON")
    private String fingerprintData;

    /**
     * IP address from server-side.
     */
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * User-Agent header from request.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Accept-* headers for additional fingerprinting.
     */
    @Column(name = "accept_headers", columnDefinition = "TEXT")
    private String acceptHeaders;

    /**
     * SHA-256 hash after normalization (combination of client + server data).
     */
    @Column(name = "normalized_hash", nullable = false, length = 64)
    private String normalizedHash;

    @Column(name = "first_seen_at", nullable = false, updatable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    /**
     * Number of times this fingerprint was used.
     */
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 1;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        firstSeenAt = now;
        lastSeenAt = now;
        if (usageCount == null) {
            usageCount = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastSeenAt = LocalDateTime.now();
    }

    /**
     * Increment usage count and update last seen timestamp.
     */
    public void incrementUsage() {
        this.usageCount++;
        this.lastSeenAt = LocalDateTime.now();
    }
}


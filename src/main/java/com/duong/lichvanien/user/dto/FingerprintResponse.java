package com.duong.lichvanien.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for fingerprint information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerprintResponse {

    /**
     * Unique fingerprint ID (SHA-256 hash).
     */
    private String fingerprintId;

    /**
     * Whether this is a new fingerprint or existing one.
     */
    private boolean isNew;

    /**
     * First time this fingerprint was seen.
     */
    private LocalDateTime firstSeenAt;

    /**
     * Usage count.
     */
    private Integer usageCount;
}


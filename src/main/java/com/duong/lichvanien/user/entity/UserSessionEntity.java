package com.duong.lichvanien.user.entity;

import com.duong.lichvanien.user.enums.DeviceType;
import com.duong.lichvanien.user.enums.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User session entity for JWT session management.
 * Supports both authenticated and anonymous sessions.
 */
@Entity
@Table(name = "user_session",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_fingerprint_id", columnList = "fingerprint_id"),
           @Index(name = "idx_session_token", columnList = "session_token"),
           @Index(name = "idx_refresh_token", columnList = "refresh_token"),
           @Index(name = "idx_ip_address", columnList = "ip_address"),
           @Index(name = "idx_expires_at", columnList = "expires_at"),
           @Index(name = "idx_is_active", columnList = "is_active"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User associated with this session. NULL for anonymous sessions.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * JWT token ID (jti claim) for token identification.
     */
    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    /**
     * Refresh token for session renewal.
     */
    @Column(name = "refresh_token", unique = true)
    private String refreshToken;

    /**
     * SHA-256 hash linking to fingerprint table.
     */
    @Column(name = "fingerprint_id", nullable = false, length = 64)
    private String fingerprintId;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 50)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 50)
    private Platform platform;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        lastActivityAt = now;
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Update last activity timestamp.
     */
    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Check if session is expired.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if session is valid (active and not expired).
     */
    public boolean isValid() {
        return isActive && !isExpired();
    }

    /**
     * Revoke this session.
     */
    public void revoke() {
        this.isActive = false;
    }
}


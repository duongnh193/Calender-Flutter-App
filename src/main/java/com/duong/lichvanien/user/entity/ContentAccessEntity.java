package com.duong.lichvanien.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Content access entity for tracking what content a fingerprint/user has access to.
 * Used to verify payment before granting access to paid content.
 */
@Entity
@Table(name = "content_access",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_fingerprint_content", 
                           columnNames = {"fingerprint_id", "content_type", "content_id"})
       },
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_fingerprint_id", columnList = "fingerprint_id"),
           @Index(name = "idx_content_type_id", columnList = "content_type, content_id"),
           @Index(name = "idx_payment_transaction_id", columnList = "payment_transaction_id"),
           @Index(name = "idx_access_granted_at", columnList = "access_granted_at"),
           @Index(name = "idx_is_active", columnList = "is_active")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User associated with this access. NULL for anonymous.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Fingerprint ID for access verification.
     */
    @Column(name = "fingerprint_id", nullable = false, length = 64)
    private String fingerprintId;

    /**
     * Type of content (TUVI_INTERPRETATION, HOROSCOPE_LIFETIME, etc.).
     */
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    /**
     * Content identifier (chart_hash for Tu Vi, can_chi for horoscope, etc.).
     */
    @Column(name = "content_id", nullable = false)
    private String contentId;

    /**
     * Payment transaction that granted this access.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id")
    private PaymentTransactionEntity paymentTransaction;

    @Column(name = "access_granted_at", nullable = false, updatable = false)
    private LocalDateTime accessGrantedAt;

    /**
     * When access expires. NULL means permanent access.
     */
    @Column(name = "access_expires_at")
    private LocalDateTime accessExpiresAt;

    /**
     * Number of times this content was accessed.
     */
    @Column(name = "access_count", nullable = false)
    @Builder.Default
    private Integer accessCount = 0;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        accessGrantedAt = LocalDateTime.now();
        if (accessCount == null) {
            accessCount = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Record an access to this content.
     */
    public void recordAccess() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Check if access is still valid.
     */
    public boolean isAccessValid() {
        if (!isActive) {
            return false;
        }
        if (accessExpiresAt != null && LocalDateTime.now().isAfter(accessExpiresAt)) {
            return false;
        }
        return true;
    }

    /**
     * Revoke access.
     */
    public void revoke() {
        this.isActive = false;
    }
}


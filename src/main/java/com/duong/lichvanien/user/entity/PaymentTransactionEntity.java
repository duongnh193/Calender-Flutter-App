package com.duong.lichvanien.user.entity;

import com.duong.lichvanien.user.enums.PaymentStatus;
import com.duong.lichvanien.user.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment transaction entity for tracking payments.
 * Links with fingerprint for anti-cheating purposes.
 */
@Entity
@Table(name = "payment_transaction",
       indexes = {
           @Index(name = "idx_transaction_uuid", columnList = "transaction_uuid"),
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_fingerprint_id", columnList = "fingerprint_id"),
           @Index(name = "idx_session_id", columnList = "session_id"),
           @Index(name = "idx_payment_status", columnList = "payment_status"),
           @Index(name = "idx_payment_method", columnList = "payment_method"),
           @Index(name = "idx_chart_hash", columnList = "chart_hash"),
           @Index(name = "idx_created_at", columnList = "created_at"),
           @Index(name = "idx_completed_at", columnList = "completed_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID for external reference.
     */
    @Column(name = "transaction_uuid", nullable = false, unique = true, length = 36)
    private String transactionUuid;

    /**
     * User associated with this transaction. NULL for anonymous payments.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Fingerprint ID for anti-cheating.
     */
    @Column(name = "fingerprint_id", nullable = false, length = 64)
    private String fingerprintId;

    /**
     * Session that initiated this payment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private UserSessionEntity session;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "VND";

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_gateway_transaction_id")
    private String paymentGatewayTransactionId;

    @Column(name = "payment_gateway_response", columnDefinition = "JSON")
    private String paymentGatewayResponse;

    /**
     * Link with Tu Vi chart hash for interpretation payments.
     */
    @Column(name = "chart_hash", length = 64)
    private String chartHash;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (transactionUuid == null) {
            transactionUuid = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Mark payment as completed.
     */
    public void complete() {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark payment as failed.
     */
    public void fail() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    /**
     * Check if payment is completed.
     */
    public boolean isCompleted() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    /**
     * Check if payment is pending.
     */
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING || paymentStatus == PaymentStatus.PROCESSING;
    }
}


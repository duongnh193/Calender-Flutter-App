package com.duong.lichvanien.sepay.entity;

import com.duong.lichvanien.sepay.enums.SepayTransactionStatus;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.xu.entity.XuPackageEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SePay transaction entity.
 */
@Entity
@Table(name = "sepay_transaction",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_sepay_transaction_id", columnList = "sepay_transaction_id"),
           @Index(name = "idx_content", columnList = "content"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SepayTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "sepay_transaction_id", length = 255)
    private String sepayTransactionId;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "amount_vnd", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountVnd;

    @Column(name = "xu_credited")
    private Integer xuCredited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "xu_package_id")
    private XuPackageEntity xuPackage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SepayTransactionStatus status = SepayTransactionStatus.PENDING;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as completed.
     */
    public void markAsCompleted() {
        this.status = SepayTransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark as failed.
     */
    public void markAsFailed() {
        this.status = SepayTransactionStatus.FAILED;
    }

    /**
     * Mark as cancelled.
     */
    public void markAsCancelled() {
        this.status = SepayTransactionStatus.CANCELLED;
    }
}


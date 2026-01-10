package com.duong.lichvanien.affiliate.entity;

import com.duong.lichvanien.affiliate.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Withdrawal request entity for affiliate commission withdrawal.
 */
@Entity
@Table(name = "withdrawal_request",
       indexes = {
           @Index(name = "idx_affiliate_member_id", columnList = "affiliate_member_id"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_member_id", nullable = false)
    private AffiliateMemberEntity affiliateMember;

    @Column(name = "xu_amount", nullable = false)
    private Integer xuAmount;

    @Column(name = "vnd_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal vndAmount;

    @Column(name = "bank_name", nullable = false, length = 255)
    private String bankName;

    @Column(name = "bank_account", nullable = false, length = 50)
    private String bankAccount;

    @Column(name = "account_holder_name", nullable = false, length = 255)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WithdrawalStatus status = WithdrawalStatus.PENDING;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
     * Approve withdrawal.
     */
    public void approve(String adminNote) {
        this.status = WithdrawalStatus.APPROVED;
        this.adminNote = adminNote;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Reject withdrawal.
     */
    public void reject(String adminNote) {
        this.status = WithdrawalStatus.REJECTED;
        this.adminNote = adminNote;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Mark as completed.
     */
    public void complete() {
        this.status = WithdrawalStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }
}


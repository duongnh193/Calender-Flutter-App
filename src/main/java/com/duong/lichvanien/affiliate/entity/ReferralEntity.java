package com.duong.lichvanien.affiliate.entity;

import com.duong.lichvanien.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Referral entity for tracking referrals.
 */
@Entity
@Table(name = "referral",
       indexes = {
           @Index(name = "idx_referrer_user_id", columnList = "referrer_user_id"),
           @Index(name = "idx_referred_user_id", columnList = "referred_user_id"),
           @Index(name = "idx_affiliate_member_id", columnList = "affiliate_member_id"),
           @Index(name = "idx_commission_paid", columnList = "commission_paid"),
           @Index(name = "idx_created_at", columnList = "created_at")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_referral", columnNames = {"referrer_user_id", "referred_user_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_user_id", nullable = false)
    private UserEntity referrerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_user_id", nullable = false)
    private UserEntity referredUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_member_id")
    private AffiliateMemberEntity affiliateMember;

    @Column(name = "first_payment_amount", precision = 15, scale = 2)
    private BigDecimal firstPaymentAmount;

    @Column(name = "commission_xu")
    private Integer commissionXu;

    @Column(name = "commission_paid", nullable = false)
    @Builder.Default
    private Boolean commissionPaid = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Mark as converted (referred user made first payment).
     */
    public void markAsConverted(BigDecimal paymentAmount) {
        this.firstPaymentAmount = paymentAmount;
        this.convertedAt = LocalDateTime.now();
    }

    /**
     * Set commission and mark as paid.
     */
    public void setCommissionAndMarkPaid(Integer commissionXu) {
        this.commissionXu = commissionXu;
        this.commissionPaid = true;
    }
}


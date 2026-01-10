package com.duong.lichvanien.affiliate.entity;

import com.duong.lichvanien.affiliate.enums.AffiliateStatus;
import com.duong.lichvanien.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Affiliate member entity.
 */
@Entity
@Table(name = "affiliate_member",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_affiliate_code", columnList = "affiliate_code"),
           @Index(name = "idx_status", columnList = "status")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "affiliate_code", nullable = false, unique = true, length = 50)
    private String affiliateCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AffiliateStatus status = AffiliateStatus.ACTIVE;

    @Column(name = "total_referrals", nullable = false)
    @Builder.Default
    private Integer totalReferrals = 0;

    @Column(name = "total_commission_xu", nullable = false)
    @Builder.Default
    private Integer totalCommissionXu = 0;

    @Column(name = "pending_commission_xu", nullable = false)
    @Builder.Default
    private Integer pendingCommissionXu = 0;

    @Column(name = "withdrawn_commission_xu", nullable = false)
    @Builder.Default
    private Integer withdrawnCommissionXu = 0;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        joinedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add commission to pending.
     */
    public void addPendingCommission(Integer xuAmount) {
        this.pendingCommissionXu += xuAmount;
        this.totalCommissionXu += xuAmount;
    }

    /**
     * Move pending commission to withdrawn.
     */
    public void withdrawCommission(Integer xuAmount) {
        if (xuAmount <= this.pendingCommissionXu) {
            this.pendingCommissionXu -= xuAmount;
            this.withdrawnCommissionXu += xuAmount;
        }
    }

    /**
     * Increment referral count.
     */
    public void incrementReferrals() {
        this.totalReferrals++;
    }
}


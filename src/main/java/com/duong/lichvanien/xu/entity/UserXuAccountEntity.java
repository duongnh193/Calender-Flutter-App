package com.duong.lichvanien.xu.entity;

import com.duong.lichvanien.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User xu account entity for tracking xu balance.
 */
@Entity
@Table(name = "user_xu_account",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserXuAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "xu_balance", nullable = false)
    @Builder.Default
    private Integer xuBalance = 0;

    @Column(name = "total_xu_earned", nullable = false)
    @Builder.Default
    private Integer totalXuEarned = 0;

    @Column(name = "total_xu_spent", nullable = false)
    @Builder.Default
    private Integer totalXuSpent = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
     * Add xu to balance.
     */
    public void addXu(Integer amount) {
        if (amount > 0) {
            this.xuBalance += amount;
            this.totalXuEarned += amount;
        }
    }

    /**
     * Deduct xu from balance.
     */
    public void deductXu(Integer amount) {
        if (amount > 0 && this.xuBalance >= amount) {
            this.xuBalance -= amount;
            this.totalXuSpent += amount;
        }
    }

    /**
     * Check if has enough xu.
     */
    public boolean hasEnoughXu(Integer amount) {
        return this.xuBalance >= amount;
    }
}


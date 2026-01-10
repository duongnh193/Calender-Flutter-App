package com.duong.lichvanien.xu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Xu package entity for purchase packages.
 */
@Entity
@Table(name = "xu_package",
       indexes = {
           @Index(name = "idx_is_active", columnList = "is_active"),
           @Index(name = "idx_display_order", columnList = "display_order")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "xu_amount", nullable = false)
    private Integer xuAmount;

    @Column(name = "price_vnd", nullable = false, precision = 15, scale = 2)
    private BigDecimal priceVnd;

    @Column(name = "bonus_xu", nullable = false)
    @Builder.Default
    private Integer bonusXu = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

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
     * Get total xu (amount + bonus).
     */
    public Integer getTotalXu() {
        return xuAmount + bonusXu;
    }
}


package com.duong.lichvanien.xu.entity;

import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.xu.enums.XuTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Xu transaction entity for transaction history.
 */
@Entity
@Table(name = "xu_transaction",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_transaction_type", columnList = "transaction_type"),
           @Index(name = "idx_reference_id", columnList = "reference_id"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private XuTransactionType transactionType;

    @Column(name = "xu_amount", nullable = false)
    private Integer xuAmount; // Positive for credit, negative for debit

    @Column(name = "vnd_amount", precision = 15, scale = 2)
    private BigDecimal vndAmount;

    @Column(name = "reference_id", length = 255)
    private String referenceId; // Reference to related transaction

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Check if transaction is credit (adds xu).
     */
    public boolean isCredit() {
        return xuAmount > 0;
    }

    /**
     * Check if transaction is debit (deducts xu).
     */
    public boolean isDebit() {
        return xuAmount < 0;
    }
}


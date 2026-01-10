package com.duong.lichvanien.sepay.repository;

import com.duong.lichvanien.sepay.entity.SepayTransactionEntity;
import com.duong.lichvanien.sepay.enums.SepayTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for SepayTransactionEntity.
 */
@Repository
public interface SepayTransactionRepository extends JpaRepository<SepayTransactionEntity, Long> {

    /**
     * Find by SePay transaction ID.
     */
    Optional<SepayTransactionEntity> findBySepayTransactionId(String sepayTransactionId);

    /**
     * Find by content (for matching webhook).
     */
    Optional<SepayTransactionEntity> findByContentAndStatus(String content, SepayTransactionStatus status);

    /**
     * Find all by user ID.
     */
    Page<SepayTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find all by status.
     */
    Page<SepayTransactionEntity> findByStatusOrderByCreatedAtDesc(SepayTransactionStatus status, Pageable pageable);

    /**
     * Sum amount by status in date range.
     */
    @Query("SELECT COALESCE(SUM(s.amountVnd), 0) FROM SepayTransactionEntity s " +
           "WHERE s.status = :status AND s.createdAt BETWEEN :start AND :end")
    BigDecimal sumAmountByStatusAndDateRange(@Param("status") SepayTransactionStatus status,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    /**
     * Count by status in date range.
     */
    @Query("SELECT COUNT(s) FROM SepayTransactionEntity s " +
           "WHERE s.status = :status AND s.createdAt BETWEEN :start AND :end")
    long countByStatusAndDateRange(@Param("status") SepayTransactionStatus status,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    /**
     * Find by ID with user and xu package (fetch join to avoid lazy loading issues).
     */
    @Query("SELECT s FROM SepayTransactionEntity s " +
           "LEFT JOIN FETCH s.user " +
           "LEFT JOIN FETCH s.xuPackage " +
           "WHERE s.id = :id")
    Optional<SepayTransactionEntity> findByIdWithRelations(@Param("id") Long id);
}


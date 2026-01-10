package com.duong.lichvanien.xu.repository;

import com.duong.lichvanien.xu.entity.XuTransactionEntity;
import com.duong.lichvanien.xu.enums.XuTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for XuTransactionEntity.
 */
@Repository
public interface XuTransactionRepository extends JpaRepository<XuTransactionEntity, Long> {

    /**
     * Find all transactions for a user.
     */
    Page<XuTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find all transactions for a user with user entity (fetch join to avoid lazy loading issues).
     */
    @Query("SELECT t FROM XuTransactionEntity t " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.user.id = :userId " +
           "ORDER BY t.createdAt DESC")
    Page<XuTransactionEntity> findByUserIdWithUserOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find transactions by type for a user.
     */
    List<XuTransactionEntity> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(Long userId, XuTransactionType transactionType);

    /**
     * Find transactions by reference ID.
     */
    List<XuTransactionEntity> findByReferenceId(String referenceId);

    /**
     * Sum xu amount by transaction type in date range.
     */
    @Query("SELECT COALESCE(SUM(t.xuAmount), 0) FROM XuTransactionEntity t " +
           "WHERE t.transactionType = :type AND t.createdAt BETWEEN :start AND :end")
    Integer sumXuAmountByTypeAndDateRange(@Param("type") XuTransactionType type,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    /**
     * Count transactions by type in date range.
     */
    @Query("SELECT COUNT(t) FROM XuTransactionEntity t " +
           "WHERE t.transactionType = :type AND t.createdAt BETWEEN :start AND :end")
    Long countByTypeAndDateRange(@Param("type") XuTransactionType type,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    /**
     * Find all transactions with user entity (fetch join to avoid lazy loading issues).
     */
    @Query("SELECT t FROM XuTransactionEntity t " +
           "LEFT JOIN FETCH t.user " +
           "ORDER BY t.createdAt DESC")
    Page<XuTransactionEntity> findAllWithUserOrderByCreatedAtDesc(Pageable pageable);
}


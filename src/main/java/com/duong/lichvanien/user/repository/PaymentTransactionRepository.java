package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.PaymentTransactionEntity;
import com.duong.lichvanien.user.enums.PaymentStatus;
import com.duong.lichvanien.user.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentTransactionEntity.
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {

    /**
     * Find transaction by UUID.
     */
    Optional<PaymentTransactionEntity> findByTransactionUuid(String transactionUuid);

    /**
     * Find transactions by user ID.
     */
    Page<PaymentTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find transactions by fingerprint ID.
     */
    List<PaymentTransactionEntity> findByFingerprintIdOrderByCreatedAtDesc(String fingerprintId);

    /**
     * Find completed transaction by fingerprint and chart hash.
     * Used to check if payment was made for a specific Tu Vi chart.
     */
    Optional<PaymentTransactionEntity> findByFingerprintIdAndChartHashAndPaymentStatus(
            String fingerprintId, String chartHash, PaymentStatus paymentStatus);

    /**
     * Check if payment exists for fingerprint and content.
     */
    @Query("SELECT COUNT(t) > 0 FROM PaymentTransactionEntity t " +
           "WHERE t.fingerprintId = :fingerprintId " +
           "AND t.contentType = :contentType " +
           "AND t.contentId = :contentId " +
           "AND t.paymentStatus = 'COMPLETED'")
    boolean existsCompletedPayment(
            @Param("fingerprintId") String fingerprintId,
            @Param("contentType") String contentType,
            @Param("contentId") String contentId);

    /**
     * Find transactions by status.
     */
    List<PaymentTransactionEntity> findByPaymentStatus(PaymentStatus status);

    /**
     * Find pending transactions older than threshold (for cleanup).
     */
    List<PaymentTransactionEntity> findByPaymentStatusAndCreatedAtBefore(
            PaymentStatus status, LocalDateTime threshold);

    /**
     * Find transactions by payment gateway transaction ID.
     */
    Optional<PaymentTransactionEntity> findByPaymentGatewayTransactionId(String gatewayTransactionId);

    /**
     * Count transactions by fingerprint and type.
     */
    long countByFingerprintIdAndTransactionType(String fingerprintId, TransactionType type);

    /**
     * Find transactions by type and date range.
     */
    List<PaymentTransactionEntity> findByTransactionTypeAndCreatedAtBetween(
            TransactionType type, LocalDateTime start, LocalDateTime end);

    /**
     * Sum amount by status and date range (for reporting).
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM PaymentTransactionEntity t " +
           "WHERE t.paymentStatus = :status " +
           "AND t.createdAt BETWEEN :start AND :end")
    java.math.BigDecimal sumAmountByStatusAndDateRange(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}


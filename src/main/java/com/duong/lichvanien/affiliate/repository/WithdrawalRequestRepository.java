package com.duong.lichvanien.affiliate.repository;

import com.duong.lichvanien.affiliate.entity.WithdrawalRequestEntity;
import com.duong.lichvanien.affiliate.enums.WithdrawalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for WithdrawalRequestEntity.
 */
@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequestEntity, Long> {

    /**
     * Find all by affiliate member ID.
     */
    Page<WithdrawalRequestEntity> findByAffiliateMemberIdOrderByCreatedAtDesc(Long affiliateMemberId, Pageable pageable);

    /**
     * Find all by status.
     */
    Page<WithdrawalRequestEntity> findByStatusOrderByCreatedAtDesc(WithdrawalStatus status, Pageable pageable);

    /**
     * Count by status.
     */
    long countByStatus(WithdrawalStatus status);

    /**
     * Sum vnd amount by status.
     */
    @Query("SELECT COALESCE(SUM(w.vndAmount), 0) FROM WithdrawalRequestEntity w WHERE w.status = :status")
    BigDecimal sumVndAmountByStatus(@Param("status") WithdrawalStatus status);

    /**
     * Find pending withdrawals.
     */
    List<WithdrawalRequestEntity> findByStatusOrderByCreatedAtAsc(WithdrawalStatus status);
}


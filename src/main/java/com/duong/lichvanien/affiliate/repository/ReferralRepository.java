package com.duong.lichvanien.affiliate.repository;

import com.duong.lichvanien.affiliate.entity.ReferralEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ReferralEntity.
 */
@Repository
public interface ReferralRepository extends JpaRepository<ReferralEntity, Long> {

    /**
     * Find by referrer user ID.
     */
    Page<ReferralEntity> findByReferrerUserIdOrderByCreatedAtDesc(Long referrerUserId, Pageable pageable);

    /**
     * Find by referred user ID.
     */
    Optional<ReferralEntity> findByReferredUserId(Long referredUserId);

    /**
     * Find by affiliate member ID.
     */
    List<ReferralEntity> findByAffiliateMemberIdOrderByCreatedAtDesc(Long affiliateMemberId);

    /**
     * Count referrals by referrer user ID.
     */
    long countByReferrerUserId(Long referrerUserId);

    /**
     * Count converted referrals (with first payment) by referrer user ID.
     */
    @Query("SELECT COUNT(r) FROM ReferralEntity r WHERE r.referrerUser.id = :referrerUserId AND r.convertedAt IS NOT NULL")
    long countConvertedReferralsByReferrerUserId(@Param("referrerUserId") Long referrerUserId);

    /**
     * Find all referrals with commission not paid.
     */
    @Query("SELECT r FROM ReferralEntity r WHERE r.commissionPaid = false AND r.commissionXu IS NOT NULL")
    List<ReferralEntity> findUnpaidCommissions();
}


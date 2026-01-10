package com.duong.lichvanien.affiliate.repository;

import com.duong.lichvanien.affiliate.entity.AffiliateMemberEntity;
import com.duong.lichvanien.affiliate.enums.AffiliateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AffiliateMemberEntity.
 */
@Repository
public interface AffiliateMemberRepository extends JpaRepository<AffiliateMemberEntity, Long> {

    /**
     * Find by user ID.
     */
    Optional<AffiliateMemberEntity> findByUserId(Long userId);

    /**
     * Find by affiliate code.
     */
    Optional<AffiliateMemberEntity> findByAffiliateCode(String affiliateCode);

    /**
     * Find all by status.
     */
    Page<AffiliateMemberEntity> findByStatus(AffiliateStatus status, Pageable pageable);

    /**
     * Count by status.
     */
    long countByStatus(AffiliateStatus status);

    /**
     * Sum total commission xu.
     */
    @Query("SELECT COALESCE(SUM(m.totalCommissionXu), 0) FROM AffiliateMemberEntity m")
    Integer sumTotalCommissionXu();

    /**
     * Sum pending commission xu.
     */
    @Query("SELECT COALESCE(SUM(m.pendingCommissionXu), 0) FROM AffiliateMemberEntity m")
    Integer sumPendingCommissionXu();
}


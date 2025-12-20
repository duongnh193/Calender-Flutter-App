package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.NatalChartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for NatalChartEntity.
 */
@Repository
public interface NatalChartRepository extends JpaRepository<NatalChartEntity, Long> {

    /**
     * Find natal chart by chart hash.
     * This is the main lookup method for FACT data.
     *
     * @param chartHash SHA-256 hash of the canonical chart FACT data
     * @return Optional containing the natal chart entity if found
     */
    Optional<NatalChartEntity> findByChartHash(String chartHash);

    /**
     * Check if chart hash exists.
     *
     * @param chartHash SHA-256 hash
     * @return true if exists
     */
    boolean existsByChartHash(String chartHash);
}

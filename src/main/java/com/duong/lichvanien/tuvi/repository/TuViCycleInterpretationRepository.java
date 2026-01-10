package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.TuViCycleInterpretationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Tu Vi Cycle Interpretation entities.
 */
@Repository
public interface TuViCycleInterpretationRepository extends JpaRepository<TuViCycleInterpretationEntity, Long> {

    /**
     * Find cycle interpretation by chart hash and gender.
     */
    Optional<TuViCycleInterpretationEntity> findByChartHashAndGender(
            String chartHash, TuViCycleInterpretationEntity.Gender gender);

    /**
     * Check if cycle interpretation exists for chart hash and gender.
     */
    boolean existsByChartHashAndGender(String chartHash, TuViCycleInterpretationEntity.Gender gender);

    /**
     * Delete all interpretations for a specific chart hash.
     */
    void deleteByChartHash(String chartHash);
}


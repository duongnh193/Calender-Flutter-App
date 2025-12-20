package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.TuViStarInterpretationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TuViStarInterpretationEntity.
 */
@Repository
public interface TuViStarInterpretationRepository extends JpaRepository<TuViStarInterpretationEntity, Long> {

    /**
     * Find all star interpretations for a given palace interpretation.
     *
     * @param palaceInterpretationId The palace interpretation ID
     * @return List of star interpretations
     */
    List<TuViStarInterpretationEntity> findByPalaceInterpretationId(Long palaceInterpretationId);
}

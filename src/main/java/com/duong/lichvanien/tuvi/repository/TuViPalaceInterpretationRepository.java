package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.TuViPalaceInterpretationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TuViPalaceInterpretationEntity.
 */
@Repository
public interface TuViPalaceInterpretationRepository extends JpaRepository<TuViPalaceInterpretationEntity, Long> {

    /**
     * Find all palace interpretations for a given interpretation.
     *
     * @param interpretationId The interpretation ID
     * @return List of palace interpretations (should be 12 for a complete chart)
     */
    List<TuViPalaceInterpretationEntity> findByInterpretationId(Long interpretationId);

    /**
     * Find a specific palace interpretation by interpretation ID and palace code.
     *
     * @param interpretationId The interpretation ID
     * @param palaceCode The palace code (e.g., "MENH", "QUAN_LOC")
     * @return Optional containing the palace interpretation if found
     */
    Optional<TuViPalaceInterpretationEntity> findByInterpretationIdAndPalaceCode(Long interpretationId, String palaceCode);

    /**
     * Delete all palace interpretations for a given interpretation ID.
     * Used when updating an existing interpretation.
     *
     * @param interpretationId The interpretation ID
     */
    void deleteByInterpretationId(Long interpretationId);
}

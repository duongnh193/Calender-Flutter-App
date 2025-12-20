package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.TuViInterpretationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for TuViInterpretationEntity.
 */
@Repository
public interface TuViInterpretationRepository extends JpaRepository<TuViInterpretationEntity, Long> {

    /**
     * Find interpretation by chart hash and gender.
     * This is the main lookup method for retrieving cached interpretations.
     *
     * @param chartHash SHA-256 hash of the chart structure
     * @param gender Gender of the chart owner
     * @return Optional containing the interpretation entity if found
     */
    Optional<TuViInterpretationEntity> findByChartHashAndGender(String chartHash, TuViInterpretationEntity.Gender gender);
}

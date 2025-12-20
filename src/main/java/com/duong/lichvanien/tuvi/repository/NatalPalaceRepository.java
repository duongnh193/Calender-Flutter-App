package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.NatalPalaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NatalPalaceEntity.
 */
@Repository
public interface NatalPalaceRepository extends JpaRepository<NatalPalaceEntity, Long> {

    /**
     * Find all palaces for a given natal chart.
     *
     * @param natalChartId The natal chart ID
     * @return List of palaces (should be 12 for a complete chart), ordered by palace_index
     */
    List<NatalPalaceEntity> findByNatalChartIdOrderByPalaceIndex(Long natalChartId);

    /**
     * Find a specific palace by natal chart ID and palace code.
     *
     * @param natalChartId The natal chart ID
     * @param palaceCode The palace code (e.g., "MENH", "QUAN_LOC")
     * @return Optional containing the palace entity if found
     */
    Optional<NatalPalaceEntity> findByNatalChartIdAndPalaceCode(Long natalChartId, String palaceCode);
}

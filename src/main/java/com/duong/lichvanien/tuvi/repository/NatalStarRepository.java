package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.NatalStarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for NatalStarEntity.
 */
@Repository
public interface NatalStarRepository extends JpaRepository<NatalStarEntity, Long> {

    /**
     * Find all stars for a given natal palace, ordered by star_order.
     *
     * @param natalPalaceId The natal palace ID
     * @return List of stars ordered by star_order
     */
    List<NatalStarEntity> findByNatalPalaceIdOrderByStarOrder(Long natalPalaceId);
}

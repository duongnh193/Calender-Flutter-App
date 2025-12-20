package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for InterpretationFragmentEntity.
 */
@Repository
public interface InterpretationFragmentRepository extends JpaRepository<InterpretationFragmentEntity, Long> {

    /**
     * Find fragment by fragment code.
     */
    Optional<InterpretationFragmentEntity> findByFragmentCode(String fragmentCode);
}

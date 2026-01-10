package com.duong.lichvanien.affiliate.repository;

import com.duong.lichvanien.affiliate.entity.AffiliateConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AffiliateConfigEntity.
 */
@Repository
public interface AffiliateConfigRepository extends JpaRepository<AffiliateConfigEntity, Long> {

    /**
     * Find by config key.
     */
    Optional<AffiliateConfigEntity> findByConfigKey(String configKey);
}


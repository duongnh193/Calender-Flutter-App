package com.duong.lichvanien.xu.repository;

import com.duong.lichvanien.xu.entity.XuPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for XuPackageEntity.
 */
@Repository
public interface XuPackageRepository extends JpaRepository<XuPackageEntity, Long> {

    /**
     * Find all active packages ordered by display order.
     */
    @Query("SELECT p FROM XuPackageEntity p WHERE p.isActive = true ORDER BY p.displayOrder ASC, p.id ASC")
    List<XuPackageEntity> findAllActiveOrderByDisplayOrder();

    /**
     * Find all packages ordered by display order.
     */
    @Query("SELECT p FROM XuPackageEntity p ORDER BY p.displayOrder ASC, p.id ASC")
    List<XuPackageEntity> findAllOrderByDisplayOrder();
}


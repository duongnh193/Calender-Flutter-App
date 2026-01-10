package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.FingerprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for FingerprintEntity.
 */
@Repository
public interface FingerprintRepository extends JpaRepository<FingerprintEntity, Long> {

    /**
     * Find fingerprint by ID (SHA-256 hash).
     */
    Optional<FingerprintEntity> findByFingerprintId(String fingerprintId);

    /**
     * Find fingerprint by normalized hash.
     */
    Optional<FingerprintEntity> findByNormalizedHash(String normalizedHash);

    /**
     * Check if fingerprint exists.
     */
    boolean existsByFingerprintId(String fingerprintId);

    /**
     * Find fingerprints by IP address.
     */
    List<FingerprintEntity> findByIpAddress(String ipAddress);

    /**
     * Increment usage count.
     */
    @Modifying
    @Query("UPDATE FingerprintEntity f SET f.usageCount = f.usageCount + 1, f.lastSeenAt = :now WHERE f.fingerprintId = :fingerprintId")
    int incrementUsageCount(@Param("fingerprintId") String fingerprintId, @Param("now") LocalDateTime now);

    /**
     * Find fingerprints with high usage count (potential abuse).
     */
    List<FingerprintEntity> findByUsageCountGreaterThan(int threshold);

    /**
     * Find fingerprints seen after a certain date.
     */
    List<FingerprintEntity> findByLastSeenAtAfter(LocalDateTime date);

    /**
     * Count fingerprints by IP address.
     */
    long countByIpAddress(String ipAddress);
}


package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.ContentAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ContentAccessEntity.
 */
@Repository
public interface ContentAccessRepository extends JpaRepository<ContentAccessEntity, Long> {

    /**
     * Find access by fingerprint, content type, and content ID.
     */
    Optional<ContentAccessEntity> findByFingerprintIdAndContentTypeAndContentId(
            String fingerprintId, String contentType, String contentId);

    /**
     * Check if active access exists.
     */
    @Query("SELECT COUNT(c) > 0 FROM ContentAccessEntity c " +
           "WHERE c.fingerprintId = :fingerprintId " +
           "AND c.contentType = :contentType " +
           "AND c.contentId = :contentId " +
           "AND c.isActive = true " +
           "AND (c.accessExpiresAt IS NULL OR c.accessExpiresAt > :now)")
    boolean hasActiveAccess(
            @Param("fingerprintId") String fingerprintId,
            @Param("contentType") String contentType,
            @Param("contentId") String contentId,
            @Param("now") LocalDateTime now);

    /**
     * Find all access records by fingerprint.
     */
    List<ContentAccessEntity> findByFingerprintIdAndIsActiveTrue(String fingerprintId);

    /**
     * Find all access records by user.
     */
    List<ContentAccessEntity> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Increment access count.
     */
    @Modifying
    @Query("UPDATE ContentAccessEntity c SET c.accessCount = c.accessCount + 1, c.lastAccessedAt = :now " +
           "WHERE c.fingerprintId = :fingerprintId AND c.contentType = :contentType AND c.contentId = :contentId")
    int incrementAccessCount(
            @Param("fingerprintId") String fingerprintId,
            @Param("contentType") String contentType,
            @Param("contentId") String contentId,
            @Param("now") LocalDateTime now);

    /**
     * Find expired access records.
     */
    List<ContentAccessEntity> findByAccessExpiresAtBeforeAndIsActiveTrue(LocalDateTime now);

    /**
     * Revoke expired access.
     */
    @Modifying
    @Query("UPDATE ContentAccessEntity c SET c.isActive = false " +
           "WHERE c.accessExpiresAt < :now AND c.isActive = true")
    int revokeExpiredAccess(@Param("now") LocalDateTime now);

    /**
     * Count access records by content type.
     */
    long countByContentTypeAndIsActiveTrue(String contentType);
}


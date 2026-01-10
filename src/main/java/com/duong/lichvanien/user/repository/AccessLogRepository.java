package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.AccessLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AccessLogEntity.
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLogEntity, Long> {

    /**
     * Find logs by user ID.
     */
    Page<AccessLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find logs by fingerprint ID.
     */
    Page<AccessLogEntity> findByFingerprintIdOrderByCreatedAtDesc(String fingerprintId, Pageable pageable);

    /**
     * Find logs by endpoint.
     */
    List<AccessLogEntity> findByEndpointAndCreatedAtAfter(String endpoint, LocalDateTime after);

    /**
     * Find logs by IP address.
     */
    List<AccessLogEntity> findByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime after);

    /**
     * Count requests by fingerprint and endpoint in time range (rate limiting).
     */
    @Query("SELECT COUNT(a) FROM AccessLogEntity a " +
           "WHERE a.fingerprintId = :fingerprintId " +
           "AND a.endpoint = :endpoint " +
           "AND a.createdAt > :since")
    long countByFingerprintAndEndpointSince(
            @Param("fingerprintId") String fingerprintId,
            @Param("endpoint") String endpoint,
            @Param("since") LocalDateTime since);

    /**
     * Find duplicate requests (same fingerprint, endpoint, and request body hash).
     */
    @Query("SELECT a FROM AccessLogEntity a " +
           "WHERE a.fingerprintId = :fingerprintId " +
           "AND a.endpoint = :endpoint " +
           "AND a.requestBodyHash = :bodyHash " +
           "AND a.createdAt > :since")
    List<AccessLogEntity> findDuplicateRequests(
            @Param("fingerprintId") String fingerprintId,
            @Param("endpoint") String endpoint,
            @Param("bodyHash") String bodyHash,
            @Param("since") LocalDateTime since);

    /**
     * Delete old logs (for cleanup).
     */
    @Modifying
    @Query("DELETE FROM AccessLogEntity a WHERE a.createdAt < :threshold")
    int deleteOldLogs(@Param("threshold") LocalDateTime threshold);

    /**
     * Count requests by endpoint and date range (for analytics).
     */
    long countByEndpointAndCreatedAtBetween(String endpoint, LocalDateTime start, LocalDateTime end);

    /**
     * Find logs with specific response status.
     */
    List<AccessLogEntity> findByResponseStatusAndCreatedAtAfter(Integer status, LocalDateTime after);
}


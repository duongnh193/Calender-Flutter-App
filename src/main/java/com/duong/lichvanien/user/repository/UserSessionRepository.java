package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserSessionEntity.
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    /**
     * Find session by token.
     */
    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

    /**
     * Find session by refresh token.
     */
    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

    /**
     * Find active session by token.
     */
    Optional<UserSessionEntity> findBySessionTokenAndIsActiveTrue(String sessionToken);

    /**
     * Find active sessions by user ID.
     */
    List<UserSessionEntity> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Find sessions by fingerprint ID.
     */
    List<UserSessionEntity> findByFingerprintId(String fingerprintId);

    /**
     * Find active sessions by fingerprint ID.
     */
    List<UserSessionEntity> findByFingerprintIdAndIsActiveTrue(String fingerprintId);

    /**
     * Revoke all sessions for a user.
     */
    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.isActive = false WHERE s.user.id = :userId")
    int revokeAllUserSessions(@Param("userId") Long userId);

    /**
     * Revoke session by token.
     */
    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.isActive = false WHERE s.sessionToken = :token")
    int revokeBySessionToken(@Param("token") String token);

    /**
     * Update last activity.
     */
    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.lastActivityAt = :now WHERE s.sessionToken = :token")
    int updateLastActivity(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Find expired sessions.
     */
    List<UserSessionEntity> findByExpiresAtBeforeAndIsActiveTrue(LocalDateTime now);

    /**
     * Count active sessions by user.
     */
    long countByUserIdAndIsActiveTrue(Long userId);

    /**
     * Count active sessions by fingerprint.
     */
    long countByFingerprintIdAndIsActiveTrue(String fingerprintId);

    /**
     * Delete expired sessions.
     */
    @Modifying
    @Query("DELETE FROM UserSessionEntity s WHERE s.expiresAt < :threshold AND s.isActive = false")
    int deleteExpiredSessions(@Param("threshold") LocalDateTime threshold);
}


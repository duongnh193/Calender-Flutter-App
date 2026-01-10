package com.duong.lichvanien.user.repository;

import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find user by UUID.
     */
    Optional<UserEntity> findByUuid(String uuid);

    /**
     * Find user by email.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Find user by phone.
     */
    Optional<UserEntity> findByPhone(String phone);

    /**
     * Find user by email or phone (for login).
     */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<UserEntity> findByEmailOrPhone(@Param("identifier") String identifier);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists.
     */
    boolean existsByPhone(String phone);

    /**
     * Find active user by email.
     */
    Optional<UserEntity> findByEmailAndStatus(String email, UserStatus status);

    /**
     * Find active user by phone.
     */
    Optional<UserEntity> findByPhoneAndStatus(String phone, UserStatus status);

    /**
     * Count users by status.
     */
    long countByStatus(UserStatus status);

    /**
     * Count users by role.
     */
    long countByRole(UserRole role);

    /**
     * Find users by status with pagination.
     */
    Page<UserEntity> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find users by role with pagination.
     */
    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    /**
     * Search users by email or phone (partial match).
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "u.phone LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<UserEntity> searchByEmailOrPhone(@Param("query") String query, Pageable pageable);

    /**
     * Find user by referral code.
     */
    Optional<UserEntity> findByReferralCode(String referralCode);
}


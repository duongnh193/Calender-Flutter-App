package com.duong.lichvanien.xu.repository;

import com.duong.lichvanien.xu.entity.UserXuAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserXuAccountEntity.
 */
@Repository
public interface UserXuAccountRepository extends JpaRepository<UserXuAccountEntity, Long> {

    /**
     * Find by user ID.
     */
    Optional<UserXuAccountEntity> findByUserId(Long userId);

    /**
     * Find or create account for user.
     * If not exists, creates a new account with balance 0.
     */
    @Query("SELECT a FROM UserXuAccountEntity a WHERE a.user.id = :userId")
    Optional<UserXuAccountEntity> findByUserIdQuery(@Param("userId") Long userId);
}


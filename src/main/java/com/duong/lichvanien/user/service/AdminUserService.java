package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.dto.AdminCreateUserRequest;
import com.duong.lichvanien.user.dto.AdminUpdateUserRequest;
import com.duong.lichvanien.user.dto.AdminUserResponse;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.enums.UserStatus;
import com.duong.lichvanien.user.exception.UserAlreadyExistsException;
import com.duong.lichvanien.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

/**
 * Admin service for user management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users with pagination.
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toAdminResponse);
    }

    /**
     * Get users by status with pagination.
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable)
                .map(this::toAdminResponse);
    }

    /**
     * Get users by role with pagination.
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(this::toAdminResponse);
    }

    /**
     * Get user by ID.
     */
    @Transactional(readOnly = true)
    public Optional<AdminUserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toAdminResponse);
    }

    /**
     * Get user by UUID.
     */
    @Transactional(readOnly = true)
    public Optional<AdminUserResponse> getUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .map(this::toAdminResponse);
    }

    /**
     * Search users by email or phone.
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> searchUsers(String query, Pageable pageable) {
        return userRepository.searchByEmailOrPhone(query, pageable)
                .map(this::toAdminResponse);
    }

    /**
     * Create new user (admin).
     */
    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        // Validate email/phone uniqueness
        if (StringUtils.hasText(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email đã tồn tại: " + request.getEmail());
            }
        }
        if (StringUtils.hasText(request.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new UserAlreadyExistsException("Số điện thoại đã tồn tại: " + request.getPhone());
            }
        }

        // Must have email or phone
        if (!StringUtils.hasText(request.getEmail()) && !StringUtils.hasText(request.getPhone())) {
            throw new IllegalArgumentException("Email hoặc số điện thoại là bắt buộc");
        }

        UserEntity user = UserEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(UserStatus.ACTIVE)
                .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("Admin created user: {} with role: {}", saved.getUuid(), saved.getRole());

        return toAdminResponse(saved);
    }

    /**
     * Update user (admin).
     */
    @Transactional
    public Optional<AdminUserResponse> updateUser(Long id, AdminUpdateUserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    // Update email if provided and different
                    if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new UserAlreadyExistsException("Email đã tồn tại: " + request.getEmail());
                        }
                        user.setEmail(request.getEmail());
                    }

                    // Update phone if provided and different
                    if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
                        if (userRepository.existsByPhone(request.getPhone())) {
                            throw new UserAlreadyExistsException("Số điện thoại đã tồn tại: " + request.getPhone());
                        }
                        user.setPhone(request.getPhone());
                    }

                    // Update full name if provided
                    if (request.getFullName() != null) {
                        user.setFullName(request.getFullName());
                    }

                    // Update status if provided
                    if (request.getStatus() != null) {
                        user.setStatus(request.getStatus());
                    }

                    // Update role if provided
                    if (request.getRole() != null) {
                        user.setRole(request.getRole());
                    }

                    UserEntity saved = userRepository.save(user);
                    log.info("Admin updated user: {}", saved.getUuid());

                    return toAdminResponse(saved);
                });
    }

    /**
     * Update user status.
     */
    @Transactional
    public Optional<AdminUserResponse> updateUserStatus(Long id, UserStatus status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    UserEntity saved = userRepository.save(user);
                    log.info("Admin updated user status: {} -> {}", saved.getUuid(), status);
                    return toAdminResponse(saved);
                });
    }

    /**
     * Update user role.
     */
    @Transactional
    public Optional<AdminUserResponse> updateUserRole(Long id, UserRole role) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(role);
                    UserEntity saved = userRepository.save(user);
                    log.info("Admin updated user role: {} -> {}", saved.getUuid(), role);
                    return toAdminResponse(saved);
                });
    }

    /**
     * Ban user.
     */
    @Transactional
    public Optional<AdminUserResponse> banUser(Long id) {
        return updateUserStatus(id, UserStatus.BANNED);
    }

    /**
     * Activate user.
     */
    @Transactional
    public Optional<AdminUserResponse> activateUser(Long id) {
        return updateUserStatus(id, UserStatus.ACTIVE);
    }

    /**
     * Deactivate user.
     */
    @Transactional
    public Optional<AdminUserResponse> deactivateUser(Long id) {
        return updateUserStatus(id, UserStatus.INACTIVE);
    }

    /**
     * Reset user password.
     */
    @Transactional
    public Optional<AdminUserResponse> resetPassword(Long id, String newPassword) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    UserEntity saved = userRepository.save(user);
                    log.info("Admin reset password for user: {}", saved.getUuid());
                    return toAdminResponse(saved);
                });
    }

    /**
     * Delete user (soft delete by setting status to BANNED).
     */
    @Transactional
    public boolean deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(UserStatus.BANNED);
                    userRepository.save(user);
                    log.info("Admin soft-deleted user: {}", user.getUuid());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Hard delete user (permanent).
     */
    @Transactional
    public boolean hardDeleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Admin hard-deleted user: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Get user statistics.
     */
    @Transactional(readOnly = true)
    public UserStatistics getStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveUsers = userRepository.countByStatus(UserStatus.INACTIVE);
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED);
        long adminUsers = userRepository.countByRole(UserRole.ADMIN);

        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .bannedUsers(bannedUsers)
                .adminUsers(adminUsers)
                .build();
    }

    /**
     * Convert entity to admin response.
     */
    private AdminUserResponse toAdminResponse(UserEntity user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    /**
     * User statistics DTO.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStatistics {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long bannedUsers;
        private long adminUsers;
    }
}


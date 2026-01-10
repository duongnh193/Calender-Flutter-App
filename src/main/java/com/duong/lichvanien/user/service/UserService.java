package com.duong.lichvanien.user.service;

import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.user.dto.*;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.UserStatus;
import com.duong.lichvanien.user.exception.InvalidCredentialsException;
import com.duong.lichvanien.user.exception.UserAlreadyExistsException;
import com.duong.lichvanien.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final FingerprintService fingerprintService;
    private final AffiliateService affiliateService;

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(UserRegisterRequest request, HttpServletRequest httpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email", request.getEmail());
        }
        
        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("Số điện thoại", request.getPhone());
        }
        
        // Handle referral code if provided
        Long referrerUserId = null;
        if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
            Optional<UserEntity> referrerByCode = userRepository.findByReferralCode(request.getReferralCode());
            if (referrerByCode.isPresent()) {
                referrerUserId = referrerByCode.get().getId();
            }
        }
        
        // Generate unique referral code for new user
        String referralCode = generateReferralCode();
        
        // Get referrer user entity if exists
        UserEntity referrerUserEntity = null;
        if (referrerUserId != null) {
            referrerUserEntity = userRepository.findById(referrerUserId).orElse(null);
        }
        
        // Create user entity
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(UserStatus.ACTIVE)
                .referralCode(referralCode)
                .referredByUser(referrerUserEntity)
                .referralCount(0)
                .build();
        
        user = userRepository.save(user);
        
        log.info("New user registered: {} with referral code: {}", user.getUsername(), referralCode);
        
        // Create referral if referrer exists
        if (referrerUserId != null) {
            try {
                affiliateService.createReferral(referrerUserId, user.getId());
                log.info("Created referral: {} -> {}", referrerUserId, user.getId());
            } catch (Exception e) {
                log.warn("Failed to create referral: {}", e.getMessage());
            }
        }
        
        // Generate fingerprint and create session
        String fingerprintId = fingerprintService.generateFingerprintId(null, httpRequest);
        fingerprintService.generateFingerprint(null, httpRequest);
        
        return sessionService.createSession(user, fingerprintId, httpRequest);
    }
    
    /**
     * Generate unique referral code for user.
     */
    private String generateReferralCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "REF" + uuid;
    }

    /**
     * Login user.
     */
    @Transactional
    public AuthResponse login(UserLoginRequest request, HttpServletRequest httpRequest) {
        // Find user by email or phone
        UserEntity user = userRepository.findByEmailOrPhone(request.getIdentifier())
                .orElseThrow(InvalidCredentialsException::new);
        
        // Check if user is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("Tài khoản đã bị khóa hoặc vô hiệu hóa");
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate fingerprint
        FingerprintRequest fingerprintRequest = request.getFingerprint();
        String fingerprintId = fingerprintService.generateFingerprintId(fingerprintRequest, httpRequest);
        fingerprintService.generateFingerprint(fingerprintRequest, httpRequest);
        
        log.info("User logged in: {}", user.getUsername());
        
        return sessionService.createSession(user, fingerprintId, httpRequest);
    }

    /**
     * Get user by ID.
     */
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by UUID.
     */
    public Optional<UserEntity> getUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    /**
     * Get user by email.
     */
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by phone.
     */
    public Optional<UserEntity> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    /**
     * Update user information.
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        // Check email uniqueness if changing
        if (request.getEmail() != null && !request.getEmail().isBlank() 
                && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        // Check phone uniqueness if changing
        if (request.getPhone() != null && !request.getPhone().isBlank() 
                && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new UserAlreadyExistsException("Số điện thoại", request.getPhone());
            }
            user.setPhone(request.getPhone());
        }
        
        // Update full name if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        user = userRepository.save(user);
        
        log.info("User updated: {}", user.getUsername());
        
        return UserResponse.fromEntity(user);
    }

    /**
     * Change user password.
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // Validate password confirmation
        if (!request.passwordsMatch()) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Mật khẩu hiện tại không đúng");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Revoke all sessions except current (force re-login)
        sessionService.revokeAllUserSessions(userId);
        
        log.info("Password changed for user: {}", user.getUsername());
    }

    /**
     * Deactivate user account.
     */
    @Transactional
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        
        // Revoke all sessions
        sessionService.revokeAllUserSessions(userId);
        
        log.info("User deactivated: {}", user.getUsername());
    }

    /**
     * Ban user account.
     */
    @Transactional
    public void banUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
        
        // Revoke all sessions
        sessionService.revokeAllUserSessions(userId);
        
        log.info("User banned: {}", user.getUsername());
    }

    /**
     * Reactivate user account.
     */
    @Transactional
    public void reactivateUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        
        log.info("User reactivated: {}", user.getUsername());
    }

    /**
     * Count users by status.
     */
    public long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    /**
     * Count total users.
     */
    public long countTotalUsers() {
        return userRepository.count();
    }
}


package com.duong.lichvanien.user.service;

import com.duong.lichvanien.common.security.JwtTokenProvider;
import com.duong.lichvanien.user.dto.AuthResponse;
import com.duong.lichvanien.user.dto.UserResponse;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.entity.UserSessionEntity;
import com.duong.lichvanien.user.enums.DeviceType;
import com.duong.lichvanien.user.enums.Platform;
import com.duong.lichvanien.user.exception.SessionExpiredException;
import com.duong.lichvanien.user.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user sessions and JWT tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FingerprintService fingerprintService;

    /**
     * Create a new session for a user.
     */
    @Transactional
    public AuthResponse createSession(UserEntity user, String fingerprintId, HttpServletRequest request) {
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        // Get session details
        String ipAddress = fingerprintService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        DeviceType deviceType = detectDeviceType(userAgent);
        Platform platform = detectPlatform(userAgent, request);
        
        // Calculate expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpirationInSeconds());
        
        // Create session entity
        UserSessionEntity session = UserSessionEntity.builder()
                .user(user)
                .sessionToken(jwtTokenProvider.getTokenIdFromToken(accessToken))
                .refreshToken(refreshToken)
                .fingerprintId(fingerprintId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .platform(platform)
                .expiresAt(expiresAt)
                .isActive(true)
                .build();
        
        session = sessionRepository.save(session);
        
        log.info("Created session for user: {}, session ID: {}", user.getUsername(), session.getId());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .user(UserResponse.fromEntity(user))
                .fingerprintId(fingerprintId)
                .sessionId(session.getId())
                .build();
    }

    /**
     * Create an anonymous session.
     */
    @Transactional
    public AuthResponse createAnonymousSession(String fingerprintId, HttpServletRequest request) {
        // Generate anonymous token
        String accessToken = jwtTokenProvider.generateAnonymousToken(fingerprintId);
        
        // Get session details
        String ipAddress = fingerprintService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        DeviceType deviceType = detectDeviceType(userAgent);
        Platform platform = detectPlatform(userAgent, request);
        
        // Calculate expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getAccessTokenExpirationInSeconds());
        
        // Create session entity (no user)
        UserSessionEntity session = UserSessionEntity.builder()
                .user(null)
                .sessionToken(jwtTokenProvider.getTokenIdFromToken(accessToken))
                .refreshToken(null)
                .fingerprintId(fingerprintId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .platform(platform)
                .expiresAt(expiresAt)
                .isActive(true)
                .build();
        
        session = sessionRepository.save(session);
        
        log.info("Created anonymous session with fingerprint: {}, session ID: {}", fingerprintId, session.getId());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .fingerprintId(fingerprintId)
                .sessionId(session.getId())
                .build();
    }

    /**
     * Validate and get session by token.
     */
    public Optional<UserSessionEntity> validateSession(String token) {
        try {
            if (!jwtTokenProvider.isTokenValid(token)) {
                return Optional.empty();
            }
            
            String tokenId = jwtTokenProvider.getTokenIdFromToken(token);
            Optional<UserSessionEntity> session = sessionRepository.findBySessionTokenAndIsActiveTrue(tokenId);
            
            if (session.isPresent() && session.get().isValid()) {
                // Update last activity
                session.get().updateActivity();
                sessionRepository.save(session.get());
                return session;
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.debug("Session validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Refresh session using refresh token.
     */
    @Transactional
    public AuthResponse refreshSession(String refreshToken, HttpServletRequest request) {
        // Validate refresh token
        if (!jwtTokenProvider.isTokenValid(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new SessionExpiredException("Invalid refresh token");
        }
        
        // Find session by refresh token
        UserSessionEntity session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionExpiredException("Session not found"));
        
        if (!session.isValid()) {
            throw new SessionExpiredException("Session expired or revoked");
        }
        
        UserEntity user = session.getUser();
        if (user == null) {
            throw new SessionExpiredException("Cannot refresh anonymous session");
        }
        
        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        // Update session
        session.setSessionToken(jwtTokenProvider.getTokenIdFromToken(newAccessToken));
        session.setRefreshToken(newRefreshToken);
        session.setIpAddress(fingerprintService.getClientIpAddress(request));
        session.setExpiresAt(LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpirationInSeconds()));
        session.updateActivity();
        
        sessionRepository.save(session);
        
        log.info("Refreshed session for user: {}", user.getUsername());
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .user(UserResponse.fromEntity(user))
                .fingerprintId(session.getFingerprintId())
                .sessionId(session.getId())
                .build();
    }

    /**
     * Revoke a session by token.
     */
    @Transactional
    public void revokeSession(String token) {
        try {
            String tokenId = jwtTokenProvider.getTokenIdFromToken(token);
            int revoked = sessionRepository.revokeBySessionToken(tokenId);
            log.info("Revoked session: {}, count: {}", tokenId, revoked);
        } catch (Exception e) {
            log.debug("Failed to revoke session: {}", e.getMessage());
        }
    }

    /**
     * Revoke all sessions for a user.
     */
    @Transactional
    public void revokeAllUserSessions(Long userId) {
        int revoked = sessionRepository.revokeAllUserSessions(userId);
        log.info("Revoked all sessions for user: {}, count: {}", userId, revoked);
    }

    /**
     * Get active sessions for a user.
     */
    public List<UserSessionEntity> getActiveSessions(Long userId) {
        return sessionRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Get active sessions for a fingerprint.
     */
    public List<UserSessionEntity> getActiveSessionsByFingerprint(String fingerprintId) {
        return sessionRepository.findByFingerprintIdAndIsActiveTrue(fingerprintId);
    }

    /**
     * Count active sessions for a user.
     */
    public long countActiveSessions(Long userId) {
        return sessionRepository.countByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Clean up expired sessions.
     */
    @Transactional
    public int cleanupExpiredSessions() {
        // Delete sessions expired more than 30 days ago
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        int deleted = sessionRepository.deleteExpiredSessions(threshold);
        log.info("Cleaned up {} expired sessions", deleted);
        return deleted;
    }

    /**
     * Detect device type from User-Agent.
     */
    private DeviceType detectDeviceType(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return DeviceType.UNKNOWN;
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return DeviceType.MOBILE;
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return DeviceType.TABLET;
        } else {
            return DeviceType.DESKTOP;
        }
    }

    /**
     * Detect platform from User-Agent and request headers.
     */
    private Platform detectPlatform(String userAgent, HttpServletRequest request) {
        // Check custom header first
        String platformHeader = request.getHeader("X-Platform");
        if (platformHeader != null) {
            try {
                return Platform.valueOf(platformHeader.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Fall through to User-Agent detection
            }
        }
        
        if (userAgent == null || userAgent.isEmpty()) {
            return Platform.UNKNOWN;
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("android")) {
            return Platform.ANDROID;
        } else if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) {
            return Platform.IOS;
        } else {
            return Platform.WEB;
        }
    }
}


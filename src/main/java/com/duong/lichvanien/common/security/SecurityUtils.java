package com.duong.lichvanien.common.security;

import com.duong.lichvanien.user.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for security operations.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Get current authentication.
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Get current user principal.
     */
    public static Optional<UserPrincipal> getCurrentPrincipal() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast);
    }

    /**
     * Get current user ID.
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentPrincipal()
                .map(UserPrincipal::getId);
    }

    /**
     * Get current user UUID.
     */
    public static Optional<String> getCurrentUserUuid() {
        return getCurrentPrincipal()
                .map(UserPrincipal::getUuid);
    }

    /**
     * Get current fingerprint ID.
     */
    public static Optional<String> getCurrentFingerprintId() {
        return getCurrentPrincipal()
                .map(UserPrincipal::getFingerprintId);
    }

    /**
     * Get current token ID.
     */
    public static Optional<String> getCurrentTokenId() {
        return getCurrentPrincipal()
                .map(UserPrincipal::getTokenId);
    }

    /**
     * Check if current user is authenticated.
     */
    public static boolean isAuthenticated() {
        return getCurrentPrincipal()
                .map(UserPrincipal::isAuthenticated)
                .orElse(false);
    }

    /**
     * Check if current session is anonymous.
     */
    public static boolean isAnonymous() {
        return getCurrentPrincipal()
                .map(UserPrincipal::isAnonymous)
                .orElse(true);
    }

    /**
     * Check if current user is a registered user (not anonymous).
     */
    public static boolean isUser() {
        return getCurrentPrincipal()
                .map(UserPrincipal::isUser)
                .orElse(false);
    }

    /**
     * Check if current user is admin.
     */
    public static boolean isAdmin() {
        return getCurrentPrincipal()
                .map(UserPrincipal::isAdmin)
                .orElse(false);
    }

    /**
     * Check if current user has specific role.
     */
    public static boolean hasRole(UserRole role) {
        return getCurrentPrincipal()
                .map(p -> p.hasRole(role))
                .orElse(false);
    }

    /**
     * Check if current user has specific role by name.
     */
    public static boolean hasRole(String roleName) {
        return getCurrentPrincipal()
                .map(p -> p.hasRole(roleName))
                .orElse(false);
    }

    /**
     * Get current user role.
     */
    public static Optional<UserRole> getCurrentUserRole() {
        return getCurrentPrincipal()
                .map(UserPrincipal::getRole);
    }

    /**
     * Require current user to be admin, throws exception if not.
     */
    public static void requireAdmin() {
        if (!isAdmin()) {
            throw new SecurityException("Admin access required");
        }
    }

    /**
     * Require current user to have specific role, throws exception if not.
     */
    public static void requireRole(UserRole role) {
        if (!hasRole(role)) {
            throw new SecurityException("Required role: " + role.name());
        }
    }
}


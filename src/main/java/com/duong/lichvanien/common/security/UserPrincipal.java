package com.duong.lichvanien.common.security;

import com.duong.lichvanien.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Principal object representing the authenticated user/session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User ID (null for anonymous).
     */
    private Long id;

    /**
     * User UUID (null for anonymous).
     */
    private String uuid;

    /**
     * Username (email or phone).
     */
    private String username;

    /**
     * User email.
     */
    private String email;

    /**
     * User phone.
     */
    private String phone;

    /**
     * User role (USER, ADMIN).
     */
    private UserRole role;

    /**
     * Fingerprint ID.
     */
    private String fingerprintId;

    /**
     * JWT token.
     */
    private String token;

    /**
     * JWT token ID (jti).
     */
    private String tokenId;

    /**
     * Whether user is authenticated.
     */
    private boolean authenticated;

    /**
     * Whether this is an anonymous session.
     */
    private boolean anonymous;

    /**
     * Check if user is authenticated (not anonymous).
     */
    public boolean isUser() {
        return authenticated && !anonymous && id != null;
    }

    /**
     * Check if user is admin.
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Check if user has specific role.
     */
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    /**
     * Check if user has specific role by name.
     */
    public boolean hasRole(String roleName) {
        if (role == null || roleName == null) {
            return false;
        }
        return role.name().equalsIgnoreCase(roleName);
    }

    /**
     * Get display name.
     */
    public String getDisplayName() {
        if (username != null) {
            return username;
        }
        if (email != null) {
            return email;
        }
        if (phone != null) {
            return phone;
        }
        if (fingerprintId != null) {
            return "Anonymous-" + fingerprintId.substring(0, 8);
        }
        return "Anonymous";
    }
}


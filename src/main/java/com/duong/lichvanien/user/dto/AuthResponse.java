package com.duong.lichvanien.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication (login/register).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * JWT access token.
     */
    private String accessToken;

    /**
     * JWT refresh token for session renewal.
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiration time in seconds.
     */
    private Long expiresIn;

    /**
     * User information.
     */
    private UserResponse user;

    /**
     * Fingerprint ID assigned to this session.
     */
    private String fingerprintId;

    /**
     * Session ID.
     */
    private Long sessionId;
}


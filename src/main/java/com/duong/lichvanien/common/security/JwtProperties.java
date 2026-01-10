package com.duong.lichvanien.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     * Should be at least 256 bits (32 bytes) for HS256.
     */
    private String secret = "your-256-bit-secret-key-change-in-production-please";

    /**
     * Access token expiration time in milliseconds.
     * Default: 1 hour (3600000 ms)
     */
    private long accessTokenExpiration = 3600000;

    /**
     * Refresh token expiration time in milliseconds.
     * Default: 7 days (604800000 ms)
     */
    private long refreshTokenExpiration = 604800000;

    /**
     * Token issuer.
     */
    private String issuer = "lich-van-nien";

    /**
     * Token audience.
     */
    private String audience = "lich-van-nien-app";
}


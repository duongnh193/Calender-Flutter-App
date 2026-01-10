package com.duong.lichvanien.common.security;

import com.duong.lichvanien.user.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT token provider for generating and validating JWT tokens.
 * Uses JJWT 0.11.x API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Generate access token for a user.
     */
    public String generateAccessToken(UserEntity user) {
        return generateAccessToken(user.getId(), user.getUuid(), user.getUsername(), user.getRole().name());
    }

    /**
     * Generate access token with user details.
     */
    public String generateAccessToken(Long userId, String userUuid, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("uuid", userUuid)
                .claim("username", username)
                .claim("role", role)
                .claim("type", "access")
                .setId(UUID.randomUUID().toString())
                .setIssuer(jwtProperties.getIssuer())
                .setAudience(jwtProperties.getAudience())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate refresh token for a user.
     */
    public String generateRefreshToken(UserEntity user) {
        return generateRefreshToken(user.getId(), user.getUuid());
    }

    /**
     * Generate refresh token with user details.
     */
    public String generateRefreshToken(Long userId, String userUuid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("uuid", userUuid)
                .claim("type", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuer(jwtProperties.getIssuer())
                .setAudience(jwtProperties.getAudience())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate anonymous session token (no user).
     */
    public String generateAnonymousToken(String fingerprintId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .setSubject("anonymous")
                .claim("fingerprintId", fingerprintId)
                .claim("type", "anonymous")
                .setId(UUID.randomUUID().toString())
                .setIssuer(jwtProperties.getIssuer())
                .setAudience(jwtProperties.getAudience())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token and return claims.
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if token is valid without throwing exceptions.
     */
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get user ID from token.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        String subject = claims.getSubject();
        if ("anonymous".equals(subject)) {
            return null;
        }
        return Long.parseLong(subject);
    }

    /**
     * Get user UUID from token.
     */
    public String getUserUuidFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("uuid", String.class);
    }

    /**
     * Get username from token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Get token ID (jti) from token.
     */
    public String getTokenIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getId();
    }

    /**
     * Get fingerprint ID from token (for anonymous tokens).
     */
    public String getFingerprintIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("fingerprintId", String.class);
    }

    /**
     * Get role from token.
     */
    public String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Get token type from token.
     */
    public String getTokenType(String token) {
        Claims claims = validateToken(token);
        return claims.get("type", String.class);
    }

    /**
     * Check if token is access token.
     */
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    /**
     * Check if token is refresh token.
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Check if token is anonymous token.
     */
    public boolean isAnonymousToken(String token) {
        return "anonymous".equals(getTokenType(token));
    }

    /**
     * Get token expiration date.
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration();
    }

    /**
     * Get access token expiration in seconds.
     */
    public long getAccessTokenExpirationInSeconds() {
        return jwtProperties.getAccessTokenExpiration() / 1000;
    }

    /**
     * Get refresh token expiration in seconds.
     */
    public long getRefreshTokenExpirationInSeconds() {
        return jwtProperties.getRefreshTokenExpiration() / 1000;
    }

    /**
     * Get signing key from secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

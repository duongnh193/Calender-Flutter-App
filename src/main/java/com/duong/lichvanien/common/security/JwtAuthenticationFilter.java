package com.duong.lichvanien.common.security;

import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT authentication filter that processes JWT tokens from request headers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                processJwtToken(jwt, request);
            }
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired for request: {}", request.getRequestURI());
            // Don't block the request, just don't authenticate
        } catch (JwtException e) {
            log.debug("Invalid JWT token for request: {}", request.getRequestURI());
            // Don't block the request, just don't authenticate
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Process and validate JWT token.
     */
    private void processJwtToken(String jwt, HttpServletRequest request) {
        // Validate token
        if (!jwtTokenProvider.isTokenValid(jwt)) {
            return;
        }

        // Only process access tokens for authentication
        if (!jwtTokenProvider.isAccessToken(jwt)) {
            return;
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

        if (userId != null) {
            // Authenticated user
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                authenticateUser(user, jwt, request);
            }
        } else {
            // Anonymous token
            String fingerprintId = jwtTokenProvider.getFingerprintIdFromToken(jwt);
            if (fingerprintId != null) {
                authenticateAnonymous(fingerprintId, jwt, request);
            }
        }
    }

    /**
     * Set authentication for authenticated user.
     */
    private void authenticateUser(UserEntity user, String jwt, HttpServletRequest request) {
        // Build authorities based on user role
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Add ROLE_ADMIN if user is admin
        if (user.getRole() == UserRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        UserPrincipal principal = UserPrincipal.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .token(jwt)
                .tokenId(jwtTokenProvider.getTokenIdFromToken(jwt))
                .authenticated(true)
                .anonymous(false)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Authenticated user: {} with role: {}", user.getUsername(), user.getRole());
    }

    /**
     * Set authentication for anonymous session.
     */
    private void authenticateAnonymous(String fingerprintId, String jwt, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

        UserPrincipal principal = UserPrincipal.builder()
                .fingerprintId(fingerprintId)
                .role(null) // Anonymous has no role
                .token(jwt)
                .tokenId(jwtTokenProvider.getTokenIdFromToken(jwt))
                .authenticated(true)
                .anonymous(true)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Authenticated anonymous session with fingerprint: {}", fingerprintId);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for public endpoints
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/api/v1/user/register") ||
               path.equals("/api/v1/user/login") ||
               path.equals("/api/v1/user/fingerprint");
    }
}


package com.duong.lichvanien.user.interceptor;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.common.security.UserPrincipal;
import com.duong.lichvanien.user.service.AccessLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Interceptor that logs all API requests for auditing and analytics.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";
    
    private final AccessLogService accessLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Record start time
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        try {
            // Calculate response time
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            Integer responseTimeMs = null;
            if (startTime != null) {
                responseTimeMs = (int) (System.currentTimeMillis() - startTime);
            }
            
            // Get fingerprint ID
            String fingerprintId = FingerprintInterceptor.getFingerprintId(request);
            if (fingerprintId == null) {
                fingerprintId = "unknown";
            }
            
            // Get user info from security context
            Long userId = null;
            Long sessionId = null;
            Optional<UserPrincipal> principal = SecurityUtils.getCurrentPrincipal();
            if (principal.isPresent()) {
                userId = principal.get().getId();
                // Note: sessionId would need to be stored in principal or fetched separately
            }
            
            // Get request body (if cached)
            String requestBody = null;
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    requestBody = new String(content, StandardCharsets.UTF_8);
                    // Truncate large bodies
                    if (requestBody.length() > 10000) {
                        requestBody = requestBody.substring(0, 10000) + "...[truncated]";
                    }
                }
            }
            
            // Log access asynchronously
            accessLogService.logAccessAsync(
                    request,
                    request.getRequestURI(),
                    request.getMethod(),
                    requestBody,
                    userId,
                    sessionId,
                    fingerprintId,
                    response.getStatus(),
                    responseTimeMs
            );
            
        } catch (Exception e) {
            log.error("Error logging access: {}", e.getMessage());
        }
    }
}


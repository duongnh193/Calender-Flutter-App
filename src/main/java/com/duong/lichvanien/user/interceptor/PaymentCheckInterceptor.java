package com.duong.lichvanien.user.interceptor;

import com.duong.lichvanien.user.exception.PaymentRequiredException;
import com.duong.lichvanien.user.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that checks payment status before allowing access to paid content.
 * Intercepts specific endpoints and verifies payment based on fingerprint and content ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCheckInterceptor implements HandlerInterceptor {

    private final PaymentService paymentService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        
        // Check if this endpoint requires payment verification
        if (requiresPaymentCheck(path)) {
            String fingerprintId = FingerprintInterceptor.getFingerprintId(request);
            if (fingerprintId == null) {
                log.warn("No fingerprint ID found for payment check");
                throw new PaymentRequiredException("Không thể xác định thiết bị. Vui lòng thử lại.");
            }
            
            // Determine content type and ID based on endpoint
            ContentInfo contentInfo = extractContentInfo(request);
            
            if (contentInfo != null && contentInfo.contentType != null && contentInfo.contentId != null) {
                // Check payment
                if (!paymentService.hasActiveAccess(fingerprintId, contentInfo.contentType, contentInfo.contentId)) {
                    log.info("Payment required for fingerprint: {}, content: {}/{}", 
                            fingerprintId, contentInfo.contentType, contentInfo.contentId);
                    throw new PaymentRequiredException(contentInfo.contentType, contentInfo.contentId, fingerprintId);
                }
                
                log.debug("Payment verified for fingerprint: {}, content: {}/{}", 
                        fingerprintId, contentInfo.contentType, contentInfo.contentId);
            }
        }
        
        return true;
    }

    /**
     * Check if endpoint requires payment verification.
     */
    private boolean requiresPaymentCheck(String path) {
        // Tu Vi interpretation requires payment
        if (path.contains("/api/v1/tuvi/chart/interpretation")) {
            return true;
        }
        
        // Lifetime horoscope may require payment
        if (path.contains("/api/v1/horoscope/lifetime")) {
            return true;
        }
        
        return false;
    }

    /**
     * Extract content type and ID from request.
     */
    private ContentInfo extractContentInfo(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Tu Vi interpretation
        if (path.contains("/api/v1/tuvi/chart/interpretation")) {
            String chartHash = request.getParameter("chartHash");
            if (chartHash == null) {
                // Try to get from path or body
                // For POST requests, the chart hash might be in the body
                // This would need to be handled differently
                chartHash = extractChartHashFromRequest(request);
            }
            
            if (chartHash != null) {
                return new ContentInfo("TUVI_INTERPRETATION", chartHash);
            }
        }
        
        // Lifetime horoscope
        if (path.contains("/api/v1/horoscope/lifetime")) {
            String canChi = request.getParameter("canChi");
            if (canChi != null) {
                return new ContentInfo("HOROSCOPE_LIFETIME", canChi);
            }
        }
        
        return null;
    }

    /**
     * Try to extract chart hash from various sources.
     */
    private String extractChartHashFromRequest(HttpServletRequest request) {
        // Check query parameter
        String chartHash = request.getParameter("chartHash");
        if (chartHash != null) {
            return chartHash;
        }
        
        // Check header
        chartHash = request.getHeader("X-Chart-Hash");
        if (chartHash != null) {
            return chartHash;
        }
        
        // For POST requests with JSON body, the chart hash would need to be
        // extracted from the cached request body. This is complex and would
        // typically be handled in the controller instead.
        
        return null;
    }

    /**
     * Simple class to hold content type and ID.
     */
    private static class ContentInfo {
        final String contentType;
        final String contentId;
        
        ContentInfo(String contentType, String contentId) {
            this.contentType = contentType;
            this.contentId = contentId;
        }
    }
}


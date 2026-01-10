package com.duong.lichvanien.common.exception;

import com.duong.lichvanien.common.response.ErrorResponse;
import com.duong.lichvanien.tuvi.exception.TuViInterpretationNotFoundException;
import com.duong.lichvanien.user.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(ex.getStatus())
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        final String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(400)
                        .code("VALIDATION_ERROR")
                        .message(message)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(TuViInterpretationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTuViInterpretationNotFound(TuViInterpretationNotFoundException ex, HttpServletRequest request) {
        log.warn("Tu Vi interpretation not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(404)
                        .code("NOT_FOUND")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ==================== User Management Exceptions ====================

    @ExceptionHandler(PaymentRequiredException.class)
    public ResponseEntity<ErrorResponse> handlePaymentRequired(PaymentRequiredException ex, HttpServletRequest request) {
        log.info("Payment required: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(402)
                        .code("PAYMENT_REQUIRED")
                        .message("Nội dung này yêu cầu thanh toán")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(401)
                        .code("INVALID_CREDENTIALS")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(409)
                        .code("USER_EXISTS")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpired(SessionExpiredException ex, HttpServletRequest request) {
        log.debug("Session expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(401)
                        .code("SESSION_EXPIRED")
                        .message("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(FingerprintException.class)
    public ResponseEntity<ErrorResponse> handleFingerprintError(FingerprintException ex, HttpServletRequest request) {
        log.warn("Fingerprint error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(400)
                        .code("FINGERPRINT_ERROR")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ==================== JWT Exceptions ====================

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest request) {
        log.debug("JWT expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(401)
                        .code("TOKEN_EXPIRED")
                        .message("Token đã hết hạn. Vui lòng làm mới token hoặc đăng nhập lại.")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtError(JwtException ex, HttpServletRequest request) {
        log.warn("JWT error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(401)
                        .code("INVALID_TOKEN")
                        .message("Token không hợp lệ")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ==================== Security Exceptions ====================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(401)
                        .code("AUTHENTICATION_ERROR")
                        .message("Xác thực thất bại")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(403)
                        .code("ACCESS_DENIED")
                        .message("Bạn không có quyền truy cập tài nguyên này")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ==================== Generic Exceptions ====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(400)
                        .code("BAD_REQUEST")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(400)
                        .code("BAD_REQUEST")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .status(500)
                        .code("INTERNAL_ERROR")
                        .message("Internal server error")
                        .path(request.getRequestURI())
                        .build()
        );
    }
}

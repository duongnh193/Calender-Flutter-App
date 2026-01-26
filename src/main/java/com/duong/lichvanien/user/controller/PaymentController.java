package com.duong.lichvanien.user.controller;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.user.dto.PaymentCheckResponse;
import com.duong.lichvanien.user.dto.PaymentRequest;
import com.duong.lichvanien.user.dto.PaymentResponse;
import com.duong.lichvanien.user.entity.ContentAccessEntity;
import com.duong.lichvanien.user.entity.PaymentTransactionEntity;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import com.duong.lichvanien.user.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for payment management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @Operation(summary = "Create payment", description = "Create a new payment transaction. Requires registered user.")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        
        // Block anonymous users
        if (SecurityUtils.isAnonymous()) {
            log.warn("Anonymous user attempted to create payment");
            throw new IllegalArgumentException(
                    "Vui lòng đăng ký tài khoản để thanh toán và xem giải luận đầy đủ."
            );
        }
        
        // Require authenticated user
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        if (fingerprintId == null) {
            throw new IllegalStateException("Fingerprint ID not found");
        }
        
        Long sessionId = null; // Would need to be extracted from token if needed
        
        PaymentResponse response = paymentService.createTransaction(
                request, fingerprintId, userId, sessionId, httpRequest);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{contentType}/{contentId}")
    @Operation(summary = "Check payment status", 
               description = "Check if payment has been made for specific content")
    public ResponseEntity<PaymentCheckResponse> checkPayment(
            @PathVariable String contentType,
            @PathVariable String contentId,
            HttpServletRequest httpRequest) {
        
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        if (fingerprintId == null) {
            throw new IllegalStateException("Fingerprint ID not found");
        }
        
        PaymentCheckResponse response = paymentService.checkPaymentEligibility(
                fingerprintId, contentType, contentId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/tuvi/{chartHash}")
    @Operation(summary = "Check Tu Vi payment", 
               description = "Check if payment has been made for a Tu Vi chart interpretation")
    public ResponseEntity<PaymentCheckResponse> checkTuViPayment(
            @PathVariable String chartHash,
            HttpServletRequest httpRequest) {
        
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        if (fingerprintId == null) {
            throw new IllegalStateException("Fingerprint ID not found");
        }
        
        PaymentCheckResponse response = paymentService.checkPaymentEligibility(
                fingerprintId, "TUVI_INTERPRETATION", chartHash);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    @Operation(summary = "Payment callback", 
               description = "Callback endpoint for payment gateway to confirm payment")
    public ResponseEntity<PaymentResponse> paymentCallback(
            @RequestBody Map<String, String> callbackData) {
        
        String transactionId = callbackData.get("transactionId");
        String gatewayTransactionId = callbackData.get("gatewayTransactionId");
        String status = callbackData.get("status");
        
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        
        // In a real implementation, you would verify the callback signature
        // and handle different payment statuses
        
        if ("success".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
            PaymentResponse response = paymentService.completePayment(
                    transactionId, gatewayTransactionId, callbackData.toString());
            return ResponseEntity.ok(response);
        }
        
        // Handle other statuses
        return ResponseEntity.ok(PaymentResponse.builder()
                .transactionId(transactionId)
                .message("Payment status received: " + status)
                .build());
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get transaction", description = "Get payment transaction details")
    public ResponseEntity<PaymentResponse> getTransaction(@PathVariable String transactionId) {
        PaymentTransactionEntity transaction = paymentService.getTransaction(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        PaymentResponse response = PaymentResponse.builder()
                .transactionId(transaction.getTransactionUuid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentMethod(transaction.getPaymentMethod())
                .paymentStatus(transaction.getPaymentStatus())
                .chartHash(transaction.getChartHash())
                .createdAt(transaction.getCreatedAt())
                .expiresAt(transaction.getExpiresAt())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get payment history", 
               description = "Get payment history for the authenticated user. " +
                           "Note: This endpoint returns payment_transaction records only. " +
                           "For Xu transactions (nạp xu), use /api/v1/xu/transactions endpoint.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<PaymentTransactionEntity>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        log.info("Getting payment history for user {} (page={}, size={})", userId, page, size);
        
        Page<PaymentTransactionEntity> transactions = paymentService.getUserTransactions(userId, page, size);
        
        log.info("Found {} payment transactions for user {} (total: {})", 
                transactions.getNumberOfElements(), userId, transactions.getTotalElements());
        
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/access")
    @Operation(summary = "Get content access", 
               description = "Get all content access records for current fingerprint")
    public ResponseEntity<List<ContentAccessEntity>> getContentAccess(HttpServletRequest httpRequest) {
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        if (fingerprintId == null) {
            throw new IllegalStateException("Fingerprint ID not found");
        }
        
        List<ContentAccessEntity> access = paymentService.getFingerprintAccess(fingerprintId);
        return ResponseEntity.ok(access);
    }

    @GetMapping("/access/user")
    @Operation(summary = "Get user content access", 
               description = "Get all content access records for authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ContentAccessEntity>> getUserContentAccess() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        List<ContentAccessEntity> access = paymentService.getUserAccess(userId);
        return ResponseEntity.ok(access);
    }

    // ==================== Admin Endpoints ====================

    @PostMapping("/admin/complete/{transactionId}")
    @Operation(summary = "Admin complete payment", 
               description = "Manually complete a payment (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaymentResponse> adminCompletePayment(
            @PathVariable String transactionId,
            @RequestParam(required = false) String note) {
        // TODO: Add admin role check
        PaymentResponse response = paymentService.completePayment(transactionId, "ADMIN_" + note, "Admin completion");
        return ResponseEntity.ok(response);
    }
}


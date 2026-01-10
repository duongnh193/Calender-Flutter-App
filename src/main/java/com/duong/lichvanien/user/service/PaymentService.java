package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.dto.PaymentCheckResponse;
import com.duong.lichvanien.user.dto.PaymentRequest;
import com.duong.lichvanien.user.dto.PaymentResponse;
import com.duong.lichvanien.user.entity.ContentAccessEntity;
import com.duong.lichvanien.user.entity.PaymentTransactionEntity;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.PaymentStatus;
import com.duong.lichvanien.user.enums.TransactionType;
import com.duong.lichvanien.user.exception.PaymentRequiredException;
import com.duong.lichvanien.user.repository.ContentAccessRepository;
import com.duong.lichvanien.user.repository.PaymentTransactionRepository;
import com.duong.lichvanien.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for payment management and anti-cheating.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository transactionRepository;
    private final ContentAccessRepository contentAccessRepository;
    private final UserRepository userRepository;
    private final FingerprintService fingerprintService;

    /**
     * Create a payment transaction.
     */
    @Transactional
    public PaymentResponse createTransaction(PaymentRequest request, 
                                              String fingerprintId,
                                              Long userId,
                                              Long sessionId,
                                              HttpServletRequest httpRequest) {
        // Get user if authenticated
        UserEntity user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }
        
        String ipAddress = fingerprintService.getClientIpAddress(httpRequest);
        
        // Determine content type and ID
        String contentType = request.getContentType();
        String contentId = request.getContentId();
        
        if (request.getTransactionType() == TransactionType.TUVI_INTERPRETATION && request.getChartHash() != null) {
            contentType = "TUVI_INTERPRETATION";
            contentId = request.getChartHash();
        }
        
        // Check if already paid
        if (contentType != null && contentId != null) {
            if (hasActiveAccess(fingerprintId, contentType, contentId)) {
                return PaymentResponse.builder()
                        .paymentStatus(PaymentStatus.COMPLETED)
                        .message("Bạn đã thanh toán cho nội dung này")
                        .build();
            }
        }
        
        // Create transaction
        PaymentTransactionEntity transaction = PaymentTransactionEntity.builder()
                .user(user)
                .fingerprintId(fingerprintId)
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .chartHash(request.getChartHash())
                .contentType(contentType)
                .contentId(contentId)
                .metadata(request.getMetadata())
                .ipAddress(ipAddress)
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Payment expires in 30 minutes
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        log.info("Created payment transaction: {}", transaction.getTransactionUuid());
        
        // In a real implementation, you would integrate with payment gateway here
        // and return payment URL, QR code, etc.
        
        return PaymentResponse.builder()
                .transactionId(transaction.getTransactionUuid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentMethod(transaction.getPaymentMethod())
                .paymentStatus(transaction.getPaymentStatus())
                .chartHash(transaction.getChartHash())
                .createdAt(transaction.getCreatedAt())
                .expiresAt(transaction.getExpiresAt())
                .message("Giao dịch đã được tạo. Vui lòng thanh toán.")
                // paymentUrl, qrCode, deepLink would be set by payment gateway integration
                .build();
    }

    /**
     * Complete a payment (called by payment gateway callback or manual confirmation).
     */
    @Transactional
    public PaymentResponse completePayment(String transactionUuid, String gatewayTransactionId, String gatewayResponse) {
        PaymentTransactionEntity transaction = transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Giao dịch không tồn tại"));
        
        if (transaction.isCompleted()) {
            return PaymentResponse.builder()
                    .transactionId(transaction.getTransactionUuid())
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .message("Giao dịch đã được hoàn thành trước đó")
                    .build();
        }
        
        // Update transaction
        transaction.setPaymentGatewayTransactionId(gatewayTransactionId);
        transaction.setPaymentGatewayResponse(gatewayResponse);
        transaction.complete();
        
        transactionRepository.save(transaction);
        
        // Grant content access
        if (transaction.getContentType() != null && transaction.getContentId() != null) {
            grantContentAccess(
                    transaction.getFingerprintId(),
                    transaction.getContentType(),
                    transaction.getContentId(),
                    transaction.getUser(),
                    transaction
            );
        }
        
        log.info("Payment completed: {}", transactionUuid);
        
        return PaymentResponse.builder()
                .transactionId(transaction.getTransactionUuid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentStatus(PaymentStatus.COMPLETED)
                .chartHash(transaction.getChartHash())
                .createdAt(transaction.getCreatedAt())
                .message("Thanh toán thành công")
                .build();
    }

    /**
     * Check if fingerprint has access to content.
     */
    public boolean hasActiveAccess(String fingerprintId, String contentType, String contentId) {
        return contentAccessRepository.hasActiveAccess(fingerprintId, contentType, contentId, LocalDateTime.now());
    }

    /**
     * Check payment eligibility and return status.
     */
    public PaymentCheckResponse checkPaymentEligibility(String fingerprintId, String contentType, String contentId) {
        // Check for active access
        Optional<ContentAccessEntity> access = contentAccessRepository
                .findByFingerprintIdAndContentTypeAndContentId(fingerprintId, contentType, contentId);
        
        if (access.isPresent() && access.get().isAccessValid()) {
            ContentAccessEntity contentAccess = access.get();
            return PaymentCheckResponse.builder()
                    .isPaid(true)
                    .hasAccess(true)
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .accessGrantedAt(contentAccess.getAccessGrantedAt())
                    .accessExpiresAt(contentAccess.getAccessExpiresAt())
                    .contentType(contentType)
                    .contentId(contentId)
                    .message("Bạn đã có quyền truy cập nội dung này")
                    .build();
        }
        
        return PaymentCheckResponse.unpaid(contentType, contentId);
    }

    /**
     * Verify payment before granting access.
     * Throws PaymentRequiredException if not paid.
     */
    public void verifyPaymentOrThrow(String fingerprintId, String contentType, String contentId) {
        if (!hasActiveAccess(fingerprintId, contentType, contentId)) {
            throw new PaymentRequiredException(contentType, contentId, fingerprintId);
        }
        
        // Record access
        contentAccessRepository.incrementAccessCount(fingerprintId, contentType, contentId, LocalDateTime.now());
    }

    /**
     * Grant content access after payment.
     */
    @Transactional
    public void grantContentAccess(String fingerprintId, 
                                    String contentType, 
                                    String contentId,
                                    UserEntity user,
                                    PaymentTransactionEntity transaction) {
        // Check if access already exists
        Optional<ContentAccessEntity> existing = contentAccessRepository
                .findByFingerprintIdAndContentTypeAndContentId(fingerprintId, contentType, contentId);
        
        if (existing.isPresent()) {
            // Reactivate if inactive
            ContentAccessEntity access = existing.get();
            access.setIsActive(true);
            access.setPaymentTransaction(transaction);
            if (user != null) {
                access.setUser(user);
            }
            contentAccessRepository.save(access);
            log.info("Reactivated content access for fingerprint: {}, content: {}/{}", fingerprintId, contentType, contentId);
            return;
        }
        
        // Create new access
        ContentAccessEntity access = ContentAccessEntity.builder()
                .user(user)
                .fingerprintId(fingerprintId)
                .contentType(contentType)
                .contentId(contentId)
                .paymentTransaction(transaction)
                .accessExpiresAt(null) // Permanent access
                .isActive(true)
                .build();
        
        contentAccessRepository.save(access);
        log.info("Granted content access for fingerprint: {}, content: {}/{}", fingerprintId, contentType, contentId);
    }

    /**
     * Get transaction by UUID.
     */
    public Optional<PaymentTransactionEntity> getTransaction(String transactionUuid) {
        return transactionRepository.findByTransactionUuid(transactionUuid);
    }

    /**
     * Get transactions for a user.
     */
    public Page<PaymentTransactionEntity> getUserTransactions(Long userId, int page, int size) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    /**
     * Get transactions for a fingerprint.
     */
    public List<PaymentTransactionEntity> getFingerprintTransactions(String fingerprintId) {
        return transactionRepository.findByFingerprintIdOrderByCreatedAtDesc(fingerprintId);
    }

    /**
     * Get content access records for a fingerprint.
     */
    public List<ContentAccessEntity> getFingerprintAccess(String fingerprintId) {
        return contentAccessRepository.findByFingerprintIdAndIsActiveTrue(fingerprintId);
    }

    /**
     * Get content access records for a user.
     */
    public List<ContentAccessEntity> getUserAccess(Long userId) {
        return contentAccessRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Get total revenue in date range.
     */
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.sumAmountByStatusAndDateRange(PaymentStatus.COMPLETED, start, end);
    }

    /**
     * Revoke expired access.
     */
    @Transactional
    public int revokeExpiredAccess() {
        int revoked = contentAccessRepository.revokeExpiredAccess(LocalDateTime.now());
        log.info("Revoked {} expired content access records", revoked);
        return revoked;
    }
}


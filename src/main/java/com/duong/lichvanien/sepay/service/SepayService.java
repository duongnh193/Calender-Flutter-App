package com.duong.lichvanien.sepay.service;

import com.duong.lichvanien.sepay.config.SepayProperties;
import com.duong.lichvanien.sepay.entity.SepayTransactionEntity;
import com.duong.lichvanien.sepay.enums.SepayTransactionStatus;
import com.duong.lichvanien.sepay.repository.SepayTransactionRepository;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.repository.UserRepository;
import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.xu.entity.XuPackageEntity;
import com.duong.lichvanien.xu.repository.XuPackageRepository;
import com.duong.lichvanien.xu.service.XuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for SePay integration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SepayService {

    private final SepayTransactionRepository sepayTransactionRepository;
    private final SepayProperties sepayProperties;
    private final XuPackageRepository xuPackageRepository;
    private final UserRepository userRepository;
    private final XuService xuService;
    private final AffiliateService affiliateService;

    /**
     * Create a SePay transaction for xu deposit.
     * Returns transfer content and QR code info.
     */
    @Transactional
    public SepayDepositResponse createDepositTransaction(Long userId, Long packageId) {
        // Get xu package
        XuPackageEntity xuPackage = xuPackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Xu package not found: " + packageId));

        if (!xuPackage.getIsActive()) {
            throw new IllegalArgumentException("Xu package is not active");
        }

        // Get user (optional, can be null for anonymous)
        UserEntity user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        // Generate unique content for matching
        String content = generateTransferContent();

        // Create SePay transaction
        SepayTransactionEntity transaction = SepayTransactionEntity.builder()
                .user(user)
                .content(content)
                .amountVnd(xuPackage.getPriceVnd())
                .xuPackage(xuPackage)
                .status(SepayTransactionStatus.PENDING)
                .build();

        transaction = sepayTransactionRepository.save(transaction);

        log.info("Created SePay transaction: {} for user: {}, package: {}, amount: {} VND",
                transaction.getId(), userId, packageId, xuPackage.getPriceVnd());

        // Build response with QR code info
        return SepayDepositResponse.builder()
                .transactionId(transaction.getId().toString())
                .content(content)
                .amountVnd(xuPackage.getPriceVnd())
                .xuAmount(xuPackage.getTotalXu())
                .bankAccount(sepayProperties.getBankAccount())
                .bankName(sepayProperties.getBankName())
                .accountName(sepayProperties.getAccountName())
                .qrCodeData(generateQrCodeData(content, xuPackage.getPriceVnd()))
                .build();
    }

    /**
     * Process SePay webhook callback.
     */
    @Transactional
    public void processWebhook(SepayWebhookRequest webhookRequest) {
        log.info("=== Processing SePay Webhook ===");
        log.info("Webhook request: {}", webhookRequest);

        // Null checks
        if (webhookRequest == null) {
            log.error("Webhook request is null");
            throw new IllegalArgumentException("Webhook request cannot be null");
        }
        
        String content = webhookRequest.getContent();
        if (content == null || content.isBlank()) {
            log.error("Webhook content is null or empty");
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        
        BigDecimal amount = webhookRequest.getAmount();
        if (amount == null) {
            log.error("Webhook amount is null");
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        log.info("Webhook data - transactionId: {}, content: {}, amount: {}", 
                webhookRequest.getTransactionId(), content, amount);

        // Verify webhook signature (if SePay provides)
        // if (!verifySignature(webhookRequest)) {
        //     throw new SecurityException("Invalid webhook signature");
        // }

        // Find transaction by content
        Optional<SepayTransactionEntity> transactionOpt = sepayTransactionRepository
                .findByContentAndStatus(content, SepayTransactionStatus.PENDING);

        if (transactionOpt.isEmpty()) {
            log.warn("SePay transaction not found for content: {}", content);
            log.warn("This might be a test webhook or transaction was already processed");
            return;
        }

        SepayTransactionEntity transaction = transactionOpt.get();
        log.info("Found transaction: id={}, status={}, amount={}, user={}", 
                transaction.getId(), transaction.getStatus(), 
                transaction.getAmountVnd(), 
                transaction.getUser() != null ? transaction.getUser().getId() : "anonymous");

        // Check if already processed
        if (transaction.getStatus() != SepayTransactionStatus.PENDING) {
            log.warn("SePay transaction already processed: id={}, status={}", 
                    transaction.getId(), transaction.getStatus());
            return;
        }

        // Verify amount matches
        if (amount.compareTo(transaction.getAmountVnd()) != 0) {
            log.error("Amount mismatch for transaction {}: expected {}, got {}",
                    transaction.getId(), transaction.getAmountVnd(), amount);
            transaction.markAsFailed();
            sepayTransactionRepository.save(transaction);
            throw new IllegalArgumentException(
                    String.format("Amount mismatch: expected %s, got %s", 
                            transaction.getAmountVnd(), amount));
        }

        // Mark transaction as completed
        transaction.setSepayTransactionId(webhookRequest.getTransactionId());
        transaction.setRawResponse(webhookRequest.getRawResponse());
        transaction.markAsCompleted();

        // Calculate xu to credit
        XuPackageEntity xuPackage = transaction.getXuPackage();
        Integer xuToCredit = xuPackage != null ? xuPackage.getTotalXu() : calculateXuFromVnd(transaction.getAmountVnd());
        transaction.setXuCredited(xuToCredit);

        sepayTransactionRepository.save(transaction);

        // Credit xu to user account
        if (transaction.getUser() != null) {
            Long userId = transaction.getUser().getId();
            boolean isFirstPayment = isFirstPayment(userId);
            
            xuService.addXu(
                    userId,
                    xuToCredit,
                    com.duong.lichvanien.xu.enums.XuTransactionType.DEPOSIT,
                    transaction.getId().toString(),
                    "Nạp xu qua SePay - Gói: " + (xuPackage != null ? xuPackage.getName() : "Tùy chỉnh"),
                    transaction.getAmountVnd()
            );
            
            // Process referral conversion if this is first payment
            if (isFirstPayment) {
                try {
                    affiliateService.processReferralConversion(userId, transaction.getAmountVnd());
                    log.info("Processed referral conversion for user: {}", userId);
                } catch (Exception e) {
                    log.warn("Failed to process referral conversion: {}", e.getMessage());
                }
            }
        }

        log.info("SePay transaction completed: {}, credited {} xu to user: {}",
                transaction.getId(), xuToCredit, transaction.getUser() != null ? transaction.getUser().getId() : "anonymous");
    }

    /**
     * Get SePay transaction by ID.
     */
    @Transactional(readOnly = true)
    public Optional<SepayTransactionResponse> getTransaction(Long transactionId) {
        return sepayTransactionRepository.findByIdWithRelations(transactionId)
                .map(this::toResponse);
    }
    
    /**
     * Convert entity to response DTO.
     */
    private SepayTransactionResponse toResponse(SepayTransactionEntity entity) {
        SepayTransactionResponse.SepayTransactionResponseBuilder builder = SepayTransactionResponse.builder()
                .id(entity.getId())
                .sepayTransactionId(entity.getSepayTransactionId())
                .content(entity.getContent())
                .amountVnd(entity.getAmountVnd())
                .xuCredited(entity.getXuCredited())
                .status(entity.getStatus())
                .rawResponse(entity.getRawResponse())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt());
        
        // Safely access user (lazy loading within transaction)
        if (entity.getUser() != null) {
            builder.userId(entity.getUser().getId())
                   .userEmail(entity.getUser().getEmail())
                   .userUsername(entity.getUser().getUsername());
        }
        
        // Safely access xu package (lazy loading within transaction)
        if (entity.getXuPackage() != null) {
            builder.xuPackageId(entity.getXuPackage().getId())
                   .xuPackageName(entity.getXuPackage().getName())
                   .xuPackagePriceVnd(entity.getXuPackage().getPriceVnd())
                   .xuPackageTotalXu(entity.getXuPackage().getTotalXu());
        }
        
        return builder.build();
    }

    /**
     * Get SePay transaction by SePay transaction ID.
     */
    @Transactional(readOnly = true)
    public Optional<SepayTransactionEntity> getTransactionBySepayId(String sepayTransactionId) {
        return sepayTransactionRepository.findBySepayTransactionId(sepayTransactionId);
    }

    /**
     * Generate unique transfer content.
     */
    private String generateTransferContent() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return sepayProperties.getContentPrefix() + uuid;
    }

    /**
     * Generate QR code data (VietQR format).
     */
    private String generateQrCodeData(String content, BigDecimal amount) {
        // VietQR format: bank_account|amount|content
        return String.format("%s|%.0f|%s",
                sepayProperties.getBankAccount(),
                amount.doubleValue(),
                content);
    }

    /**
     * Calculate xu from VND (using default rate 1 xu = 1000 VND).
     */
    private Integer calculateXuFromVnd(BigDecimal vndAmount) {
        // Default: 1 xu = 1000 VND
        return vndAmount.divide(new BigDecimal("1000"), 0, java.math.RoundingMode.DOWN).intValue();
    }

    /**
     * Check if this is user's first payment.
     */
    private boolean isFirstPayment(Long userId) {
        // Count completed transactions for this user
        long completedCount = sepayTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId, 
                org.springframework.data.domain.PageRequest.of(0, 100))
                .getContent()
                .stream()
                .filter(t -> t.getStatus() == SepayTransactionStatus.COMPLETED)
                .count();
        
        // This is first payment if count is 0 (before adding this transaction)
        return completedCount == 0;
    }

    /**
     * Response DTO for deposit transaction.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SepayDepositResponse {
        private String transactionId;
        private String content;
        private BigDecimal amountVnd;
        private Integer xuAmount;
        private String bankAccount;
        private String bankName;
        private String accountName;
        private String qrCodeData;
    }

    /**
     * Webhook request DTO.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class SepayWebhookRequest {
        @com.fasterxml.jackson.annotation.JsonProperty("transactionId")
        private String transactionId;
        
        @com.fasterxml.jackson.annotation.JsonProperty("content")
        private String content;
        
        @com.fasterxml.jackson.annotation.JsonProperty("amount")
        private BigDecimal amount;
        
        @com.fasterxml.jackson.annotation.JsonProperty("rawResponse")
        private String rawResponse;
    }
    
    /**
     * Response DTO for SePay transaction.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SepayTransactionResponse {
        private Long id;
        private Long userId;
        private String userEmail;
        private String userUsername;
        private String sepayTransactionId;
        private String content;
        private BigDecimal amountVnd;
        private Integer xuCredited;
        private Long xuPackageId;
        private String xuPackageName;
        private BigDecimal xuPackagePriceVnd;
        private Integer xuPackageTotalXu;
        private SepayTransactionStatus status;
        private String rawResponse;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        private java.time.LocalDateTime completedAt;
    }
}


package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.PaymentStatus;
import com.duong.lichvanien.user.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for payment transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    /**
     * Transaction UUID for external reference.
     */
    private String transactionId;

    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentStatus paymentStatus;

    /**
     * Payment URL to redirect user (for gateway payments).
     */
    private String paymentUrl;

    /**
     * QR code data (for QR payments).
     */
    private String qrCode;

    /**
     * Deep link for mobile app payments.
     */
    private String deepLink;

    /**
     * Chart hash associated with this payment.
     */
    private String chartHash;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    /**
     * Message to display to user.
     */
    private String message;
}


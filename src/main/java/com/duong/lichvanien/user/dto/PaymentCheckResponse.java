package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for payment status check.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCheckResponse {

    /**
     * Whether the content has been paid for.
     */
    private boolean isPaid;

    /**
     * Whether access is currently valid (paid and not expired).
     */
    private boolean hasAccess;

    /**
     * Payment status if a transaction exists.
     */
    private PaymentStatus paymentStatus;

    /**
     * Transaction ID if payment exists.
     */
    private String transactionId;

    /**
     * When access was granted.
     */
    private LocalDateTime accessGrantedAt;

    /**
     * When access expires (null if permanent).
     */
    private LocalDateTime accessExpiresAt;

    /**
     * Content type being checked.
     */
    private String contentType;

    /**
     * Content ID being checked.
     */
    private String contentId;

    /**
     * Message to display.
     */
    private String message;

    /**
     * Create response for paid content.
     */
    public static PaymentCheckResponse paid(String transactionId, LocalDateTime accessGrantedAt) {
        return PaymentCheckResponse.builder()
                .isPaid(true)
                .hasAccess(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .accessGrantedAt(accessGrantedAt)
                .message("Bạn đã thanh toán cho nội dung này")
                .build();
    }

    /**
     * Create response for unpaid content.
     */
    public static PaymentCheckResponse unpaid(String contentType, String contentId) {
        return PaymentCheckResponse.builder()
                .isPaid(false)
                .hasAccess(false)
                .contentType(contentType)
                .contentId(contentId)
                .message("Nội dung này yêu cầu thanh toán")
                .build();
    }
}


package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a payment transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Loại giao dịch không được để trống")
    private TransactionType transactionType;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "VND";

    /**
     * Payment method (momo, zalopay, vnpay, bank_transfer, etc.).
     */
    private String paymentMethod;

    /**
     * Tu Vi chart hash (for TUVI_INTERPRETATION payments).
     */
    private String chartHash;

    /**
     * Content type being purchased.
     */
    private String contentType;

    /**
     * Content ID being purchased.
     */
    private String contentId;

    /**
     * Return URL after payment completion.
     */
    private String returnUrl;

    /**
     * Cancel URL if payment is cancelled.
     */
    private String cancelUrl;

    /**
     * Additional metadata.
     */
    private String metadata;
}


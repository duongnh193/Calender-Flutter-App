package com.duong.lichvanien.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when payment is required to access content.
 * Returns HTTP 402 Payment Required.
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class PaymentRequiredException extends RuntimeException {

    private final String contentType;
    private final String contentId;
    private final String fingerprintId;

    public PaymentRequiredException(String message) {
        super(message);
        this.contentType = null;
        this.contentId = null;
        this.fingerprintId = null;
    }

    public PaymentRequiredException(String contentType, String contentId) {
        super(String.format("Payment required for %s: %s", contentType, contentId));
        this.contentType = contentType;
        this.contentId = contentId;
        this.fingerprintId = null;
    }

    public PaymentRequiredException(String contentType, String contentId, String fingerprintId) {
        super(String.format("Payment required for %s: %s (fingerprint: %s)", contentType, contentId, fingerprintId));
        this.contentType = contentType;
        this.contentId = contentId;
        this.fingerprintId = fingerprintId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentId() {
        return contentId;
    }

    public String getFingerprintId() {
        return fingerprintId;
    }
}


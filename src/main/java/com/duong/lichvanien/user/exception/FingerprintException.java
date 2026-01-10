package com.duong.lichvanien.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown for fingerprint-related errors.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FingerprintException extends RuntimeException {

    private final String fingerprintId;

    public FingerprintException(String message) {
        super(message);
        this.fingerprintId = null;
    }

    public FingerprintException(String message, String fingerprintId) {
        super(message);
        this.fingerprintId = fingerprintId;
    }

    public FingerprintException(String message, Throwable cause) {
        super(message, cause);
        this.fingerprintId = null;
    }

    public String getFingerprintId() {
        return fingerprintId;
    }
}


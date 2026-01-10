package com.duong.lichvanien.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a session has expired.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionExpiredException extends RuntimeException {

    private final String sessionToken;

    public SessionExpiredException(String message) {
        super(message);
        this.sessionToken = null;
    }

    public SessionExpiredException(String message, String sessionToken) {
        super(message);
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}


package com.duong.lichvanien.common.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String code, String message) {
        super(code, message, 404);
    }
}

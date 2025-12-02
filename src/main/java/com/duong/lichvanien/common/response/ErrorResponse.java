package com.duong.lichvanien.common.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String code;
    private String message;
    private String path;
}

package com.duong.lichvanien.xu.dto;

import com.duong.lichvanien.xu.enums.XuTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for xu transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuTransactionResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userUsername;
    private XuTransactionType transactionType;
    private Integer xuAmount;
    private BigDecimal vndAmount;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
    private boolean isCredit;
    private boolean isDebit;
}


package com.duong.lichvanien.xu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for xu balance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuBalanceResponse {
    private Integer xuBalance;
    private Integer totalXuEarned;
    private Integer totalXuSpent;
}


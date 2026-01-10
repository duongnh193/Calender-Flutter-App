package com.duong.lichvanien.affiliate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for affiliate status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateStatusResponse {
    private Boolean isMember;
    private String referralCode; // Mã giới thiệu 
    private Integer totalReferrals;
    private Integer totalCommissionXu;
    private Integer pendingCommissionXu;
    private Integer withdrawnCommissionXu;
    private Integer minXuToJoin;
    private Integer currentXuBalance;
    
    // Deprecated: backward compatibility
    @Deprecated
    private String affiliateCode;
}


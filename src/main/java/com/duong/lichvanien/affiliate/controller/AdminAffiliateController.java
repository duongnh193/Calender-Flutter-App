package com.duong.lichvanien.affiliate.controller;

import com.duong.lichvanien.affiliate.entity.AffiliateConfigEntity;
import com.duong.lichvanien.affiliate.entity.AffiliateMemberEntity;
import com.duong.lichvanien.affiliate.entity.ReferralEntity;
import com.duong.lichvanien.affiliate.entity.WithdrawalRequestEntity;
import com.duong.lichvanien.affiliate.enums.WithdrawalStatus;
import com.duong.lichvanien.affiliate.service.AffiliateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for affiliate management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/affiliate")
@RequiredArgsConstructor
@Tag(name = "Admin Affiliate", description = "Admin affiliate management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAffiliateController {

    private final AffiliateService affiliateService;

    @GetMapping("/config")
    @Operation(summary = "Get affiliate config", description = "Get all affiliate configuration")
    public ResponseEntity<List<AffiliateConfigEntity>> getConfig() {
        List<AffiliateConfigEntity> configs = affiliateService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    @PutMapping("/config")
    @Operation(summary = "Update affiliate config", description = "Update affiliate configuration")
    public ResponseEntity<AffiliateConfigEntity> updateConfig(@RequestBody Map<String, String> configUpdates) {
        affiliateService.updateConfigs(configUpdates);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members")
    @Operation(summary = "Get affiliate members", description = "Get all affiliate members")
    public ResponseEntity<Page<AffiliateMemberEntity>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AffiliateMemberEntity> members = affiliateService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/referrals")
    @Operation(summary = "Get all referrals", description = "Get all referrals")
    public ResponseEntity<Page<ReferralEntity>> getReferrals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReferralEntity> referrals = affiliateService.getAllReferrals(pageable);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "Get withdrawal requests", description = "Get all withdrawal requests")
    public ResponseEntity<Page<WithdrawalRequestEntity>> getWithdrawals(
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WithdrawalRequestEntity> withdrawals = status != null
                ? affiliateService.getWithdrawalsByStatus(status, pageable)
                : affiliateService.getAllWithdrawals(pageable);
        return ResponseEntity.ok(withdrawals);
    }

    @PostMapping("/withdrawals/{id}/approve")
    @Operation(summary = "Approve withdrawal", description = "Approve a withdrawal request")
    public ResponseEntity<Void> approveWithdrawal(@PathVariable Long id, @RequestParam(required = false) String adminNote) {
        affiliateService.approveWithdrawal(id, adminNote);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdrawals/{id}/reject")
    @Operation(summary = "Reject withdrawal", description = "Reject a withdrawal request")
    public ResponseEntity<Void> rejectWithdrawal(@PathVariable Long id, @RequestParam(required = false) String adminNote) {
        affiliateService.rejectWithdrawal(id, adminNote);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get affiliate statistics", description = "Get affiliate program statistics")
    public ResponseEntity<AffiliateStatistics> getStatistics() {
        AffiliateStatistics stats = affiliateService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Statistics DTO.
     */
    @lombok.Data
    @lombok.Builder
    public static class AffiliateStatistics {
        private long totalMembers;
        private long activeMembers;
        private long totalReferrals;
        private long totalCommissionXu;
        private long pendingCommissionXu;
        private long totalWithdrawals;
        private long pendingWithdrawals;
    }
}


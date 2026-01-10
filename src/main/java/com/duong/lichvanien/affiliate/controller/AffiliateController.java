package com.duong.lichvanien.affiliate.controller;

import com.duong.lichvanien.affiliate.dto.AffiliateStatusResponse;
import com.duong.lichvanien.affiliate.dto.WithdrawalRequestDto;
import com.duong.lichvanien.affiliate.entity.AffiliateMemberEntity;
import com.duong.lichvanien.affiliate.entity.ReferralEntity;
import com.duong.lichvanien.affiliate.entity.WithdrawalRequestEntity;
import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.repository.UserRepository;
import com.duong.lichvanien.xu.service.XuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for affiliate program (user).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/affiliate")
@RequiredArgsConstructor
@Tag(name = "Affiliate", description = "Affiliate program APIs")
@SecurityRequirement(name = "bearerAuth")
public class AffiliateController {

    private final AffiliateService affiliateService;
    private final XuService xuService;
    private final UserRepository userRepository;

    @GetMapping("/status")
    @Operation(summary = "Get affiliate status", description = "Get affiliate status for authenticated user")
    public ResponseEntity<AffiliateStatusResponse> getStatus() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        // Get user to get referral code
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var memberOpt = affiliateService.getAffiliateMember(userId);
        Integer minXu = affiliateService.getMinXuToJoin();
        Integer currentBalance = xuService.getBalance(userId);

        if (memberOpt.isPresent()) {
            AffiliateMemberEntity member = memberOpt.get();
            AffiliateStatusResponse response = AffiliateStatusResponse.builder()
                    .isMember(true)
                    .referralCode(user.getReferralCode()) // Trả về referral code 
                    .totalReferrals(member.getTotalReferrals())
                    .totalCommissionXu(member.getTotalCommissionXu())
                    .pendingCommissionXu(member.getPendingCommissionXu())
                    .withdrawnCommissionXu(member.getWithdrawnCommissionXu())
                    .minXuToJoin(minXu)
                    .currentXuBalance(currentBalance)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            AffiliateStatusResponse response = AffiliateStatusResponse.builder()
                    .isMember(false)
                    .referralCode(user.getReferralCode()) // Trả về referral code 
                    .minXuToJoin(minXu)
                    .currentXuBalance(currentBalance)
                    .build();
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/join")
    @Operation(summary = "Join affiliate program", description = "Join affiliate program (requires minimum xu)")
    public ResponseEntity<AffiliateStatusResponse> joinAffiliate() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        // Get user to get referral code
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        affiliateService.joinAffiliate(userId); // Join affiliate program

        AffiliateStatusResponse response = AffiliateStatusResponse.builder()
                .isMember(true)
                .referralCode(user.getReferralCode()) // Trả về referral code 
                .totalReferrals(0)
                .totalCommissionXu(0)
                .pendingCommissionXu(0)
                .withdrawnCommissionXu(0)
                .minXuToJoin(affiliateService.getMinXuToJoin())
                .currentXuBalance(xuService.getBalance(userId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/referrals")
    @Operation(summary = "Get referrals", description = "Get list of referrals for authenticated user")
    public ResponseEntity<Page<ReferralEntity>> getReferrals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        Pageable pageable = PageRequest.of(page, size);
        Page<ReferralEntity> referrals = affiliateService.getReferrals(userId, pageable);
        return ResponseEntity.ok(referrals);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Create withdrawal request", description = "Create withdrawal request for affiliate commission")
    public ResponseEntity<WithdrawalRequestEntity> createWithdrawal(@Valid @RequestBody WithdrawalRequestDto request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        WithdrawalRequestEntity withdrawal = affiliateService.createWithdrawalRequest(
                userId,
                request.getXuAmount(),
                request.getBankName(),
                request.getBankAccount(),
                request.getAccountHolderName()
        );

        return ResponseEntity.ok(withdrawal);
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "Get withdrawal history", description = "Get withdrawal request history for authenticated user")
    public ResponseEntity<Page<WithdrawalRequestEntity>> getWithdrawals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        Pageable pageable = PageRequest.of(page, size);
        Page<WithdrawalRequestEntity> withdrawals = affiliateService.getWithdrawalRequests(userId, pageable);
        return ResponseEntity.ok(withdrawals);
    }
}


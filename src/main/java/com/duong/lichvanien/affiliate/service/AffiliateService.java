package com.duong.lichvanien.affiliate.service;

import com.duong.lichvanien.affiliate.entity.AffiliateConfigEntity;
import com.duong.lichvanien.affiliate.entity.AffiliateMemberEntity;
import com.duong.lichvanien.affiliate.entity.ReferralEntity;
import com.duong.lichvanien.affiliate.entity.WithdrawalRequestEntity;
import com.duong.lichvanien.affiliate.enums.AffiliateStatus;
import com.duong.lichvanien.affiliate.enums.WithdrawalStatus;
import com.duong.lichvanien.affiliate.repository.AffiliateConfigRepository;
import com.duong.lichvanien.affiliate.repository.AffiliateMemberRepository;
import com.duong.lichvanien.affiliate.repository.ReferralRepository;
import com.duong.lichvanien.affiliate.repository.WithdrawalRequestRepository;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.repository.UserRepository;
import com.duong.lichvanien.xu.enums.XuTransactionType;
import com.duong.lichvanien.xu.service.XuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for affiliate program management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateService {

    private final AffiliateConfigRepository configRepository;
    private final AffiliateMemberRepository affiliateMemberRepository;
    private final ReferralRepository referralRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final UserRepository userRepository;
    private final XuService xuService;

    // ==================== Config Management ====================

    /**
     * Get config value as integer.
     */
    @Transactional(readOnly = true)
    public Integer getConfigInt(String key, Integer defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(AffiliateConfigEntity::getIntValue)
                .orElse(defaultValue);
    }

    /**
     * Get config value as double.
     */
    @Transactional(readOnly = true)
    public Double getConfigDouble(String key, Double defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(AffiliateConfigEntity::getDoubleValue)
                .orElse(defaultValue);
    }

    /**
     * Get interpretation price in xu.
     */
    @Transactional(readOnly = true)
    public Integer getInterpretationPriceXu() {
        return getConfigInt("interpretation_price_xu", 10);
    }

    /**
     * Get xu to VND rate.
     */
    @Transactional(readOnly = true)
    public Integer getXuToVndRate() {
        return getConfigInt("xu_to_vnd_rate", 1000);
    }

    /**
     * Get minimum xu to join affiliate.
     */
    @Transactional(readOnly = true)
    public Integer getMinXuToJoin() {
        return getConfigInt("min_xu_to_join", 100);
    }

    /**
     * Get commission rate.
     */
    @Transactional(readOnly = true)
    public Double getCommissionRate() {
        return getConfigDouble("commission_rate", 0.30);
    }

    /**
     * Get free referral count.
     */
    @Transactional(readOnly = true)
    public Integer getFreeReferralCount() {
        return getConfigInt("free_referral_count", 3);
    }

    /**
     * Get free referral xu reward.
     */
    @Transactional(readOnly = true)
    public Integer getFreeReferralXuReward() {
        return getConfigInt("free_referral_xu_reward", 10);
    }

    // ==================== Affiliate Member Management ====================

    /**
     * Join affiliate program.
     */
    @Transactional
    public AffiliateMemberEntity joinAffiliate(Long userId) {
        // Check if already a member
        Optional<AffiliateMemberEntity> existing = affiliateMemberRepository.findByUserId(userId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("User is already an affiliate member");
        }

        // Check minimum xu requirement
        Integer minXu = getMinXuToJoin();
        Integer userBalance = xuService.getBalance(userId);
        if (userBalance < minXu) {
            throw new IllegalArgumentException("Cần tối thiểu " + minXu + " xu để tham gia affiliate. Hiện tại: " + userBalance);
        }

        // Get user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Generate unique affiliate code
        String affiliateCode = generateAffiliateCode(userId);

        // Create affiliate member
        AffiliateMemberEntity member = AffiliateMemberEntity.builder()
                .user(user)
                .affiliateCode(affiliateCode)
                .status(AffiliateStatus.ACTIVE)
                .totalReferrals(0)
                .totalCommissionXu(0)
                .pendingCommissionXu(0)
                .withdrawnCommissionXu(0)
                .build();

        member = affiliateMemberRepository.save(member);

        log.info("User {} joined affiliate program with code: {}", userId, affiliateCode);

        return member;
    }

    /**
     * Get affiliate member by user ID.
     */
    @Transactional(readOnly = true)
    public Optional<AffiliateMemberEntity> getAffiliateMember(Long userId) {
        return affiliateMemberRepository.findByUserId(userId);
    }

    /**
     * Get affiliate member by code.
     */
    @Transactional(readOnly = true)
    public Optional<AffiliateMemberEntity> getAffiliateMemberByCode(String affiliateCode) {
        return affiliateMemberRepository.findByAffiliateCode(affiliateCode);
    }

    /**
     * Generate unique affiliate code.
     */
    private String generateAffiliateCode(Long userId) {
        // Format: LVN + first 6 chars of UUID + last 2 digits of userId
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        String userIdSuffix = String.format("%02d", userId % 100);
        return "LVN" + uuid + userIdSuffix;
    }

    // ==================== Referral Management ====================

    /**
     * Create referral when user registers with referral code.
     */
    @Transactional
    public void createReferral(Long referrerUserId, Long referredUserId) {
        // Check if referral already exists
        Optional<ReferralEntity> existing = referralRepository.findByReferredUserId(referredUserId);
        if (existing.isPresent()) {
            log.warn("Referral already exists for user: {}", referredUserId);
            return;
        }

        // Get users
        UserEntity referrer = userRepository.findById(referrerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Referrer user not found: " + referrerUserId));
        UserEntity referred = userRepository.findById(referredUserId)
                .orElseThrow(() -> new IllegalArgumentException("Referred user not found: " + referredUserId));

        // Check if referrer is affiliate member
        Optional<AffiliateMemberEntity> affiliateMember = affiliateMemberRepository.findByUserId(referrerUserId);

        // Create referral
        ReferralEntity referral = ReferralEntity.builder()
                .referrerUser(referrer)
                .referredUser(referred)
                .affiliateMember(affiliateMember.orElse(null))
                .commissionPaid(false)
                .build();

        referralRepository.save(referral);

        log.info("Created referral: {} -> {}", referrerUserId, referredUserId);
    }

    /**
     * Process referral conversion when referred user makes first payment.
     */
    @Transactional
    public void processReferralConversion(Long referredUserId, BigDecimal paymentAmount) {
        // Find referral
        Optional<ReferralEntity> referralOpt = referralRepository.findByReferredUserId(referredUserId);
        if (referralOpt.isEmpty()) {
            return; // No referral
        }

        ReferralEntity referral = referralOpt.get();

        // Check if already converted
        if (referral.getConvertedAt() != null) {
            return; // Already processed
        }

        // Mark as converted
        referral.markAsConverted(paymentAmount);

        Long referrerUserId = referral.getReferrerUser().getId();

        // Check if referrer is affiliate member
        if (referral.getAffiliateMember() != null) {
            // Affiliate member - give 30% commission
            processAffiliateCommission(referral, paymentAmount);
        } else {
            // Non-affiliate - count referral
            processNonAffiliateReferral(referrerUserId);
        }

        referralRepository.save(referral);
    }

    /**
     * Process affiliate commission (30%).
     */
    private void processAffiliateCommission(ReferralEntity referral, BigDecimal paymentAmount) {
        AffiliateMemberEntity affiliateMember = referral.getAffiliateMember();
        Double commissionRate = getCommissionRate();
        Integer xuToVndRate = getXuToVndRate();

        // Calculate commission in VND
        BigDecimal commissionVnd = paymentAmount.multiply(BigDecimal.valueOf(commissionRate));
        
        // Convert to xu
        Integer commissionXu = commissionVnd.divide(BigDecimal.valueOf(xuToVndRate), 0, java.math.RoundingMode.DOWN).intValue();

        // Add to affiliate member pending commission
        affiliateMember.addPendingCommission(commissionXu);
        affiliateMember.incrementReferrals();
        affiliateMemberRepository.save(affiliateMember);

        // Set commission in referral
        referral.setCommissionXu(commissionXu);

        log.info("Processed affiliate commission: {} xu for referral: {} -> {}",
                commissionXu, referral.getReferrerUser().getId(), referral.getReferredUser().getId());
    }

    /**
     * Process non-affiliate referral (count and give free xu after 3).
     */
    private void processNonAffiliateReferral(Long referrerUserId) {
        UserEntity referrer = userRepository.findById(referrerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Referrer user not found: " + referrerUserId));

        // Increment referral count
        int newCount = referrer.getReferralCount() + 1;
        referrer.setReferralCount(newCount);
        userRepository.save(referrer);

        // Check if reached free referral count
        Integer freeReferralCount = getFreeReferralCount();
        if (newCount >= freeReferralCount) {
            // Give free xu reward
            Integer rewardXu = getFreeReferralXuReward();
            xuService.addXu(
                    referrerUserId,
                    rewardXu,
                    XuTransactionType.REFERRAL_BONUS,
                    null,
                    "Thưởng giới thiệu " + freeReferralCount + " người dùng",
                    null
            );

            // Reset count
            referrer.setReferralCount(0);
            userRepository.save(referrer);

            log.info("Gave free xu reward: {} xu to user: {} after {} referrals",
                    rewardXu, referrerUserId, freeReferralCount);
        }
    }

    /**
     * Get referrals for user.
     */
    @Transactional(readOnly = true)
    public Page<ReferralEntity> getReferrals(Long userId, Pageable pageable) {
        return referralRepository.findByReferrerUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    // ==================== Withdrawal Management ====================

    /**
     * Create withdrawal request.
     */
    @Transactional
    public WithdrawalRequestEntity createWithdrawalRequest(Long userId, Integer xuAmount,
                                                           String bankName, String bankAccount, String accountHolderName) {
        // Get affiliate member
        AffiliateMemberEntity member = affiliateMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not an affiliate member"));

        // Check pending commission
        if (member.getPendingCommissionXu() < xuAmount) {
            throw new IllegalArgumentException("Không đủ hoa hồng để rút. Có sẵn: " + member.getPendingCommissionXu() + ", yêu cầu: " + xuAmount);
        }

        // Calculate VND amount
        Integer xuToVndRate = getXuToVndRate();
        BigDecimal vndAmount = BigDecimal.valueOf(xuAmount).multiply(BigDecimal.valueOf(xuToVndRate));

        // Create withdrawal request
        WithdrawalRequestEntity request = WithdrawalRequestEntity.builder()
                .affiliateMember(member)
                .xuAmount(xuAmount)
                .vndAmount(vndAmount)
                .bankName(bankName)
                .bankAccount(bankAccount)
                .accountHolderName(accountHolderName)
                .status(WithdrawalStatus.PENDING)
                .build();

        request = withdrawalRequestRepository.save(request);

        log.info("Created withdrawal request: {} xu ({} VND) for user: {}", xuAmount, vndAmount, userId);

        return request;
    }

    /**
     * Approve withdrawal request (admin).
     */
    @Transactional
    public void approveWithdrawal(Long withdrawalId, String adminNote) {
        WithdrawalRequestEntity request = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal request not found: " + withdrawalId));

        if (request.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalArgumentException("Withdrawal request is not pending");
        }

        // Approve
        request.approve(adminNote);
        withdrawalRequestRepository.save(request);

        // Move commission from pending to withdrawn
        AffiliateMemberEntity member = request.getAffiliateMember();
        member.withdrawCommission(request.getXuAmount());
        affiliateMemberRepository.save(member);

        log.info("Approved withdrawal request: {}", withdrawalId);
    }

    /**
     * Reject withdrawal request (admin).
     */
    @Transactional
    public void rejectWithdrawal(Long withdrawalId, String adminNote) {
        WithdrawalRequestEntity request = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal request not found: " + withdrawalId));

        if (request.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalArgumentException("Withdrawal request is not pending");
        }

        request.reject(adminNote);
        withdrawalRequestRepository.save(request);

        log.info("Rejected withdrawal request: {}", withdrawalId);
    }

    /**
     * Get withdrawal requests for user.
     */
    @Transactional(readOnly = true)
    public Page<WithdrawalRequestEntity> getWithdrawalRequests(Long userId, Pageable pageable) {
        AffiliateMemberEntity member = affiliateMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not an affiliate member"));

        return withdrawalRequestRepository.findByAffiliateMemberIdOrderByCreatedAtDesc(member.getId(), pageable);
    }

    /**
     * Get all configs (admin).
     */
    @Transactional(readOnly = true)
    public List<AffiliateConfigEntity> getAllConfigs() {
        return configRepository.findAll();
    }

    /**
     * Update configs (admin).
     */
    @Transactional
    public void updateConfigs(Map<String, String> configUpdates) {
        for (Map.Entry<String, String> entry : configUpdates.entrySet()) {
            AffiliateConfigEntity config = configRepository.findByConfigKey(entry.getKey())
                    .orElseGet(() -> {
                        AffiliateConfigEntity newConfig = new AffiliateConfigEntity();
                        newConfig.setConfigKey(entry.getKey());
                        return newConfig;
                    });
            config.setConfigValue(entry.getValue());
            configRepository.save(config);
        }
    }

    /**
     * Get all members (admin).
     */
    @Transactional(readOnly = true)
    public Page<AffiliateMemberEntity> getAllMembers(Pageable pageable) {
        return affiliateMemberRepository.findAll(pageable);
    }

    /**
     * Get all referrals (admin).
     */
    @Transactional(readOnly = true)
    public Page<ReferralEntity> getAllReferrals(Pageable pageable) {
        return referralRepository.findAll(pageable);
    }

    /**
     * Get all withdrawals (admin).
     */
    @Transactional(readOnly = true)
    public Page<WithdrawalRequestEntity> getAllWithdrawals(Pageable pageable) {
        return withdrawalRequestRepository.findAll(pageable);
    }

    /**
     * Get withdrawals by status (admin).
     */
    @Transactional(readOnly = true)
    public Page<WithdrawalRequestEntity> getWithdrawalsByStatus(WithdrawalStatus status, Pageable pageable) {
        return withdrawalRequestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * Get affiliate statistics (admin).
     */
    @Transactional(readOnly = true)
    public com.duong.lichvanien.affiliate.controller.AdminAffiliateController.AffiliateStatistics getStatistics() {
        long totalMembers = affiliateMemberRepository.count();
        long activeMembers = affiliateMemberRepository.countByStatus(AffiliateStatus.ACTIVE);
        long totalReferrals = referralRepository.count();
        Integer totalCommissionXu = affiliateMemberRepository.sumTotalCommissionXu();
        Integer pendingCommissionXu = affiliateMemberRepository.sumPendingCommissionXu();
        long totalWithdrawals = withdrawalRequestRepository.count();
        long pendingWithdrawals = withdrawalRequestRepository.countByStatus(WithdrawalStatus.PENDING);

        return com.duong.lichvanien.affiliate.controller.AdminAffiliateController.AffiliateStatistics.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .totalReferrals(totalReferrals)
                .totalCommissionXu(totalCommissionXu != null ? totalCommissionXu : 0)
                .pendingCommissionXu(pendingCommissionXu != null ? pendingCommissionXu : 0)
                .totalWithdrawals(totalWithdrawals)
                .pendingWithdrawals(pendingWithdrawals)
                .build();
    }
}


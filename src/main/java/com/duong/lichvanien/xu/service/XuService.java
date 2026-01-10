package com.duong.lichvanien.xu.service;

import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.repository.UserRepository;
import com.duong.lichvanien.xu.entity.UserXuAccountEntity;
import com.duong.lichvanien.xu.entity.XuPackageEntity;
import com.duong.lichvanien.xu.entity.XuTransactionEntity;
import com.duong.lichvanien.xu.enums.XuTransactionType;
import com.duong.lichvanien.xu.repository.UserXuAccountRepository;
import com.duong.lichvanien.xu.repository.XuPackageRepository;
import com.duong.lichvanien.xu.repository.XuTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for xu management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XuService {

    private final UserXuAccountRepository xuAccountRepository;
    private final XuTransactionRepository xuTransactionRepository;
    private final XuPackageRepository xuPackageRepository;
    private final UserRepository userRepository;

    /**
     * Get or create xu account for user.
     */
    @Transactional
    public UserXuAccountEntity getOrCreateAccount(Long userId) {
        return xuAccountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                    
                    UserXuAccountEntity account = UserXuAccountEntity.builder()
                            .user(user)
                            .xuBalance(0)
                            .totalXuEarned(0)
                            .totalXuSpent(0)
                            .build();
                    
                    return xuAccountRepository.save(account);
                });
    }

    /**
     * Get xu balance for user.
     */
    @Transactional(readOnly = true)
    public Integer getBalance(Long userId) {
        return xuAccountRepository.findByUserId(userId)
                .map(UserXuAccountEntity::getXuBalance)
                .orElse(0);
    }

    /**
     * Add xu to user account (deposit, commission, bonus).
     */
    @Transactional
    public void addXu(Long userId, Integer xuAmount, XuTransactionType transactionType, 
                     String referenceId, String description, BigDecimal vndAmount) {
        if (xuAmount <= 0) {
            throw new IllegalArgumentException("Xu amount must be positive");
        }

        UserXuAccountEntity account = getOrCreateAccount(userId);
        account.addXu(xuAmount);
        xuAccountRepository.save(account);

        // Create transaction record
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        XuTransactionEntity transaction = XuTransactionEntity.builder()
                .user(user)
                .transactionType(transactionType)
                .xuAmount(xuAmount)
                .vndAmount(vndAmount)
                .referenceId(referenceId)
                .description(description)
                .build();

        xuTransactionRepository.save(transaction);

        log.info("Added {} xu to user {} (type: {}, reference: {})", 
                xuAmount, userId, transactionType, referenceId);
    }

    /**
     * Deduct xu from user account (purchase).
     */
    @Transactional
    public void deductXu(Long userId, Integer xuAmount, String referenceId, String description) {
        if (xuAmount <= 0) {
            throw new IllegalArgumentException("Xu amount must be positive");
        }

        UserXuAccountEntity account = getOrCreateAccount(userId);
        
        if (!account.hasEnoughXu(xuAmount)) {
            throw new IllegalArgumentException("Insufficient xu balance. Required: " + xuAmount + ", Available: " + account.getXuBalance());
        }

        account.deductXu(xuAmount);
        xuAccountRepository.save(account);

        // Create transaction record
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        XuTransactionEntity transaction = XuTransactionEntity.builder()
                .user(user)
                .transactionType(XuTransactionType.PURCHASE)
                .xuAmount(-xuAmount) // Negative for debit
                .referenceId(referenceId)
                .description(description)
                .build();

        xuTransactionRepository.save(transaction);

        log.info("Deducted {} xu from user {} (reference: {})", xuAmount, userId, referenceId);
    }

    /**
     * Check if user has enough xu.
     */
    @Transactional(readOnly = true)
    public boolean hasEnoughXu(Long userId, Integer requiredXu) {
        return getBalance(userId) >= requiredXu;
    }

    /**
     * Get all active xu packages.
     */
    @Transactional(readOnly = true)
    public List<XuPackageEntity> getActivePackages() {
        return xuPackageRepository.findAllActiveOrderByDisplayOrder();
    }

    /**
     * Get all xu packages (admin).
     */
    @Transactional(readOnly = true)
    public List<XuPackageEntity> getAllPackages() {
        return xuPackageRepository.findAllOrderByDisplayOrder();
    }

    /**
     * Get xu package by ID.
     */
    @Transactional(readOnly = true)
    public Optional<XuPackageEntity> getPackageById(Long packageId) {
        return xuPackageRepository.findById(packageId);
    }

    /**
     * Get xu transaction history for user.
     */
    @Transactional(readOnly = true)
    public Page<com.duong.lichvanien.xu.dto.XuTransactionResponse> getTransactionHistory(Long userId, Pageable pageable) {
        Page<XuTransactionEntity> entities = xuTransactionRepository.findByUserIdWithUserOrderByCreatedAtDesc(userId, pageable);
        return entities.map(this::toTransactionResponse);
    }
    
    /**
     * Convert entity to response DTO.
     */
    private com.duong.lichvanien.xu.dto.XuTransactionResponse toTransactionResponse(XuTransactionEntity entity) {
        com.duong.lichvanien.xu.dto.XuTransactionResponse.XuTransactionResponseBuilder builder = 
                com.duong.lichvanien.xu.dto.XuTransactionResponse.builder()
                .id(entity.getId())
                .transactionType(entity.getTransactionType())
                .xuAmount(entity.getXuAmount())
                .vndAmount(entity.getVndAmount())
                .referenceId(entity.getReferenceId())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .isCredit(entity.isCredit())
                .isDebit(entity.isDebit());
        
        // Safely access user (lazy loading within transaction)
        if (entity.getUser() != null) {
            builder.userId(entity.getUser().getId())
                   .userEmail(entity.getUser().getEmail())
                   .userUsername(entity.getUser().getUsername());
        }
        
        return builder.build();
    }

    /**
     * Get xu transactions by type for user.
     */
    @Transactional(readOnly = true)
    public List<XuTransactionEntity> getTransactionsByType(Long userId, XuTransactionType transactionType) {
        return xuTransactionRepository.findByUserIdAndTransactionTypeOrderByCreatedAtDesc(userId, transactionType);
    }

    /**
     * Get xu account details.
     */
    @Transactional(readOnly = true)
    public Optional<UserXuAccountEntity> getAccount(Long userId) {
        return xuAccountRepository.findByUserId(userId);
    }

    /**
     * Get interpretation price in xu (from affiliate config).
     * This is a placeholder - will be implemented in AffiliateService.
     */
    @Transactional(readOnly = true)
    public Integer getInterpretationPriceXu() {
        // Default price, will be loaded from affiliate_config
        return 10;
    }

    /**
     * Create xu package (admin).
     */
    @Transactional
    public XuPackageEntity createPackage(XuPackageEntity packageEntity) {
        return xuPackageRepository.save(packageEntity);
    }

    /**
     * Update xu package (admin).
     */
    @Transactional
    public XuPackageEntity updatePackage(Long id, com.duong.lichvanien.xu.dto.AdminXuPackageRequest request) {
        XuPackageEntity packageEntity = xuPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found: " + id));

        if (request.getName() != null) {
            packageEntity.setName(request.getName());
        }
        if (request.getXuAmount() != null) {
            packageEntity.setXuAmount(request.getXuAmount());
        }
        if (request.getPriceVnd() != null) {
            packageEntity.setPriceVnd(request.getPriceVnd());
        }
        if (request.getBonusXu() != null) {
            packageEntity.setBonusXu(request.getBonusXu());
        }
        if (request.getIsActive() != null) {
            packageEntity.setIsActive(request.getIsActive());
        }
        if (request.getDisplayOrder() != null) {
            packageEntity.setDisplayOrder(request.getDisplayOrder());
        }

        return xuPackageRepository.save(packageEntity);
    }

    /**
     * Delete xu package (admin).
     */
    @Transactional
    public void deletePackage(Long id) {
        if (!xuPackageRepository.existsById(id)) {
            throw new IllegalArgumentException("Package not found: " + id);
        }
        xuPackageRepository.deleteById(id);
    }

    /**
     * Get all xu transactions (admin).
     */
    @Transactional(readOnly = true)
    public Page<com.duong.lichvanien.xu.dto.XuTransactionResponse> getAllTransactions(Pageable pageable) {
        Page<XuTransactionEntity> entities = xuTransactionRepository.findAllWithUserOrderByCreatedAtDesc(pageable);
        return entities.map(this::toTransactionResponse);
    }

    /**
     * Get xu statistics (admin).
     */
    @Transactional(readOnly = true)
    public com.duong.lichvanien.xu.controller.AdminXuController.XuStatistics getStatistics() {
        long totalUsers = xuAccountRepository.count();
        
        // Sum total xu earned and spent
        List<UserXuAccountEntity> accounts = xuAccountRepository.findAll();
        long totalXuInCirculation = accounts.stream()
                .mapToLong(UserXuAccountEntity::getXuBalance)
                .sum();
        long totalXuEarned = accounts.stream()
                .mapToLong(UserXuAccountEntity::getTotalXuEarned)
                .sum();
        long totalXuSpent = accounts.stream()
                .mapToLong(UserXuAccountEntity::getTotalXuSpent)
                .sum();

        // Count transactions by type
        long totalDeposits = xuTransactionRepository.countByTypeAndDateRange(
                XuTransactionType.DEPOSIT, LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now());
        long totalPurchases = xuTransactionRepository.countByTypeAndDateRange(
                XuTransactionType.PURCHASE, LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now());

        return com.duong.lichvanien.xu.controller.AdminXuController.XuStatistics.builder()
                .totalUsers(totalUsers)
                .totalXuInCirculation(totalXuInCirculation)
                .totalXuEarned(totalXuEarned)
                .totalXuSpent(totalXuSpent)
                .totalDeposits(totalDeposits)
                .totalPurchases(totalPurchases)
                .build();
    }
}


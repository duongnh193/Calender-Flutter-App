package com.duong.lichvanien.admin.controller;

import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.sepay.repository.SepayTransactionRepository;
import com.duong.lichvanien.sepay.enums.SepayTransactionStatus;
import com.duong.lichvanien.user.service.AdminUserService;
import com.duong.lichvanien.xu.service.XuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Admin controller for statistics.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin Statistics", description = "Admin statistics APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final SepayTransactionRepository sepayTransactionRepository;
    private final AdminUserService adminUserService;
    private final XuService xuService;
    private final AffiliateService affiliateService;

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue", description = "Get total revenue statistics")
    public ResponseEntity<RevenueStatistics> getRevenue(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

        BigDecimal totalRevenue = sepayTransactionRepository.sumAmountByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);
        long totalTransactions = sepayTransactionRepository.countByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);

        RevenueStatistics stats = RevenueStatistics.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(totalTransactions)
                .startDate(start)
                .endDate(end)
                .build();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/daily")
    @Operation(summary = "Get daily revenue", description = "Get revenue statistics by day")
    public ResponseEntity<RevenueStatistics> getDailyRevenue(@RequestParam String date) {
        // Parse date and get day range
        LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime end = start.plusDays(1);

        BigDecimal totalRevenue = sepayTransactionRepository.sumAmountByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);
        long totalTransactions = sepayTransactionRepository.countByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);

        RevenueStatistics stats = RevenueStatistics.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(totalTransactions)
                .startDate(start)
                .endDate(end)
                .build();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/monthly")
    @Operation(summary = "Get monthly revenue", description = "Get revenue statistics by month")
    public ResponseEntity<RevenueStatistics> getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        BigDecimal totalRevenue = sepayTransactionRepository.sumAmountByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);
        long totalTransactions = sepayTransactionRepository.countByStatusAndDateRange(
                SepayTransactionStatus.COMPLETED, start, end);

        RevenueStatistics stats = RevenueStatistics.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(totalTransactions)
                .startDate(start)
                .endDate(end)
                .build();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @Operation(summary = "Get user statistics", description = "Get user statistics")
    public ResponseEntity<AdminUserService.UserStatistics> getUserStatistics() {
        return ResponseEntity.ok(adminUserService.getStatistics());
    }

    @GetMapping("/affiliate")
    @Operation(summary = "Get affiliate statistics", description = "Get affiliate program statistics")
    public ResponseEntity<com.duong.lichvanien.affiliate.controller.AdminAffiliateController.AffiliateStatistics> getAffiliateStatistics() {
        return ResponseEntity.ok(affiliateService.getStatistics());
    }

    /**
     * Revenue statistics DTO.
     */
    @lombok.Data
    @lombok.Builder
    public static class RevenueStatistics {
        private BigDecimal totalRevenue;
        private long totalTransactions;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}


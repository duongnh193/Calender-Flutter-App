package com.duong.lichvanien.xu.controller;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.sepay.service.SepayService;
import com.duong.lichvanien.xu.dto.XuBalanceResponse;
import com.duong.lichvanien.xu.dto.XuDepositRequest;
import com.duong.lichvanien.xu.dto.XuPurchaseRequest;
import com.duong.lichvanien.xu.dto.XuTransactionResponse;
import com.duong.lichvanien.xu.entity.XuPackageEntity;
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

import java.util.List;

/**
 * REST Controller for xu management (user).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/xu")
@RequiredArgsConstructor
@Tag(name = "Xu", description = "Xu currency APIs")
@SecurityRequirement(name = "bearerAuth")
public class XuController {

    private final XuService xuService;
    private final SepayService sepayService;

    @GetMapping("/balance")
    @Operation(summary = "Get xu balance", description = "Get current xu balance for authenticated user")
    public ResponseEntity<XuBalanceResponse> getBalance() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        Integer balance = xuService.getBalance(userId);
        var account = xuService.getAccount(userId).orElse(null);

        XuBalanceResponse response = XuBalanceResponse.builder()
                .xuBalance(balance)
                .totalXuEarned(account != null ? account.getTotalXuEarned() : 0)
                .totalXuSpent(account != null ? account.getTotalXuSpent() : 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/packages")
    @Operation(summary = "Get xu packages", description = "Get all active xu packages")
    public ResponseEntity<List<XuPackageEntity>> getPackages() {
        List<XuPackageEntity> packages = xuService.getActivePackages();
        return ResponseEntity.ok(packages);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Create deposit transaction", description = "Create SePay transaction for xu deposit")
    public ResponseEntity<SepayService.SepayDepositResponse> createDeposit(@Valid @RequestBody XuDepositRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        SepayService.SepayDepositResponse response = sepayService.createDepositTransaction(userId, request.getPackageId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transaction history", description = "Get xu transaction history for authenticated user")
    public ResponseEntity<Page<XuTransactionResponse>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        Pageable pageable = PageRequest.of(page, size);
        Page<XuTransactionResponse> transactions = xuService.getTransactionHistory(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/purchase")
    @Operation(summary = "Purchase interpretation with xu", description = "Purchase Tu Vi interpretation using xu. Note: Use /tuvi/chart/interpretation endpoint instead - this endpoint is for backward compatibility.")
    public ResponseEntity<?> purchaseInterpretation(@Valid @RequestBody XuPurchaseRequest request) {
        // This endpoint is deprecated - use /tuvi/chart/interpretation instead
        // Xu will be deducted automatically in TuViChartController
        throw new UnsupportedOperationException("Vui lòng sử dụng endpoint /api/v1/tuvi/chart/interpretation để mua giải luận bằng xu");
    }
}


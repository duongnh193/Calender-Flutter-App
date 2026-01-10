package com.duong.lichvanien.xu.controller;

import com.duong.lichvanien.xu.dto.AdminXuPackageRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for xu management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/xu")
@RequiredArgsConstructor
@Tag(name = "Admin Xu", description = "Admin xu management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminXuController {

    private final XuService xuService;

    @GetMapping("/packages")
    @Operation(summary = "Get all xu packages", description = "Get all xu packages (admin)")
    public ResponseEntity<List<XuPackageEntity>> getAllPackages() {
        List<XuPackageEntity> packages = xuService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    @PostMapping("/packages")
    @Operation(summary = "Create xu package", description = "Create a new xu package")
    public ResponseEntity<XuPackageEntity> createPackage(@Valid @RequestBody AdminXuPackageRequest request) {
        XuPackageEntity packageEntity = XuPackageEntity.builder()
                .name(request.getName())
                .xuAmount(request.getXuAmount())
                .priceVnd(request.getPriceVnd())
                .bonusXu(request.getBonusXu() != null ? request.getBonusXu() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        packageEntity = xuService.createPackage(packageEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(packageEntity);
    }

    @PutMapping("/packages/{id}")
    @Operation(summary = "Update xu package", description = "Update an existing xu package")
    public ResponseEntity<XuPackageEntity> updatePackage(@PathVariable Long id, @Valid @RequestBody AdminXuPackageRequest request) {
        XuPackageEntity packageEntity = xuService.updatePackage(id, request);
        return ResponseEntity.ok(packageEntity);
    }

    @DeleteMapping("/packages/{id}")
    @Operation(summary = "Delete xu package", description = "Delete an xu package")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        xuService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all xu transactions", description = "Get all xu transactions with pagination")
    public ResponseEntity<Page<XuTransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<XuTransactionResponse> transactions = xuService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get xu statistics", description = "Get xu system statistics")
    public ResponseEntity<XuStatistics> getStatistics() {
        XuStatistics stats = xuService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Statistics DTO.
     */
    @lombok.Data
    @lombok.Builder
    public static class XuStatistics {
        private long totalUsers;
        private long totalXuInCirculation;
        private long totalXuEarned;
        private long totalXuSpent;
        private long totalDeposits;
        private long totalPurchases;
    }
}


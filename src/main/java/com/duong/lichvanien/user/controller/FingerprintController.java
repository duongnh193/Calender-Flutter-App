package com.duong.lichvanien.user.controller;

import com.duong.lichvanien.user.dto.AuthResponse;
import com.duong.lichvanien.user.dto.FingerprintRequest;
import com.duong.lichvanien.user.dto.FingerprintResponse;
import com.duong.lichvanien.user.entity.FingerprintEntity;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import com.duong.lichvanien.user.service.FingerprintService;
import com.duong.lichvanien.user.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for fingerprint management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user/fingerprint")
@RequiredArgsConstructor
@Tag(name = "Fingerprint", description = "Device fingerprint APIs")
public class FingerprintController {

    private final FingerprintService fingerprintService;
    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Submit fingerprint", 
               description = "Submit client-side fingerprint data and get fingerprint ID with anonymous session")
    public ResponseEntity<AuthResponse> submitFingerprint(
            @RequestBody(required = false) FingerprintRequest request,
            HttpServletRequest httpRequest) {
        
        // Generate fingerprint
        FingerprintResponse fingerprintResponse = fingerprintService.generateFingerprint(request, httpRequest);
        
        // Create anonymous session
        AuthResponse authResponse = sessionService.createAnonymousSession(
                fingerprintResponse.getFingerprintId(), httpRequest);
        
        log.info("Fingerprint submitted: {}, isNew: {}", 
                fingerprintResponse.getFingerprintId(), fingerprintResponse.isNew());
        
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/current")
    @Operation(summary = "Get current fingerprint", 
               description = "Get the fingerprint ID for the current request")
    public ResponseEntity<FingerprintResponse> getCurrentFingerprint(HttpServletRequest httpRequest) {
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        
        if (fingerprintId == null) {
            // Generate on the fly
            fingerprintId = fingerprintService.generateFingerprintId(null, httpRequest);
        }
        
        FingerprintEntity fingerprint = fingerprintService.getFingerprintById(fingerprintId)
                .orElse(null);
        
        FingerprintResponse response = FingerprintResponse.builder()
                .fingerprintId(fingerprintId)
                .isNew(fingerprint == null)
                .firstSeenAt(fingerprint != null ? fingerprint.getFirstSeenAt() : null)
                .usageCount(fingerprint != null ? fingerprint.getUsageCount() : 0)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fingerprintId}")
    @Operation(summary = "Get fingerprint info", 
               description = "Get fingerprint information by ID (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FingerprintResponse> getFingerprintInfo(@PathVariable String fingerprintId) {
        // TODO: Add admin role check
        FingerprintEntity fingerprint = fingerprintService.getFingerprintById(fingerprintId)
                .orElseThrow(() -> new IllegalArgumentException("Fingerprint not found"));
        
        FingerprintResponse response = FingerprintResponse.builder()
                .fingerprintId(fingerprint.getFingerprintId())
                .isNew(false)
                .firstSeenAt(fingerprint.getFirstSeenAt())
                .usageCount(fingerprint.getUsageCount())
                .build();
        
        return ResponseEntity.ok(response);
    }
}


package com.duong.lichvanien.tuvi.controller;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.CycleInterpretationResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.service.TuViChartService;
import com.duong.lichvanien.tuvi.service.TuViGrokInterpretationService;
import com.duong.lichvanien.user.dto.PaymentCheckResponse;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Tu Vi interpretations using Grok AI.
 * Provides separate endpoints for full interpretation (paid) and cycle interpretation (free).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tuvi/grok")
@RequiredArgsConstructor
@Tag(name = "Tu Vi Grok Interpretation", description = "APIs for Tu Vi interpretations powered by Grok AI")
public class TuViGrokInterpretationController {

    private final TuViGrokInterpretationService interpretationService;
    private final TuViChartService chartService;

    /**
     * Generate full Tu Vi interpretation (PAID).
     * Requires xu payment or previous payment.
     */
    @PostMapping("/interpretation/full")
    @Operation(
        summary = "Generate full Tu Vi interpretation (Paid)",
        description = "Generate comprehensive Tu Vi interpretation including all 12 palaces. " +
                     "Requires xu payment. The response is cached for future requests.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Interpretation generated successfully",
                content = @Content(schema = @Schema(implementation = TuViInterpretationResponse.class))
            ),
            @ApiResponse(
                responseCode = "402",
                description = "Payment required - not enough xu"
            ),
            @ApiResponse(
                responseCode = "503",
                description = "Grok AI service unavailable"
            )
        }
    )
    public ResponseEntity<TuViInterpretationResponse> generateFullInterpretation(
            @Valid @RequestBody TuViChartRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received full interpretation request for: {}", request.getName());

        // Check if Grok is available
        if (!interpretationService.isGrokAvailable()) {
            log.error("Grok AI service is not available");
            throw new RuntimeException("Dịch vụ AI không khả dụng. Vui lòng thử lại sau.");
        }

        // Generate chart to get chart hash
        TuViChartResponse chart = chartService.generateChart(request);
        String chartHash = chart.getChartHash();
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);

        // Check access
        boolean hasAccess = interpretationService.checkFullInterpretationAccess(userId, fingerprintId, chartHash);

        if (!hasAccess) {
            // Try to deduct xu if user is authenticated
            if (userId != null) {
                boolean deducted = interpretationService.deductXuForFullInterpretation(userId, chartHash);
                if (!deducted) {
                    Integer priceXu = interpretationService.getFullInterpretationPriceXu();
                    throw new IllegalArgumentException(
                            "Không đủ xu để xem giải luận. Cần " + priceXu + " xu.");
                }
                hasAccess = true;
            } else {
                Integer priceXu = interpretationService.getFullInterpretationPriceXu();
                throw new IllegalArgumentException(
                        "Cần đăng nhập và có " + priceXu + " xu để xem giải luận đầy đủ.");
            }
        }

        // Generate interpretation
        TuViInterpretationResponse response = interpretationService.generateFullInterpretation(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate cycle (Đại hạn/Tiểu vận) interpretation (FREE).
     * No payment required - this is preview content.
     */
    @PostMapping("/interpretation/cycles")
    @Operation(
        summary = "Generate cycle interpretation (Free)",
        description = "Generate Đại hạn/Tiểu vận interpretation. " +
                     "This is FREE content available to all users as a preview. " +
                     "The response is cached for future requests.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Cycle interpretation generated successfully",
                content = @Content(schema = @Schema(implementation = CycleInterpretationResponse.class))
            ),
            @ApiResponse(
                responseCode = "503",
                description = "Grok AI service unavailable"
            )
        }
    )
    public ResponseEntity<CycleInterpretationResponse> generateCycleInterpretation(
            @Valid @RequestBody TuViChartRequest request) {
        
        log.info("Received cycle interpretation request for: {}", request.getName());

        // Check if Grok is available
        if (!interpretationService.isGrokAvailable()) {
            log.error("Grok AI service is not available");
            throw new RuntimeException("Dịch vụ AI không khả dụng. Vui lòng thử lại sau.");
        }

        // Generate interpretation (no payment check - FREE)
        CycleInterpretationResponse response = interpretationService.generateCycleInterpretation(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check access to full interpretation.
     */
    @GetMapping("/interpretation/full/check")
    @Operation(
        summary = "Check access to full interpretation",
        description = "Check if user has access to full interpretation for a specific chart",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Access status returned"
            )
        }
    )
    public ResponseEntity<PaymentCheckResponse> checkFullInterpretationAccess(
            @Parameter(description = "Chart hash", required = true)
            @RequestParam String chartHash,
            HttpServletRequest httpRequest) {
        
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);

        boolean hasAccess = interpretationService.checkFullInterpretationAccess(userId, fingerprintId, chartHash);
        Integer priceXu = interpretationService.getFullInterpretationPriceXu();

        PaymentCheckResponse response = PaymentCheckResponse.builder()
                .hasAccess(hasAccess)
                .contentType("TUVI_INTERPRETATION_FULL")
                .contentId(chartHash)
                .message(hasAccess ? "Đã có quyền truy cập" : "Cần " + priceXu + " xu để xem giải luận")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint for full interpretation (alternative).
     */
    @GetMapping("/interpretation/full")
    @Operation(
        summary = "Generate full interpretation (GET)",
        description = "Alternative GET endpoint for full interpretation"
    )
    public ResponseEntity<TuViInterpretationResponse> generateFullInterpretationGet(
            @Parameter(description = "Birth date (yyyy-MM-dd)", required = true)
            @RequestParam String date,
            
            @Parameter(description = "Birth hour (0-23)", required = true)
            @RequestParam Integer hour,
            
            @Parameter(description = "Birth minute (0-59)")
            @RequestParam(defaultValue = "0") Integer minute,
            
            @Parameter(description = "Gender (male/female)", required = true)
            @RequestParam String gender,
            
            @Parameter(description = "Is lunar date")
            @RequestParam(defaultValue = "false") Boolean isLunar,
            
            @Parameter(description = "Is leap month")
            @RequestParam(defaultValue = "false") Boolean isLeapMonth,
            
            @Parameter(description = "Name")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Birth place")
            @RequestParam(required = false) String birthPlace,
            
            HttpServletRequest httpRequest) {
        
        TuViChartRequest request = TuViChartRequest.builder()
                .date(date)
                .hour(hour)
                .minute(minute)
                .gender(gender)
                .isLunar(isLunar)
                .isLeapMonth(isLeapMonth)
                .name(name)
                .birthPlace(birthPlace)
                .build();
        
        return generateFullInterpretation(request, httpRequest);
    }

    /**
     * GET endpoint for cycle interpretation (alternative).
     */
    @GetMapping("/interpretation/cycles")
    @Operation(
        summary = "Generate cycle interpretation (GET)",
        description = "Alternative GET endpoint for cycle interpretation (FREE)"
    )
    public ResponseEntity<CycleInterpretationResponse> generateCycleInterpretationGet(
            @Parameter(description = "Birth date (yyyy-MM-dd)", required = true)
            @RequestParam String date,
            
            @Parameter(description = "Birth hour (0-23)", required = true)
            @RequestParam Integer hour,
            
            @Parameter(description = "Birth minute (0-59)")
            @RequestParam(defaultValue = "0") Integer minute,
            
            @Parameter(description = "Gender (male/female)", required = true)
            @RequestParam String gender,
            
            @Parameter(description = "Is lunar date")
            @RequestParam(defaultValue = "false") Boolean isLunar,
            
            @Parameter(description = "Is leap month")
            @RequestParam(defaultValue = "false") Boolean isLeapMonth,
            
            @Parameter(description = "Name")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Birth place")
            @RequestParam(required = false) String birthPlace) {
        
        TuViChartRequest request = TuViChartRequest.builder()
                .date(date)
                .hour(hour)
                .minute(minute)
                .gender(gender)
                .isLunar(isLunar)
                .isLeapMonth(isLeapMonth)
                .name(name)
                .birthPlace(birthPlace)
                .build();
        
        return generateCycleInterpretation(request);
    }

    /**
     * Check Grok AI service status.
     */
    @GetMapping("/status")
    @Operation(
        summary = "Check Grok AI service status",
        description = "Check if Grok AI service is available"
    )
    public ResponseEntity<Object> checkGrokStatus() {
        boolean available = interpretationService.isGrokAvailable();
        
        return ResponseEntity.ok(java.util.Map.of(
                "available", available,
                "message", available ? "Grok AI service is available" : "Grok AI service is not configured"
        ));
    }
}


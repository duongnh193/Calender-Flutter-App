package com.duong.lichvanien.tuvi.controller;

import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.service.TuViChartService;
import com.duong.lichvanien.tuvi.service.TuViInterpretationService;
import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.user.dto.PaymentCheckResponse;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import com.duong.lichvanien.user.service.PaymentService;
import com.duong.lichvanien.xu.service.XuService;
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
 * REST Controller for Tu Vi (Purple Star Astrology) chart generation.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tuvi")
@RequiredArgsConstructor
@Tag(name = "Tu Vi Chart", description = "APIs for generating Tu Vi (Purple Star Astrology) charts")
public class TuViChartController {

    private final TuViChartService tuViChartService;
    private final TuViInterpretationService tuViInterpretationService;
    private final PaymentService paymentService;
    private final XuService xuService;
    private final AffiliateService affiliateService;

    @PostMapping("/chart")
    @Operation(
        summary = "Generate Tu Vi chart",
        description = "Generate a complete Tu Vi chart based on birth date, time, and gender. " +
                     "Returns all 12 palaces with stars, Tuần/Triệt markers, and Đại Vận cycles.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Chart generated successfully",
                content = @Content(schema = @Schema(implementation = TuViChartResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            )
        }
    )
    public ResponseEntity<TuViChartResponse> generateChart(
            @Valid @RequestBody TuViChartRequest request) {
        
        log.info("Received Tu Vi chart request for date={}, hour={}, gender={}",
                request.getDate(), request.getHour(), request.getGender());
        
        TuViChartResponse response = tuViChartService.generateChart(request);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart")
    @Operation(
        summary = "Get or Generate Tu Vi chart (GET)",
        description = "Get chart by chartHash, or generate a new chart using query parameters. " +
                     "If chartHash is provided, retrieves existing chart from database. " +
                     "Otherwise, generates a new chart from birth parameters.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Chart retrieved or generated successfully",
                content = @Content(schema = @Schema(implementation = TuViChartResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Chart not found for provided chartHash"
            )
        }
    )
    public ResponseEntity<TuViChartResponse> getChart(
            @Parameter(description = "Chart hash to retrieve existing chart")
            @RequestParam(required = false) String chartHash,
            
            @Parameter(description = "Birth date (yyyy-MM-dd) - required if chartHash not provided", example = "1995-03-02")
            @RequestParam(required = false) String date,
            
            @Parameter(description = "Birth hour (0-23) - required if chartHash not provided", example = "8")
            @RequestParam(required = false) Integer hour,
            
            @Parameter(description = "Birth minute (0-59)", example = "30")
            @RequestParam(defaultValue = "0") Integer minute,
            
            @Parameter(description = "Gender (male/female) - required if chartHash not provided", example = "female")
            @RequestParam(required = false) String gender,
            
            @Parameter(description = "Is lunar date", example = "false")
            @RequestParam(defaultValue = "false") Boolean isLunar,
            
            @Parameter(description = "Is leap month (only for lunar dates)")
            @RequestParam(defaultValue = "false") Boolean isLeapMonth,
            
            @Parameter(description = "Name (optional, for display)")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Birth place (optional, for display)")
            @RequestParam(required = false) String birthPlace) {
        
        // If chartHash is provided, retrieve chart from database
        if (chartHash != null && !chartHash.isEmpty()) {
            log.info("Retrieving chart by hash: {}", chartHash);
            try {
                TuViChartResponse chart = tuViChartService.getChartByHash(chartHash);
                return ResponseEntity.ok(chart);
            } catch (IllegalArgumentException e) {
                log.error("Chart not found for hash: {}", chartHash);
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                log.error("Error retrieving chart by hash: {}", chartHash, e);
                throw new RuntimeException("Lỗi khi lấy lá số từ database: " + e.getMessage(), e);
            }
        }
        
        // Otherwise, generate new chart from parameters
        if (date == null || hour == null || gender == null) {
            throw new IllegalArgumentException(
                "Either chartHash or (date, hour, gender) must be provided");
        }
        
        log.info("Generating new chart from parameters: date={}, hour={}, gender={}", date, hour, gender);
        
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
        
        return generateChart(request);
    }

    @PostMapping("/chart/interpretation")
    @Operation(
        summary = "Generate Tu Vi chart interpretation",
        description = "Generate detailed AI-powered interpretation for a Tu Vi chart. " +
                     "Returns comprehensive analysis for all 12 palaces plus overview section. " +
                     "Note: This endpoint may take 30-60 seconds due to AI generation. " +
                     "Payment is required for this endpoint.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Interpretation generated successfully",
                content = @Content(schema = @Schema(implementation = TuViInterpretationResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "402",
                description = "Payment required"
            ),
            @ApiResponse(
                responseCode = "503",
                description = "AI service unavailable"
            )
        }
    )
    public ResponseEntity<TuViInterpretationResponse> generateInterpretation(
            @Valid @RequestBody TuViChartRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received Tu Vi interpretation request for date={}, hour={}, gender={}",
                request.getDate(), request.getHour(), request.getGender());
        
        // Generate chart first to get chart hash
        TuViChartResponse chartResponse = tuViChartService.generateChart(request);
        String chartHash = chartResponse.getChartHash();
        
        // Get fingerprint ID from request
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        
        // Check payment - try xu first, then fallback to old payment system
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);
        boolean hasAccess = false;
        
        if (userId != null) {
            // User is authenticated - check xu balance
            Integer priceXu = affiliateService.getInterpretationPriceXu();
            if (xuService.hasEnoughXu(userId, priceXu)) {
                // Deduct xu and grant access
                xuService.deductXu(userId, priceXu, chartHash, "Mua giải luận Tử Vi - " + chartHash);
                hasAccess = true;
                log.info("User {} purchased interpretation with {} xu", userId, priceXu);
            }
        }
        
        // Fallback to old payment system if not using xu
        if (!hasAccess && fingerprintId != null && chartHash != null) {
            try {
                paymentService.verifyPaymentOrThrow(fingerprintId, "TUVI_INTERPRETATION", chartHash);
                hasAccess = true;
            } catch (Exception e) {
                // Payment required
                throw new IllegalArgumentException("Cần thanh toán hoặc đủ xu để xem giải luận. Giá: " + 
                        (userId != null ? affiliateService.getInterpretationPriceXu() + " xu" : "thanh toán"));
            }
        }
        
        if (!hasAccess) {
            throw new IllegalArgumentException("Cần đăng nhập và có đủ xu hoặc thanh toán để xem giải luận");
        }
        
        TuViInterpretationResponse response = tuViInterpretationService.generateInterpretation(request);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chart/interpretation/check")
    @Operation(
        summary = "Check payment status for Tu Vi interpretation",
        description = "Check if payment has been made for a specific Tu Vi chart interpretation",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Payment status returned"
            )
        }
    )
    public ResponseEntity<PaymentCheckResponse> checkInterpretationPayment(
            @Parameter(description = "Chart hash", required = true)
            @RequestParam String chartHash,
            HttpServletRequest httpRequest) {
        
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);
        if (fingerprintId == null) {
            throw new IllegalStateException("Fingerprint ID not found");
        }
        
        PaymentCheckResponse response = paymentService.checkPaymentEligibility(
                fingerprintId, "TUVI_INTERPRETATION", chartHash);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart/interpretation")
    @Operation(
        summary = "Generate Tu Vi chart interpretation (GET)",
        description = "Generate detailed AI-powered interpretation using query parameters. " +
                     "Alternative to POST endpoint. Payment is required.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Interpretation generated successfully",
                content = @Content(schema = @Schema(implementation = TuViInterpretationResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "402",
                description = "Payment required"
            )
        }
    )
    public ResponseEntity<TuViInterpretationResponse> generateInterpretationGet(
            @Parameter(description = "Birth date (yyyy-MM-dd)", example = "1995-03-02", required = true)
            @RequestParam String date,
            
            @Parameter(description = "Birth hour (0-23)", example = "8", required = true)
            @RequestParam Integer hour,
            
            @Parameter(description = "Birth minute (0-59)", example = "30")
            @RequestParam(defaultValue = "0") Integer minute,
            
            @Parameter(description = "Gender (male/female)", example = "female", required = true)
            @RequestParam String gender,
            
            @Parameter(description = "Is lunar date", example = "false")
            @RequestParam(defaultValue = "false") Boolean isLunar,
            
            @Parameter(description = "Is leap month (only for lunar dates)")
            @RequestParam(defaultValue = "false") Boolean isLeapMonth,
            
            @Parameter(description = "Name (required for interpretation)")
            @RequestParam String name,
            
            @Parameter(description = "Birth place (optional)")
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
        
        return generateInterpretation(request, httpRequest);
    }
}

package com.duong.lichvanien.tuvi.controller;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.service.TuViAudioService;
import com.duong.lichvanien.tuvi.service.TuViChartService;
import com.duong.lichvanien.tuvi.service.TuViGrokInterpretationService;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * REST Controller for Tu Vi interpretation audio (text-to-speech).
 * Provides endpoints to generate MP3 audio from interpretation text.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tuvi/audio")
@RequiredArgsConstructor
@Tag(name = "Tu Vi Audio", description = "APIs for generating audio from Tu Vi interpretations")
public class TuViAudioController {

    private final TuViAudioService audioService;
    private final TuViGrokInterpretationService interpretationService;
    private final TuViChartService chartService;

    /**
     * Generate audio for a specific palace from Tu Vi interpretation (POST).
     * Requires xu payment or previous payment (same as full interpretation).
     *
     * @param palaceCode Palace code (OVERVIEW, MENH, QUAN_LOC, etc.)
     * @param request    Chart request
     * @param httpRequest HTTP request for fingerprint/user context
     * @return MP3 audio stream
     */
    @PostMapping("/{palaceCode}")
    @Operation(
        summary = "Generate audio for palace interpretation (POST)",
        description = "Generate MP3 audio from interpretation text for a specific palace. " +
                     "Requires xu payment (same as full interpretation). " +
                     "Palace codes: OVERVIEW, MENH, QUAN_LOC, TAI_BACH, PHU_THE, TAT_ACH, " +
                     "TU_TUC, DIEN_TRACH, PHU_MAU, HUYNH_DE, PHUC_DUC, NO_BOC, THIEN_DI",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Audio generated successfully (MP3 stream)"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters or palace code"
            ),
            @ApiResponse(
                responseCode = "402",
                description = "Payment required - not enough xu"
            ),
            @ApiResponse(
                responseCode = "503",
                description = "TTS service unavailable"
            )
        }
    )
    public ResponseEntity<InputStream> generateAudioPost(
            @Parameter(description = "Palace code", required = true, example = "MENH")
            @PathVariable String palaceCode,
            @Valid @RequestBody TuViChartRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received audio generation request for palace: {} for: {}", palaceCode, request.getName());

        // Check if TTS is available
        if (!audioService.isAvailable()) {
            log.error("TTS service is not available");
            throw new RuntimeException("Dịch vụ TTS không khả dụng. Vui lòng thử lại sau.");
        }

        // Check access (same as full interpretation)
        checkAccess(request, httpRequest);

        // Generate audio
        InputStream audioStream = audioService.generateAudioForPalace(request, palaceCode);

        // Return audio stream with proper headers
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tuvi-" + palaceCode.toLowerCase() + ".mp3\"")
                .body(audioStream);
    }

    /**
     * Generate audio for a specific palace from Tu Vi interpretation (GET).
     * Alternative endpoint using query parameters.
     *
     * @param palaceCode Palace code
     * @param date       Birth date (yyyy-MM-dd)
     * @param hour       Birth hour (0-23)
     * @param minute     Birth minute (0-59)
     * @param gender     Gender (male/female)
     * @param isLunar    Is lunar date
     * @param isLeapMonth Is leap month
     * @param name       Name
     * @param birthPlace Birth place
     * @param httpRequest HTTP request
     * @return MP3 audio stream
     */
    @GetMapping("/{palaceCode}")
    @Operation(
        summary = "Generate audio for palace interpretation (GET)",
        description = "Alternative GET endpoint for audio generation using query parameters"
    )
    public ResponseEntity<InputStream> generateAudioGet(
            @Parameter(description = "Palace code", required = true)
            @PathVariable String palaceCode,
            
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
        
        return generateAudioPost(palaceCode, request, httpRequest);
    }

    /**
     * Check access to audio (same logic as full interpretation).
     */
    private void checkAccess(TuViChartRequest request, HttpServletRequest httpRequest) {
        // Block anonymous users
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);
        if (userId == null) {
            log.warn("Anonymous user attempted to access audio");
            throw new IllegalArgumentException(
                    "Cần đăng ký tài khoản và có đủ xu để nghe audio giải luận. " +
                    "Bạn có thể xem Đại hạn/Tiểu vận miễn phí (3 lần/ngày) mà không cần đăng ký."
            );
        }

        // Generate chart to get chart hash
        TuViChartResponse chart = chartService.generateChart(request);
        String chartHash = chart.getChartHash();
        String fingerprintId = FingerprintInterceptor.getFingerprintId(httpRequest);

        // Check access
        boolean hasAccess = interpretationService.checkFullInterpretationAccess(userId, fingerprintId, chartHash);

        if (!hasAccess) {
            // Try to deduct xu
            boolean deducted = interpretationService.deductXuForFullInterpretation(userId, chartHash);
            if (!deducted) {
                Integer priceXu = interpretationService.getFullInterpretationPriceXu();
                throw new IllegalArgumentException(
                        "Không đủ xu để nghe audio giải luận. Cần " + priceXu + " xu. " +
                        "Vui lòng nạp xu để tiếp tục."
                );
            }
        }
    }
}


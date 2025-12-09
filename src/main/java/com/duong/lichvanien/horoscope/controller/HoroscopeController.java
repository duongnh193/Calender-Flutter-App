package com.duong.lichvanien.horoscope.controller;

import com.duong.lichvanien.common.response.ErrorResponse;
import com.duong.lichvanien.horoscope.dto.*;
import com.duong.lichvanien.horoscope.service.CanChiService;
import com.duong.lichvanien.horoscope.service.HoroscopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/horoscope")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Horoscope", description = "Horoscope predictions API - daily, monthly, yearly, and lifetime")
public class HoroscopeController {

    private final HoroscopeService horoscopeService;
    private final CanChiService canChiService;

    // ==================== LIFETIME HOROSCOPE ====================

    @GetMapping("/lifetime")
    @Operation(
            summary = "Get lifetime horoscope",
            description = "Returns lifetime horoscope predictions based on Can-Chi combination and gender. " +
                    "Can-Chi should be in format like 'Giáp Tý', 'Ất Sửu', etc."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lifetime horoscope found",
                    content = @Content(schema = @Schema(implementation = HoroscopeLifetimeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horoscope not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public HoroscopeLifetimeResponse getLifetime(
            @Parameter(description = "Can-Chi combination (e.g., 'Giáp Tý', 'Giap Ty')", required = true, example = "Giáp Tý")
            @RequestParam String canChi,

            @Parameter(description = "Gender (male or female)", required = true, example = "male")
            @RequestParam String gender
    ) {
        log.debug("GET /lifetime - canChi={}, gender={}", canChi, gender);
        return horoscopeService.getLifetime(canChi, gender);
    }

    // ==================== YEARLY HOROSCOPE ====================

    @GetMapping("/yearly")
    @Operation(
            summary = "Get yearly horoscope",
            description = "Returns yearly horoscope predictions for a specific zodiac and year. " +
                    "Provide either zodiacId or zodiacCode."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Yearly horoscope found",
                    content = @Content(schema = @Schema(implementation = HoroscopeYearlyResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horoscope not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public HoroscopeYearlyResponse getYearly(
            @Parameter(description = "Zodiac ID (1-12)", example = "1")
            @RequestParam(required = false) Long zodiacId,

            @Parameter(description = "Zodiac code (ti, suu, dan, mao, thin, ty, ngo, mui, than, dau, tuat, hoi)", example = "ti")
            @RequestParam(required = false) String zodiacCode,

            @Parameter(description = "Year (1900-2100)", required = true, example = "2025")
            @RequestParam @Min(1900) @Max(2100) int year
    ) {
        log.debug("GET /yearly - zodiacId={}, zodiacCode={}, year={}", zodiacId, zodiacCode, year);
        return horoscopeService.getYearly(zodiacId, zodiacCode, year);
    }

    // ==================== MONTHLY HOROSCOPE ====================

    @GetMapping("/monthly")
    @Operation(
            summary = "Get monthly horoscope",
            description = "Returns monthly horoscope predictions for a specific zodiac, year, and month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monthly horoscope found",
                    content = @Content(schema = @Schema(implementation = HoroscopeMonthlyResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horoscope not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public HoroscopeMonthlyResponse getMonthly(
            @Parameter(description = "Zodiac ID (1-12)", example = "1")
            @RequestParam(required = false) Long zodiacId,

            @Parameter(description = "Zodiac code", example = "ti")
            @RequestParam(required = false) String zodiacCode,

            @Parameter(description = "Year (1900-2100)", required = true, example = "2025")
            @RequestParam @Min(1900) @Max(2100) int year,

            @Parameter(description = "Month (1-12)", required = true, example = "12")
            @RequestParam @Min(1) @Max(12) int month
    ) {
        log.debug("GET /monthly - zodiacId={}, zodiacCode={}, year={}, month={}", zodiacId, zodiacCode, year, month);
        return horoscopeService.getMonthly(zodiacId, zodiacCode, year, month);
    }

    // ==================== DAILY HOROSCOPE ====================

    @GetMapping("/daily")
    @Operation(
            summary = "Get daily horoscope",
            description = "Returns daily horoscope predictions for a specific zodiac and date. " +
                    "If date is not provided, defaults to today (UTC+7 timezone)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Daily horoscope found",
                    content = @Content(schema = @Schema(implementation = HoroscopeDailyResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horoscope not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public HoroscopeDailyResponse getDaily(
            @Parameter(description = "Zodiac ID (1-12)", example = "1")
            @RequestParam(required = false) Long zodiacId,

            @Parameter(description = "Zodiac code", example = "ti")
            @RequestParam(required = false) String zodiacCode,

            @Parameter(description = "Date in ISO format (YYYY-MM-DD). Defaults to today in UTC+7.", example = "2025-12-09")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.debug("GET /daily - zodiacId={}, zodiacCode={}, date={}", zodiacId, zodiacCode, date);
        return horoscopeService.getDaily(zodiacId, zodiacCode, date);
    }

    // ==================== CAN-CHI CALCULATION ====================

    @GetMapping("/can-chi")
    @Operation(
            summary = "Calculate Can-Chi from birth date",
            description = "Calculates the Can-Chi (Thiên Can - Địa Chi) combination from a given birth date. " +
                    "This is useful for determining the Can-Chi parameter for lifetime horoscope lookup."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Can-Chi calculated successfully",
                    content = @Content(schema = @Schema(implementation = CanChiResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public CanChiResult calculateCanChi(
            @Parameter(description = "Birth date in ISO format (YYYY-MM-DD)", required = true, example = "1990-05-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate
    ) {
        log.debug("GET /can-chi - birthDate={}", birthDate);
        return canChiService.calculateFromBirthDate(birthDate);
    }
}

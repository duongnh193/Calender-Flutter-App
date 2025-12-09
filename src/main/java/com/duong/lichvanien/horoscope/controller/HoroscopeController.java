package com.duong.lichvanien.horoscope.controller;

import com.duong.lichvanien.common.response.ErrorResponse;
import com.duong.lichvanien.common.utils.LogSanitizer;
import com.duong.lichvanien.horoscope.dto.*;
import com.duong.lichvanien.horoscope.service.CanChiService;
import com.duong.lichvanien.horoscope.service.HoroscopeService;
import com.duong.lichvanien.horoscope.service.LifetimeByBirthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final LifetimeByBirthService lifetimeByBirthService;

    // ==================== LIFETIME HOROSCOPE ====================

    @GetMapping("/lifetime")
    @Operation(
            summary = "Get lifetime horoscope by Can-Chi",
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

    // ==================== LIFETIME BY BIRTH ====================

    @PostMapping("/lifetime/by-birth")
    @Operation(
            summary = "Get lifetime horoscope by birth data",
            description = """
                    Computes Can-Chi from birth date, time, and optional lunar calendar info,
                    then returns the corresponding lifetime horoscope.
                    
                    **Hour Branch Mapping (12 Canh):**
                    - Tý: 23:00 - 00:59
                    - Sửu: 01:00 - 02:59
                    - Dần: 03:00 - 04:59
                    - Mão: 05:00 - 06:59
                    - Thìn: 07:00 - 08:59
                    - Tỵ: 09:00 - 10:59
                    - Ngọ: 11:00 - 12:59
                    - Mùi: 13:00 - 14:59
                    - Thân: 15:00 - 16:59
                    - Dậu: 17:00 - 18:59
                    - Tuất: 19:00 - 20:59
                    - Hợi: 21:00 - 22:59
                    
                    **Timezone:** All dates/times are interpreted as Asia/Bangkok (UTC+7).
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lifetime horoscope computed and returned (may be exact match or fallback)",
                    content = @Content(
                            schema = @Schema(implementation = LifetimeByBirthResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Exact match",
                                            summary = "Found exact Can-Chi match in database",
                                            value = """
                                                    {
                                                      "zodiacId": 11,
                                                      "zodiacCode": "tuat",
                                                      "zodiacName": "Tuất",
                                                      "canChi": "Canh Tuất",
                                                      "gender": "male",
                                                      "hourBranch": "ti",
                                                      "hourBranchName": "Tý",
                                                      "computed": true,
                                                      "isFallback": false,
                                                      "overview": "...",
                                                      "career": "...",
                                                      "metadata": {"source": "db", "computed": true}
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Fallback",
                                            summary = "No exact match, returning zodiac-level default",
                                            value = """
                                                    {
                                                      "zodiacId": 11,
                                                      "zodiacCode": "tuat",
                                                      "zodiacName": "Tuất",
                                                      "canChi": null,
                                                      "gender": "male",
                                                      "hourBranch": "ti",
                                                      "message": "Lifetime data not found for computed Can-Chi; returning zodiac-level default.",
                                                      "computed": true,
                                                      "isFallback": true,
                                                      "overview": "..."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input (bad date format, hour/minute out of range, missing gender)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public LifetimeByBirthResponse getLifetimeByBirth(
            @Valid @RequestBody LifetimeByBirthRequest request
    ) {
        // Mask DOB in logs to prevent PII leakage
        log.info("POST /lifetime/by-birth - date={}, hour={}, minute={}, isLunar={}, gender={}",
                LogSanitizer.maskDate(request.getDate()), request.getHour(), request.getMinute(),
                request.getIsLunar(), request.getGender());
        return lifetimeByBirthService.getLifetimeByBirth(request);
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
        // Mask DOB in logs to prevent PII leakage
        log.debug("GET /can-chi - birthDate={}", LogSanitizer.maskDate(birthDate.toString()));
        return canChiService.calculateFromBirthDate(birthDate);
    }
}

package com.duong.lichvanien.tuvi.controller;

import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.service.TuViChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        summary = "Generate Tu Vi chart (GET)",
        description = "Generate a Tu Vi chart using query parameters. Alternative to POST endpoint.",
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
    public ResponseEntity<TuViChartResponse> generateChartGet(
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
            
            @Parameter(description = "Name (optional, for display)")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Birth place (optional, for display)")
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
        
        return generateChart(request);
    }
}

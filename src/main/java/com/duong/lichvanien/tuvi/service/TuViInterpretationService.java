package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.exception.TuViInterpretationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for retrieving Tu Vi chart interpretations.
 * Supports both rule-based generation (from fragments) and database lookup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViInterpretationService {

    private final TuViChartService chartService;
    private final TuViInterpretationDatabaseService databaseService;
    private final InterpretationCompositionService compositionService;

    /**
     * Get interpretation for a Tu Vi chart.
     * First tries database lookup, then falls back to rule-based generation.
     */
    public TuViInterpretationResponse getInterpretation(TuViChartRequest request) {
        log.info("Retrieving interpretation for: {}", request.getName());

        // Generate the chart first (deterministic)
        TuViChartResponse chart = chartService.generateChart(request);

        // Try database lookup first
        Optional<TuViInterpretationResponse> dbResult = databaseService.findByChartHash(
                chart, request.getGender(), request.getName());
        
        if (dbResult.isPresent()) {
            log.info("Found interpretation in database for: {}", request.getName());
            return dbResult.get();
        }

        // Not found in DB - generate from fragments (rule-based)
        log.info("Generating interpretation from fragments for: {}", request.getName());
        TuViInterpretationResponse interpretation = compositionService.generateInterpretationFromFragments(
                chart, request.getGender(), request.getName());

        // Optionally save to database for future use
        try {
            databaseService.save(chart, request.getGender(), interpretation);
            log.info("Saved generated interpretation to database");
        } catch (Exception e) {
            log.warn("Failed to save generated interpretation to database: {}", e.getMessage());
        }

        return interpretation;
    }

    /**
     * @deprecated Use getInterpretation() instead. This method name is kept for backward compatibility.
     */
    @Deprecated
    public TuViInterpretationResponse generateInterpretation(TuViChartRequest request) {
        return getInterpretation(request);
    }
}

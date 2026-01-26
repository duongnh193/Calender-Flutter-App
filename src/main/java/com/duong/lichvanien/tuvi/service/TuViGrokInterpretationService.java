package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.CycleInterpretationResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.DaiVanInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.entity.TuViCycleInterpretationEntity;
import com.duong.lichvanien.tuvi.repository.TuViCycleInterpretationRepository;
import com.duong.lichvanien.user.entity.ContentAccessEntity;
import com.duong.lichvanien.user.repository.ContentAccessRepository;
import com.duong.lichvanien.user.service.PaymentService;
import com.duong.lichvanien.xu.entity.XuTransactionEntity;
import com.duong.lichvanien.xu.enums.XuTransactionType;
import com.duong.lichvanien.xu.repository.XuTransactionRepository;
import com.duong.lichvanien.xu.service.XuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for generating Tu Vi interpretations using Grok AI.
 * Handles caching, access control, and coordination between chart generation and AI.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViGrokInterpretationService {

    private final TuViChartService chartService;
    private final GrokAIService grokAIService;
    private final TuViInterpretationDatabaseService interpretationDatabaseService;
    private final TuViCycleInterpretationRepository cycleInterpretationRepository;
    private final XuService xuService;
    private final XuTransactionRepository xuTransactionRepository;
    private final AffiliateService affiliateService;
    private final PaymentService paymentService;
    private final ContentAccessRepository contentAccessRepository;
    private final ObjectMapper objectMapper;

    /**
     * Maximum number of free cycle interpretations allowed for anonymous users per fingerprint.
     */
    private static final int MAX_CYCLE_INTERPRETATION_ANONYMOUS = 3;
    
    /**
     * Content type for cycle interpretation tracking.
     */
    private static final String CONTENT_TYPE_CYCLE_INTERPRETATION = "TUVI_CYCLE_INTERPRETATION";

    /**
     * Generate full Tu Vi interpretation.
     * First checks cache, then calls Grok API if not cached.
     *
     * @param request Chart request
     * @return Full interpretation response
     */
    @Transactional
    public TuViInterpretationResponse generateFullInterpretation(TuViChartRequest request) {
        log.info("Generating full interpretation for: {}", request.getName());

        // Generate chart first to get chart hash
        TuViChartResponse chart = chartService.generateChart(request);
        String chartHash = chart.getChartHash();

        // Check cache in database
        Optional<TuViInterpretationResponse> cachedResult = interpretationDatabaseService.findByChartHash(
                chart, request.getGender(), request.getName());

        if (cachedResult.isPresent()) {
            log.info("Found cached full interpretation for chart hash: {}", chartHash);
            return cachedResult.get();
        }

        // Not in cache - call Grok API
        log.info("Calling Grok API for full interpretation, chart hash: {}", chartHash);
        TuViInterpretationResponse response = grokAIService.generateFullInterpretation(
                chart, request.getName(), request.getGender());

        if (response == null) {
            log.error("Failed to generate full interpretation from Grok API");
            throw new RuntimeException("Không thể tạo giải luận. Vui lòng thử lại sau.");
        }

        // Save to cache
        try {
            interpretationDatabaseService.save(chart, request.getGender(), response);
            log.info("Saved full interpretation to cache for chart hash: {}", chartHash);
        } catch (Exception e) {
            log.warn("Failed to save full interpretation to cache: {}", e.getMessage());
            // Continue - still return the response
        }

        return response;
    }

    /**
     * Generate cycle (Đại hạn/Tiểu vận) interpretation.
     * This is FREE content - no payment required.
     * For anonymous users, tracks usage per fingerprint (max 3 times).
     * Uses double-check pattern to prevent duplicate API calls in race conditions.
     *
     * @param request Chart request
     * @param fingerprintId Fingerprint ID (nullable for authenticated users)
     * @return Cycle interpretation response with usage info
     */
    @Transactional
    public CycleInterpretationResponse generateCycleInterpretation(TuViChartRequest request, String fingerprintId) {
        log.info("Generating cycle interpretation for: {}, fingerprintId: {}", request.getName(), fingerprintId);

        // Note: Daily limit check is now done in controller using AccessLogService
        // AccessLogInterceptor will automatically log this request to access_log

        // Generate chart first to get chart hash and cycle info
        TuViChartResponse chart = chartService.generateChart(request);
        String chartHash = chart.getChartHash();
        TuViCycleInterpretationEntity.Gender gender = 
                TuViCycleInterpretationEntity.Gender.valueOf(request.getGender().toLowerCase());

        // First check: Look for cached interpretation
        Optional<TuViCycleInterpretationEntity> cached = 
                cycleInterpretationRepository.findByChartHashAndGender(chartHash, gender);

        CycleInterpretationResponse response;
        if (cached.isPresent()) {
            log.info("Found cached cycle interpretation for chart hash: {}", chartHash);
            response = convertEntityToResponse(cached.get(), chart);
        } else {
            // Not in cache - call Grok API
            // Note: In race conditions, multiple requests might reach here simultaneously
            log.info("Calling Grok API for cycle interpretation, chart hash: {}", chartHash);
            response = grokAIService.generateCycleInterpretation(
                    chart, request.getName(), request.getGender());

            if (response == null) {
                log.error("Failed to generate cycle interpretation from Grok API");
                throw new RuntimeException("Không thể tạo giải luận đại vận. Vui lòng thử lại sau.");
            }

            // Second check: Verify cache again AFTER API call but BEFORE save
            // This prevents duplicate API calls when multiple requests race to generate the same interpretation
            Optional<TuViCycleInterpretationEntity> cachedAfterApi = 
                    cycleInterpretationRepository.findByChartHashAndGender(chartHash, gender);
            
            if (cachedAfterApi.isPresent()) {
                // Another request already saved it while we were calling API
                // Use the cached version instead of saving duplicate
                log.info("Found cached cycle interpretation after API call (race condition detected), " +
                        "using cached version for chart hash: {}", chartHash);
                response = convertEntityToResponse(cachedAfterApi.get(), chart);
            } else {
                // Still not in cache - safe to save
                // Note: saveCycleInterpretationToCache uses upsert logic to handle any remaining race conditions
                try {
                    saveCycleInterpretationToCache(response, chart, request);
                    log.info("Saved cycle interpretation to cache for chart hash: {}", chartHash);
                } catch (Exception e) {
                    log.warn("Failed to save cycle interpretation to cache: {}", e.getMessage());
                    // Continue - still return the response (it was successfully generated)
                }
            }
        }

        // Note: Usage tracking is now done via access_log table (logged by AccessLogInterceptor)
        // Usage info can be retrieved from AccessLogService if needed in response
        // For now, we don't set usage info in response as it's handled by controller

        return response;
    }

    /**
     * Check if user has access to full interpretation.
     * Returns true if user has already paid (via xu transaction) or has old payment.
     *
     * @param userId       User ID (nullable for anonymous)
     * @param fingerprintId Fingerprint ID
     * @param chartHash    Chart hash
     * @return true if user has access
     */
    public boolean checkFullInterpretationAccess(Long userId, String fingerprintId, String chartHash) {
        // Check if user has already paid for this chartHash (via xu transaction)
        if (userId != null && chartHash != null) {
            List<XuTransactionEntity> existingTransactions = 
                    xuTransactionRepository.findByReferenceId(chartHash);
            
            // Check if there's a PURCHASE transaction for this user and chartHash
            boolean hasPaid = existingTransactions.stream()
                    .anyMatch(t -> t.getUser().getId().equals(userId) 
                            && t.getTransactionType() == XuTransactionType.PURCHASE
                            && t.getReferenceId() != null 
                            && t.getReferenceId().equals(chartHash));
            
            if (hasPaid) {
                log.info("User {} already paid for chartHash {}", userId, chartHash);
                return true;
            }
        }

        // Check old payment system (backward compatibility)
        if (fingerprintId != null && chartHash != null) {
            try {
                paymentService.verifyPaymentOrThrow(fingerprintId, "TUVI_INTERPRETATION_FULL", chartHash);
                return true;
            } catch (Exception e) {
                // No access via payment
            }
        }

        return false;
    }

    /**
     * Deduct xu for full interpretation access.
     * Checks if user has already paid for this chartHash to avoid double deduction.
     *
     * @param userId    User ID
     * @param chartHash Chart hash
     * @return true if deduction successful
     */
    @Transactional
    public boolean deductXuForFullInterpretation(Long userId, String chartHash) {
        if (userId == null || chartHash == null) {
            return false;
        }

        // Check if user has already paid for this chartHash
        List<XuTransactionEntity> existingTransactions = 
                xuTransactionRepository.findByReferenceId(chartHash);
        
        boolean alreadyPaid = existingTransactions.stream()
                .anyMatch(t -> t.getUser().getId().equals(userId) 
                        && t.getTransactionType() == XuTransactionType.PURCHASE
                        && t.getReferenceId() != null 
                        && t.getReferenceId().equals(chartHash));
        
        if (alreadyPaid) {
            log.info("User {} already paid for chartHash {}, skipping deduction", userId, chartHash);
            return true; // Already paid, consider as success
        }

        Integer priceXu = affiliateService.getInterpretationPriceXu();
        if (!xuService.hasEnoughXu(userId, priceXu)) {
            log.warn("User {} does not have enough xu (required: {}, available: {})", 
                    userId, priceXu, xuService.getBalance(userId));
            return false;
        }

        xuService.deductXu(userId, priceXu, chartHash, "Mua giải luận Tử Vi Full - " + chartHash);
        log.info("User {} purchased full interpretation with {} xu for chart {}", userId, priceXu, chartHash);
        return true;
    }

    /**
     * Get the price in xu for full interpretation.
     */
    public Integer getFullInterpretationPriceXu() {
        return affiliateService.getInterpretationPriceXu();
    }

    /**
     * Check if Grok AI service is available.
     */
    public boolean isGrokAvailable() {
        return grokAIService.isAvailable();
    }

    /**
     * Save cycle interpretation to cache.
     * Uses upsert logic: update if exists, insert if not.
     * This prevents duplicate key errors in race conditions.
     */
    private void saveCycleInterpretationToCache(
            CycleInterpretationResponse response, 
            TuViChartResponse chart,
            TuViChartRequest request) throws JsonProcessingException {
        
        String chartHash = chart.getChartHash();
        TuViCycleInterpretationEntity.Gender gender = 
                TuViCycleInterpretationEntity.Gender.valueOf(request.getGender().toLowerCase());
        
        // Check if already exists (handle race condition)
        Optional<TuViCycleInterpretationEntity> existingOpt = 
                cycleInterpretationRepository.findByChartHashAndGender(chartHash, gender);
        
        TuViCycleInterpretationEntity entity;
        if (existingOpt.isPresent()) {
            // Update existing entity
            entity = existingOpt.get();
            log.debug("Updating existing cycle interpretation for chart hash: {}, gender: {}", chartHash, gender);
        } else {
            // Create new entity
            entity = new TuViCycleInterpretationEntity();
            entity.setChartHash(chartHash);
            entity.setGender(gender);
            entity.setGeneratedAt(LocalDateTime.now());
            log.debug("Creating new cycle interpretation for chart hash: {}, gender: {}", chartHash, gender);
        }
        
        // Update fields (both for new and existing)
        entity.setName(request.getName());
        
        if (request.getDate() != null) {
            entity.setBirthDate(LocalDate.parse(request.getDate()));
        }
        
        // Store interpretation data as JSON
        Map<String, Object> interpretationData = Map.of(
                "introduction", response.getIntroduction() != null ? response.getIntroduction() : "",
                "overallCycleSummary", response.getOverallCycleSummary() != null ? response.getOverallCycleSummary() : "",
                "daiVanInterpretations", response.getDaiVanInterpretations() != null ? response.getDaiVanInterpretations() : List.of(),
                "generalAdvice", response.getGeneralAdvice() != null ? response.getGeneralAdvice() : ""
        );
        entity.setCycleInterpretationData(objectMapper.writeValueAsString(interpretationData));
        
        entity.setAiModel(response.getAiModel());
        entity.setUpdatedAt(LocalDateTime.now());
        
        cycleInterpretationRepository.save(entity);
    }

    /**
     * Convert entity to response DTO.
     */
    private CycleInterpretationResponse convertEntityToResponse(
            TuViCycleInterpretationEntity entity, TuViChartResponse chart) {
        
        try {
            // Parse JSON data
            Map<String, Object> data = objectMapper.readValue(
                    entity.getCycleInterpretationData(), 
                    new TypeReference<Map<String, Object>>() {});
            
            // Parse daiVanInterpretations
            List<DaiVanInterpretation> daiVanInterpretations = null;
            if (data.get("daiVanInterpretations") != null) {
                daiVanInterpretations = objectMapper.convertValue(
                        data.get("daiVanInterpretations"),
                        new TypeReference<List<DaiVanInterpretation>>() {});
            }

            return CycleInterpretationResponse.builder()
                    .chartHash(entity.getChartHash())
                    .name(entity.getName())
                    .gender(entity.getGender().name())
                    .birthDate(entity.getBirthDate() != null ? entity.getBirthDate().toString() : null)
                    .lunarYearCanChi(chart.getCenter().getLunarYearCanChi())
                    .cycleInfo(chart.getCycles())
                    .introduction((String) data.get("introduction"))
                    .overallCycleSummary((String) data.get("overallCycleSummary"))
                    .daiVanInterpretations(daiVanInterpretations)
                    .generalAdvice((String) data.get("generalAdvice"))
                    .generatedAt(entity.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .aiModel(entity.getAiModel())
                    .fromCache(true)
                    .build();

        } catch (Exception e) {
            log.error("Error converting cycle interpretation entity to response: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi đọc dữ liệu cache", e);
        }
    }

    /**
     * Get cycle interpretation usage count for a fingerprint.
     * Uses ContentAccessEntity to track usage per fingerprint.
     *
     * @param fingerprintId Fingerprint ID
     * @return Usage count (0 if never used)
     */
    private int getCycleInterpretationUsageCount(String fingerprintId) {
        Optional<ContentAccessEntity> access = contentAccessRepository
                .findByFingerprintIdAndContentTypeAndContentId(
                        fingerprintId, CONTENT_TYPE_CYCLE_INTERPRETATION, fingerprintId);
        
        if (access.isPresent() && access.get().getIsActive()) {
            return access.get().getAccessCount();
        }
        
        return 0;
    }

    /**
     * Track cycle interpretation usage for a fingerprint.
     * Creates or updates ContentAccessEntity to record usage.
     *
     * @param fingerprintId Fingerprint ID
     */
    @Transactional
    private void trackCycleInterpretationUsage(String fingerprintId) {
        Optional<ContentAccessEntity> existing = contentAccessRepository
                .findByFingerprintIdAndContentTypeAndContentId(
                        fingerprintId, CONTENT_TYPE_CYCLE_INTERPRETATION, fingerprintId);
        
        if (existing.isPresent()) {
            // Update existing access record
            ContentAccessEntity access = existing.get();
            access.recordAccess();
            contentAccessRepository.save(access);
            log.debug("Incremented cycle interpretation usage for fingerprint: {}, count: {}", 
                    fingerprintId, access.getAccessCount());
        } else {
            // Create new access record
            ContentAccessEntity access = ContentAccessEntity.builder()
                    .fingerprintId(fingerprintId)
                    .contentType(CONTENT_TYPE_CYCLE_INTERPRETATION)
                    .contentId(fingerprintId) // Use fingerprintId as contentId for tracking
                    .accessCount(1)
                    .lastAccessedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();
            contentAccessRepository.save(access);
            log.debug("Created new cycle interpretation usage record for fingerprint: {}", fingerprintId);
        }
    }
}


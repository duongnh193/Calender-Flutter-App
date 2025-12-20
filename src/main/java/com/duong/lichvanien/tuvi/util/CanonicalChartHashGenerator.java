package com.duong.lichvanien.tuvi.util;

import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.enums.Star;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Canonical chart hash generator for Tu Vi charts.
 * Generates deterministic SHA-256 hash from ALL FACT data that affects interpretation.
 * 
 * Key principles:
 * 1. Keys are sorted alphabetically (TreeMap)
 * 2. Arrays are sorted deterministically
 * 3. Includes ALL FACT elements (truongSinhStage, daiVanStartAge, starOrder, etc.)
 */
@Slf4j
public class CanonicalChartHashGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    /**
     * Generate canonical hash from chart response.
     * This hash includes ALL FACT data that affects interpretation.
     *
     * @param chart The chart response
     * @param gender Gender string ("male" or "female")
     * @param isLunar Whether input was lunar calendar
     * @return SHA-256 hash string
     */
    public static String generateCanonicalHash(TuViChartResponse chart, String gender, boolean isLunar) {
        try {
            Map<String, Object> canonical = buildCanonicalStructure(chart, gender, isLunar);
            String json = objectMapper.writeValueAsString(canonical);
            String hash = sha256(json);
            log.debug("Generated canonical hash: {} (JSON length: {})", hash, json.length());
            return hash;
        } catch (Exception e) {
            log.error("Error generating canonical chart hash: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate canonical chart hash", e);
        }
    }

    /**
     * Build canonical JSON structure with all FACT data.
     * Keys are sorted alphabetically, arrays are sorted deterministically.
     */
    private static Map<String, Object> buildCanonicalStructure(
            TuViChartResponse chart, String gender, boolean isLunar) {
        
        Map<String, Object> data = new TreeMap<>();  // Sorted keys
        
        CenterInfo center = chart.getCenter();
        MarkerInfo markers = chart.getMarkers();
        
        // Birth data (alphabetically sorted)
        data.put("amDuong", center.getAmDuong());
        data.put("banMenh", center.getBanMenh());
        data.put("banMenhNguHanh", center.getBanMenhNguHanh());
        data.put("birthHour", center.getBirthHour());
        data.put("birthHourCanChi", center.getBirthHourCanChi());
        data.put("birthMinute", center.getBirthMinute());
        data.put("cucName", center.getCuc());
        data.put("cucNguHanh", center.getCucNguHanh());
        data.put("cucValue", center.getCucValue());
        data.put("chuMenhStarCode", extractStarCode(center.getChuMenh()));
        data.put("chuThanStarCode", extractStarCode(center.getChuThan()));
        data.put("gender", gender);
        data.put("hourBranchIndex", center.getHourBranchIndex());
        data.put("isLeapMonth", center.isLeapMonth());
        data.put("isLunar", isLunar);
        data.put("lunarDay", center.getLunarDay());
        data.put("lunarDayCanChi", center.getLunarDayCanChi());
        data.put("lunarMonth", center.getLunarMonth());
        data.put("lunarMonthCanChi", center.getLunarMonthCanChi());
        data.put("lunarYear", center.getLunarYear());
        data.put("lunarYearCanChi", center.getLunarYearCanChi());
        data.put("thanCuPalaceCode", extractPalaceCode(center.getThanCu()));
        data.put("thuanNghich", center.getThuanNghich());
        data.put("trietEndChi", markers.getTrietEnd());
        data.put("trietStartChi", markers.getTrietStart());
        data.put("tuanEndChi", markers.getTuanEnd());
        data.put("tuanStartChi", markers.getTuanStart());
        
        // Palaces (sorted by index)
        List<Map<String, Object>> palaces = chart.getPalaces().stream()
                .sorted(Comparator.comparing(PalaceInfo::getIndex))
                .map(p -> buildPalaceCanonical(p))
                .collect(Collectors.toList());
        data.put("palaces", palaces);
        
        return data;
    }

    /**
     * Build canonical structure for a single palace.
     */
    private static Map<String, Object> buildPalaceCanonical(PalaceInfo palace) {
        Map<String, Object> p = new TreeMap<>();
        
        p.put("canChiPrefix", palace.getCanChiPrefix());
        p.put("daiVanLabel", palace.getDaiVanLabel());
        p.put("daiVanStartAge", palace.getDaiVanStartAge());
        p.put("diaChi", palace.getDiaChiCode());
        p.put("hasTriet", palace.isHasTriet());
        p.put("hasTuan", palace.isHasTuan());
        p.put("isThanCu", palace.isThanCu());
        p.put("nameCode", palace.getNameCode());
        p.put("palaceIndex", palace.getIndex());
        p.put("truongSinhStage", palace.getTruongSinhStage());
        
        // Stars (sorted by order: Chính tinh first, then Phụ tinh, then Bàng tinh)
        // Within same type, order by code
        List<Map<String, Object>> stars = (palace.getStars() != null && !palace.getStars().isEmpty())
            ? palace.getStars().stream()
                .sorted(Comparator
                    .comparing((StarInfo s) -> getTypeOrder(s.getType()))
                    .thenComparing(StarInfo::getCode))
                .map(star -> buildStarCanonical(star, palace))
                .collect(Collectors.toList())
            : Collections.emptyList();
        p.put("stars", stars);
        
        return p;
    }

    /**
     * Build canonical structure for a single star.
     */
    private static Map<String, Object> buildStarCanonical(
            StarInfo star, PalaceInfo palace) {
        Map<String, Object> s = new TreeMap<>();
        
        s.put("brightness", star.getBrightness());
        s.put("brightnessCode", star.getBrightnessCode());
        s.put("isPositive", star.getIsPositive());
        s.put("nguHanh", star.getNguHanh());
        s.put("starCode", star.getCode());
        s.put("starOrder", calculateStarOrder(star, palace));  // Calculate actual order
        s.put("type", star.getType());
        
        return s;
    }

    /**
     * Calculate star order in palace (0 = first, 1 = second, etc.).
     * Order: Chính tinh first (by code), then Phụ tinh, then Bàng tinh.
     */
    private static int calculateStarOrder(StarInfo star, PalaceInfo palace) {
        if (palace.getStars() == null || palace.getStars().isEmpty()) {
            return 0;
        }
        
        // Sort stars: type order first, then code
        List<StarInfo> sorted = new ArrayList<>(palace.getStars());
        sorted.sort(Comparator
            .comparing((StarInfo s) -> getTypeOrder(s.getType()))
            .thenComparing(StarInfo::getCode));
        
        return sorted.indexOf(star);
    }

    /**
     * Get type order for sorting (lower = higher priority).
     */
    private static int getTypeOrder(String type) {
        if (type == null) return 99;
        return switch (type) {
            case "CHINH_TINH" -> 0;
            case "PHU_TINH" -> 1;
            case "BANG_TINH" -> 2;
            default -> 99;
        };
    }

    /**
     * Extract star code from star name (e.g., "Cự Môn" -> "CU_MON").
     * Uses Star enum lookup by Vietnamese name.
     */
    private static String extractStarCode(String starName) {
        if (starName == null || starName.isBlank()) {
            return null;
        }
        
        // Try to find matching Star enum by Vietnamese name
        for (Star star : Star.values()) {
            if (star.getText().equals(starName)) {
                return star.name();
            }
        }
        
        // Fallback: return as-is if not found (should not happen in production)
        log.warn("Could not find star code for name: {}", starName);
        return starName;
    }

    /**
     * Extract palace code from palace name (e.g., "Tài Bạch" -> "TAI_BACH").
     * Uses CungName enum lookup by Vietnamese name.
     */
    private static String extractPalaceCode(String palaceName) {
        if (palaceName == null || palaceName.isBlank()) {
            return null;
        }
        
        // Try to find matching CungName enum by Vietnamese name
        for (com.duong.lichvanien.tuvi.enums.CungName cung : com.duong.lichvanien.tuvi.enums.CungName.values()) {
            if (cung.getText().equals(palaceName)) {
                return cung.name();
            }
        }
        
        // Fallback: return as-is if not found
        log.warn("Could not find palace code for name: {}", palaceName);
        return palaceName;
    }

    /**
     * Generate SHA-256 hash from string.
     */
    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * Convert byte array to hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }
}

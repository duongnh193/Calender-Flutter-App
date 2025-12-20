package com.duong.lichvanien.tuvi.util;

import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to generate hash keys for Tu Vi chart caching.
 * Generates deterministic hash based on chart structure (stars, positions, etc.)
 */
@Slf4j
public class ChartHashGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate a unique hash for a chart based on its structure.
     * This hash is used as cache key and is deterministic for the same chart.
     *
     * @param chart The chart response
     * @param gender Gender for interpretation context
     * @return SHA-256 hash string
     */
    public static String generateChartHash(TuViChartResponse chart, String gender) {
        try {
            // Build a simplified representation of the chart
            Map<String, Object> chartData = new HashMap<>();
            
            // Center info (birth data)
            chartData.put("lunarYearCanChi", chart.getCenter().getLunarYearCanChi());
            chartData.put("lunarYear", chart.getCenter().getLunarYear());
            chartData.put("lunarMonth", chart.getCenter().getLunarMonth());
            chartData.put("lunarDay", chart.getCenter().getLunarDay());
            chartData.put("birthHour", chart.getCenter().getBirthHour());
            chartData.put("birthMinute", chart.getCenter().getBirthMinute());
            chartData.put("gender", gender);
            chartData.put("banMenh", chart.getCenter().getBanMenh());
            chartData.put("banMenhNguHanh", chart.getCenter().getBanMenhNguHanh());
            chartData.put("cuc", chart.getCenter().getCuc());
            chartData.put("cucValue", chart.getCenter().getCucValue());
            chartData.put("thuanNghich", chart.getCenter().getThuanNghich());
            chartData.put("chuMenh", chart.getCenter().getChuMenh());
            chartData.put("chuThan", chart.getCenter().getChuThan());
            chartData.put("thanCu", chart.getCenter().getThanCu());

            // Palace structure (stars and positions)
            List<Map<String, Object>> palacesData = chart.getPalaces().stream()
                    .map(palace -> {
                        Map<String, Object> palaceData = new HashMap<>();
                        palaceData.put("nameCode", palace.getNameCode());
                        palaceData.put("diaChi", palace.getDiaChi());
                        palaceData.put("hasTuan", palace.isHasTuan());
                        palaceData.put("hasTriet", palace.isHasTriet());
                        palaceData.put("isThanCu", palace.isThanCu());
                        
                        // Stars in palace (simplified - just codes and brightness)
                        if (palace.getStars() != null) {
                            List<Map<String, Object>> starsData = palace.getStars().stream()
                                    .map(star -> {
                                        Map<String, Object> starData = new HashMap<>();
                                        starData.put("code", star.getCode());
                                        starData.put("type", star.getType());
                                        starData.put("brightness", star.getBrightness());
                                        return starData;
                                    })
                                    .collect(Collectors.toList());
                            palaceData.put("stars", starsData);
                        }
                        return palaceData;
                    })
                    .collect(Collectors.toList());
            
            chartData.put("palaces", palacesData);

            // Convert to JSON string
            String jsonString = objectMapper.writeValueAsString(chartData);

            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(jsonString.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("Error generating chart hash: {}", e.getMessage(), e);
            // Fallback: use timestamp-based hash (less ideal but functional)
            return String.valueOf(System.currentTimeMillis());
        }
    }

    /**
     * Generate hash for a specific section (overview or palace).
     * Used for partial caching.
     *
     * @param chartHash Base chart hash
     * @param sectionType Type of section: "overview", "MENH", "QUAN_LOC", etc.
     * @return Combined hash string
     */
    public static String generateSectionHash(String chartHash, String sectionType) {
        return chartHash + ":" + sectionType;
    }
}

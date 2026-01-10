package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.calculator.*;
import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service for generating Tu Vi (Purple Star Astrology) charts.
 * Orchestrates all calculators to produce a complete chart.
 * Also saves FACT data to database for interpretation lookup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViChartService {

    private final NatalChartService natalChartService;

    /**
     * Generate a complete Tu Vi chart from the request.
     */
    public TuViChartResponse generateChart(TuViChartRequest request) {
        log.info("Generating Tu Vi chart for date={}, hour={}", request.getDate(), request.getHour());

        // Step 1: Parse and validate input
        LocalDate solarDate = LocalDate.parse(request.getDate());
        int hour = request.getHour();
        int minute = request.getMinute();
        boolean isMale = "male".equalsIgnoreCase(request.getGender());

        // Step 2: Convert to lunar date and calculate Can-Chi
        LunarDateCalculator.LunarDate lunarDate;
        if (request.getIsLunar()) {
            // Input is already lunar - convert to solar first for calculations
            LocalDate solar = LunarDateCalculator.convertToSolar(
                solarDate.getDayOfMonth(),
                solarDate.getMonthValue(),
                solarDate.getYear(),
                request.getIsLeapMonth()
            );
            lunarDate = LunarDateCalculator.convertToLunar(
                solar.getYear(), solar.getMonthValue(), solar.getDayOfMonth(), hour, minute
            );
        } else {
            lunarDate = LunarDateCalculator.convertToLunar(
                solarDate.getYear(), solarDate.getMonthValue(), solarDate.getDayOfMonth(), hour, minute
            );
        }

        // Step 3: Get year Âm/Dương
        AmDuong yearAmDuong = lunarDate.getCanYear().getAmDuong();

        // Step 4: Calculate palace layout
        PalaceCalculator.PalaceLayout palaceLayout = PalaceCalculator.calculate(
            lunarDate.getMonth(),
            lunarDate.getHourBranchIndex(),
            lunarDate.getCanYear(),
            isMale,
            yearAmDuong
        );

        // Step 5: Calculate Nạp Âm (element)
        NapAmCalculator.NapAm napAm = NapAmCalculator.getNapAm(
            lunarDate.getCanYear(), lunarDate.getChiYear()
        );

        // Step 6: Place stars
        Map<Star, DiaChi> allStarPositions = new HashMap<>();
        
        // Place Tử Vi group
        Map<Star, DiaChi> tuViStars = TuViStarCalculator.placeAllStars(
            palaceLayout.getCuc().getValue(),
            lunarDate.getDay(),
            palaceLayout.isThuan()
        );
        allStarPositions.putAll(tuViStars);

        // Place Thiên Phủ group
        Map<Star, DiaChi> thienPhuStars = ThienPhuStarCalculator.placeAllStars(
            tuViStars.get(Star.TU_VI)
        );
        allStarPositions.putAll(thienPhuStars);

        // Place auxiliary stars
        Map<Star, DiaChi> auxStars = AuxiliaryStarCalculator.placeAllStars(
            lunarDate.getCanYear(),
            lunarDate.getChiYear(),
            lunarDate.getMonth(),
            lunarDate.getHourBranchIndex()
        );
        allStarPositions.putAll(auxStars);

        // Place Trường Sinh stars
        Map<Star, DiaChi> truongSinhStars = TruongSinhCalculator.calculateStarPositions(
            palaceLayout.getCuc().getNguHanh(),
            palaceLayout.isThuan()
        );
        allStarPositions.putAll(truongSinhStars);

        // Step 7: Assign stars to palaces
        assignStarsToPalaces(palaceLayout.getPalaces(), allStarPositions);
        
        // Step 7.5: Calculate brightness for stars (after assignment)
        calculateStarBrightness(palaceLayout.getPalaces());

        // Step 8: Calculate Trường Sinh labels for palaces
        Map<DiaChi, String> truongSinhLabels = TruongSinhCalculator.calculateAllStages(
            palaceLayout.getCuc().getNguHanh(),
            palaceLayout.isThuan()
        );
        for (PalaceInfo palace : palaceLayout.getPalaces()) {
            DiaChi palaceChi = DiaChi.fromCode(palace.getDiaChiCode().toLowerCase());
            palace.setTruongSinhStage(truongSinhLabels.get(palaceChi));
        }

        // Step 9: Calculate markers (Tuần, Triệt)
        MarkerInfo markers = MarkerCalculator.buildMarkerInfo(
            lunarDate.getCanYear(), lunarDate.getChiYear()
        );
        
        // Mark palaces with Tuần/Triệt
        for (PalaceInfo palace : palaceLayout.getPalaces()) {
            DiaChi palaceChi = DiaChi.valueOf(palace.getDiaChiCode());
            palace.setHasTuan(MarkerCalculator.isInTuan(palaceChi, lunarDate.getCanYear(), lunarDate.getChiYear()));
            palace.setHasTriet(MarkerCalculator.isInTriet(palaceChi, lunarDate.getCanYear()));
        }

        // Step 10: Calculate Đại Vận
        CycleInfo cycles = CycleCalculator.calculateDaiVan(
            palaceLayout.getCuc().getValue(),
            palaceLayout.isThuan(),
            palaceLayout.getMenhChi(),
            palaceLayout.getPalaces()
        );

        // Step 11: Build center info
        CenterInfo center = buildCenterInfo(
            request, lunarDate, napAm, palaceLayout, isMale
        );

        // Step 12: Save FACT data to database
        String chartHash = natalChartService.saveNatalChart(
                TuViChartResponse.builder()
                        .center(center)
                        .palaces(palaceLayout.getPalaces())
                        .markers(markers)
                        .cycles(cycles)
                        .build(),
                request
        );

        // Step 13: Build final response (including chart hash for interpretation lookup and payment)
        TuViChartResponse response = TuViChartResponse.builder()
                .center(center)
                .palaces(palaceLayout.getPalaces())
                .markers(markers)
                .cycles(cycles)
                .calculatedAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .chartHash(chartHash)
                .build();
        
        return response;
    }

    /**
     * Assign stars to their respective palaces.
     */
    private void assignStarsToPalaces(List<PalaceInfo> palaces, Map<Star, DiaChi> starPositions) {
        for (Map.Entry<Star, DiaChi> entry : starPositions.entrySet()) {
            Star star = entry.getKey();
            DiaChi chi = entry.getValue();

            for (PalaceInfo palace : palaces) {
                if (palace.getDiaChiCode().equals(chi.name())) {
                    StarInfo starInfo = StarInfo.from(star, null); // Brightness calculated separately
                    if (palace.getStars() == null) {
                        palace.setStars(new ArrayList<>());
                    }
                    palace.getStars().add(starInfo);
                    break;
                }
            }
        }

        // Sort stars in each palace by type (main first, then auxiliary, then minor)
        for (PalaceInfo palace : palaces) {
            if (palace.getStars() != null) {
                palace.getStars().sort((a, b) -> {
                    int typeOrder1 = getStarTypeOrder(a.getType());
                    int typeOrder2 = getStarTypeOrder(b.getType());
                    return Integer.compare(typeOrder1, typeOrder2);
                });
            }
        }
    }

    private int getStarTypeOrder(String type) {
        return switch (type) {
            case "CHINH_TINH" -> 0;
            case "PHU_TINH" -> 1;
            case "BANG_TINH" -> 2;
            case "TRUONG_SINH" -> 3;
            default -> 4;
        };
    }

    /**
     * Calculate brightness for stars in palaces.
     * For now, sets default brightness "BINH" for all stars.
     * TODO: Implement proper brightness calculation based on star position and palace.
     */
    private void calculateStarBrightness(List<PalaceInfo> palaces) {
        for (PalaceInfo palace : palaces) {
            if (palace.getStars() != null) {
                for (StarInfo star : palace.getStars()) {
                    // If brightness is not set, use default "BINH"
                    if (star.getBrightness() == null || star.getBrightness().isBlank()) {
                        star.setBrightness("BINH");
                        star.setBrightnessCode("B");
                        log.debug("Set default brightness BINH for star {} in palace {}", 
                                star.getCode(), palace.getNameCode());
                    }
                }
            }
        }
    }

    /**
     * Build the center info section.
     */
    private CenterInfo buildCenterInfo(
            TuViChartRequest request,
            LunarDateCalculator.LunarDate lunarDate,
            NapAmCalculator.NapAm napAm,
            PalaceCalculator.PalaceLayout palaceLayout,
            boolean isMale) {

        // Calculate Mệnh-Cục relationship
        NguHanh menhNguHanh = napAm.getNguHanh();
        NguHanh cucNguHanh = palaceLayout.getCuc().getNguHanh();
        String menhCucRelation = NguHanh.getRelationship(menhNguHanh, cucNguHanh);

        // Determine direction text
        String thuanNghich = palaceLayout.isThuan() 
            ? (isMale ? "Dương nam - Thuận lý" : "Âm nữ - Thuận lý")
            : (isMale ? "Âm nam - Nghịch lý" : "Dương nữ - Nghịch lý");

        // Find Chủ Mệnh and Chủ Thân (simplified - main star in Mệnh/Thân palace)
        String chuMenh = findMainStarInPalace(palaceLayout.getPalaces(), CungName.MENH);
        boolean menhKhongChinhTinh = false;
        
        if (chuMenh == null) {
            // Cung Mệnh không có Chính tinh - áp dụng logic fallback theo Tử Vi học
            menhKhongChinhTinh = true;
            log.warn("Cung Mệnh không có Chính tinh (Mệnh vô Chính tinh). Áp dụng logic fallback theo Tử Vi học.");
            
            // Theo nguyên tắc Tử Vi học: Ưu tiên xem cung đối diện (Tài Bạch) hoặc cung Thân
            // 1. Ưu tiên 1: Xem cung đối diện với Mệnh (cách 6 vị trí = Thiên Di, index 6)
            chuMenh = findChinhTinhInPalace(palaceLayout.getPalaces(), CungName.THIEN_DI);
            if (chuMenh != null) {
                log.info("Tìm thấy Chủ mệnh từ cung đối diện (Thiên Di): {}", chuMenh);
            } else {
                // 2. Ưu tiên 2: Xem cung Tài Bạch (quan trọng đối với Mệnh)
                chuMenh = findChinhTinhInPalace(palaceLayout.getPalaces(), CungName.TAI_BACH);
                if (chuMenh != null) {
                    log.info("Tìm thấy Chủ mệnh từ cung Tài Bạch: {}", chuMenh);
                } else {
                    // 3. Ưu tiên 3: Xem cung Thân
                    if (palaceLayout.getThanCungName() != null) {
                        chuMenh = findChinhTinhInPalace(palaceLayout.getPalaces(), palaceLayout.getThanCungName());
                        if (chuMenh != null) {
                            log.info("Tìm thấy Chủ mệnh từ cung Thân ({}): {}", 
                                    palaceLayout.getThanCungName().getText(), chuMenh);
                        }
                    }
                    
                    // 4. Cuối cùng: Dùng sao đầu tiên trong Mệnh (nếu có)
                    if (chuMenh == null) {
                        for (PalaceInfo palace : palaceLayout.getPalaces()) {
                            if ("MENH".equals(palace.getNameCode()) && palace.getStars() != null && !palace.getStars().isEmpty()) {
                                chuMenh = palace.getStars().get(0).getName();
                                log.warn("Dùng sao đầu tiên trong cung Mệnh làm Chủ mệnh fallback: {} (type: {})", 
                                        chuMenh, palace.getStars().get(0).getType());
                                break;
                            }
                        }
                    }
                }
            }
            
            // If still null (Mệnh palace has no stars at all), set empty string
            if (chuMenh == null) {
                chuMenh = "";
                log.error("Không thể xác định Chủ mệnh - Cung Mệnh và các cung liên quan đều không có Chính tinh hoặc sao!");
            }
        }
        
        String chuThan = findMainStarInPalace(palaceLayout.getPalaces(), palaceLayout.getThanCungName());

        return CenterInfo.builder()
                .name(request.getName())
                .birthPlace(request.getBirthPlace())
                .solarDate(request.getDate())
                .lunarYearCanChi(lunarDate.getCanChiYear())
                .lunarYear(lunarDate.getYear())
                .lunarMonth(lunarDate.getMonth())
                .lunarMonthCanChi(lunarDate.getCanChiMonth())
                .isLeapMonth(lunarDate.isLeapMonth())
                .lunarDay(lunarDate.getDay())
                .lunarDayCanChi(lunarDate.getCanChiDay())
                .birthHour(request.getHour())
                .birthMinute(request.getMinute())
                .birthHourCanChi(lunarDate.getCanChiHour())
                .hourBranchIndex(lunarDate.getHourBranchIndex())
                .gender(request.getGender())
                .amDuong(lunarDate.getCanYear().getAmDuong().getText())
                .thuanNghich(thuanNghich)
                .banMenh(napAm.getName())
                .banMenhNguHanh(napAm.getNguHanh().name())
                .banMenhDescription(napAm.getDescription())
                .cuc(palaceLayout.getCuc().getText())
                .cucValue(palaceLayout.getCuc().getValue())
                .cucNguHanh(palaceLayout.getCuc().getNguHanh().name())
                .menhCucRelation(menhCucRelation)
                .chuMenh(chuMenh)
                .menhKhongChinhTinh(menhKhongChinhTinh)
                .chuThan(chuThan)
                .thanCu(palaceLayout.getThanCungName() != null ? palaceLayout.getThanCungName().getText() : null)
                .build();
    }

    /**
     * Find the main star (Chính tinh) in a palace.
     * Returns the first Chính tinh found, or the first star if no Chính tinh exists (fallback).
     */
    private String findMainStarInPalace(List<PalaceInfo> palaces, CungName cungName) {
        if (cungName == null) {
            log.warn("findMainStarInPalace called with null cungName");
            return null;
        }
        
        for (PalaceInfo palace : palaces) {
            if (palace.getNameCode().equals(cungName.name())) {
                if (palace.getStars() == null || palace.getStars().isEmpty()) {
                    log.warn("Palace {} has no stars!", cungName.name());
                    return null;
                }
                
                // First, try to find Chính tinh
                for (StarInfo star : palace.getStars()) {
                    if ("CHINH_TINH".equals(star.getType())) {
                        log.debug("Found Chính tinh {} in palace {}", star.getName(), cungName.name());
                        return star.getName();
                    }
                }
                
                // For Cung Mệnh, Chủ mệnh MUST be a Chính tinh - if not found, this is an error
                if (cungName == CungName.MENH) {
                    log.error("Cung Mệnh has no Chính tinh! This is a chart calculation error. Stars in Mệnh: {}", 
                            palace.getStars().stream()
                                    .map(s -> s.getCode() + "(" + s.getType() + ")")
                                    .collect(Collectors.joining(", ")));
                    // Return null instead of fallback - let the caller handle it
                    return null;
                }
                
                // For other palaces (like Thân), fallback to first star if no Chính tinh
                StarInfo firstStar = palace.getStars().get(0);
                log.warn("No Chính tinh found in palace {}, using first star: {} (type: {})", 
                        cungName.name(), firstStar.getName(), firstStar.getType());
                return firstStar.getName();
            }
        }
        
        log.warn("Palace {} not found in palaces list!", cungName.name());
        return null;
    }
    
    /**
     * Find Chính tinh (main star) in a specific palace.
     * Returns the first Chính tinh found, or null if none exists.
     * This is a helper method for fallback logic when Cung Mệnh has no Chính tinh.
     */
    private String findChinhTinhInPalace(List<PalaceInfo> palaces, CungName cungName) {
        if (cungName == null) {
            return null;
        }
        
        for (PalaceInfo palace : palaces) {
            if (palace.getNameCode().equals(cungName.name())) {
                if (palace.getStars() == null || palace.getStars().isEmpty()) {
                    return null;
                }
                
                // Find first Chính tinh
                for (StarInfo star : palace.getStars()) {
                    if ("CHINH_TINH".equals(star.getType())) {
                        log.debug("Found Chính tinh {} in palace {} for fallback", star.getName(), cungName.name());
                        return star.getName();
                    }
                }
                return null;
            }
        }
        return null;
    }
}

package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.calculator.*;
import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main service for generating Tu Vi (Purple Star Astrology) charts.
 * Orchestrates all calculators to produce a complete chart.
 */
@Slf4j
@Service
public class TuViChartService {

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

        // Step 12: Build final response
        return TuViChartResponse.builder()
                .center(center)
                .palaces(palaceLayout.getPalaces())
                .markers(markers)
                .cycles(cycles)
                .calculatedAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();
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
                .chuThan(chuThan)
                .thanCu(palaceLayout.getThanCungName() != null ? palaceLayout.getThanCungName().getText() : null)
                .build();
    }

    /**
     * Find the main star (Chính tinh) in a palace.
     */
    private String findMainStarInPalace(List<PalaceInfo> palaces, CungName cungName) {
        if (cungName == null) return null;
        
        for (PalaceInfo palace : palaces) {
            if (palace.getNameCode().equals(cungName.name())) {
                if (palace.getStars() != null) {
                    for (StarInfo star : palace.getStars()) {
                        if ("CHINH_TINH".equals(star.getType())) {
                            return star.getName();
                        }
                    }
                }
                break;
            }
        }
        return null;
    }
}

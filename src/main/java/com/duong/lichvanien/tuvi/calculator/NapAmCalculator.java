package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * Calculator for Nạp Âm (Nap Am / 60 Jiazi cycles) - determines the element of a year.
 * Each of the 60 Can-Chi combinations has a specific element association.
 */
public class NapAmCalculator {

    /**
     * Nạp Âm result containing the element name and category.
     */
    @Data
    @Builder
    public static class NapAm {
        private String name;           // e.g., "Sơn Đầu Hỏa"
        private String description;    // e.g., "Lửa trên núi"
        private NguHanh nguHanh;       // e.g., HOA
    }

    /**
     * 30 Nạp Âm categories (each covers 2 consecutive Can-Chi pairs).
     */
    @Getter
    @AllArgsConstructor
    public enum NapAmType {
        HAI_TRUNG_KIM("Hải Trung Kim", "Vàng trong biển", NguHanh.KIM),
        LO_TRUNG_HOA("Lô Trung Hỏa", "Lửa trong lò", NguHanh.HOA),
        DAI_LAM_MOC("Đại Lâm Mộc", "Gỗ rừng lớn", NguHanh.MOC),
        LO_BANG_THO("Lộ Bàng Thổ", "Đất bên đường", NguHanh.THO),
        KIEM_PHONG_KIM("Kiếm Phong Kim", "Vàng mũi kiếm", NguHanh.KIM),
        SAN_DAU_HOA("Sơn Đầu Hỏa", "Lửa trên núi", NguHanh.HOA),
        GIAN_HA_THUY("Giản Hạ Thủy", "Nước dưới suối", NguHanh.THUY),
        THANH_DICH_THO("Thành Đầu Thổ", "Đất đầu thành", NguHanh.THO),
        BACH_LAP_KIM("Bạch Lạp Kim", "Vàng trong nến", NguHanh.KIM),
        DUONG_LIEU_MOC("Dương Liễu Mộc", "Gỗ cây liễu", NguHanh.MOC),
        TUYEN_TRUNG_THUY("Tuyền Trung Thủy", "Nước trong suối", NguHanh.THUY),
        OC_THUONG_THO("Ốc Thượng Thổ", "Đất trên mái", NguHanh.THO),
        TICH_LICH_HOA("Tích Lịch Hỏa", "Lửa sấm sét", NguHanh.HOA),
        TUNG_BACH_MOC("Tùng Bách Mộc", "Gỗ tùng bách", NguHanh.MOC),
        TRUONG_LUU_THUY("Trường Lưu Thủy", "Nước chảy dài", NguHanh.THUY),
        SA_TRUNG_KIM("Sa Trung Kim", "Vàng trong cát", NguHanh.KIM),
        SAN_HA_HOA("Sơn Hạ Hỏa", "Lửa chân núi", NguHanh.HOA),
        BINH_DIA_MOC("Bình Địa Mộc", "Gỗ đồng bằng", NguHanh.MOC),
        BICH_THUONG_THO("Bích Thượng Thổ", "Đất trên tường", NguHanh.THO),
        KIM_BACH_KIM("Kim Bạch Kim", "Vàng trắng", NguHanh.KIM),
        PHAT_DANG_HOA("Phúc Đăng Hỏa", "Lửa đèn Phật", NguHanh.HOA),
        THIEN_HA_THUY("Thiên Hà Thủy", "Nước sông Ngân", NguHanh.THUY),
        DAI_DICH_THO("Đại Dịch Thổ", "Đất trạm lớn", NguHanh.THO),
        THOA_XUYEN_KIM("Thoa Xuyến Kim", "Vàng trang sức", NguHanh.KIM),
        TANG_DO_MOC("Tang Đố Mộc", "Gỗ cây dâu", NguHanh.MOC),
        DAI_KHE_THUY("Đại Khê Thủy", "Nước khe lớn", NguHanh.THUY),
        SA_TRUNG_THO("Sa Trung Thổ", "Đất trong cát", NguHanh.THO),
        THIEN_THUONG_HOA("Thiên Thượng Hỏa", "Lửa trên trời", NguHanh.HOA),
        LUU_HA_MOC("Lựu Hạ Mộc", "Gỗ cây lựu", NguHanh.MOC),
        DAI_HAI_THUY("Đại Hải Thủy", "Nước biển lớn", NguHanh.THUY);

        private final String name;
        private final String description;
        private final NguHanh nguHanh;
    }

    /**
     * Lookup table mapping Can-Chi index (0-59) to NapAm type.
     * Each NapAm covers 2 consecutive Can-Chi pairs.
     */
    private static final NapAmType[] NAP_AM_TABLE = {
        NapAmType.HAI_TRUNG_KIM,    // 0-1: Giáp Tý, Ất Sửu
        NapAmType.HAI_TRUNG_KIM,
        NapAmType.LO_TRUNG_HOA,     // 2-3: Bính Dần, Đinh Mão
        NapAmType.LO_TRUNG_HOA,
        NapAmType.DAI_LAM_MOC,      // 4-5: Mậu Thìn, Kỷ Tỵ
        NapAmType.DAI_LAM_MOC,
        NapAmType.LO_BANG_THO,      // 6-7: Canh Ngọ, Tân Mùi
        NapAmType.LO_BANG_THO,
        NapAmType.KIEM_PHONG_KIM,   // 8-9: Nhâm Thân, Quý Dậu
        NapAmType.KIEM_PHONG_KIM,
        NapAmType.SAN_DAU_HOA,      // 10-11: Giáp Tuất, Ất Hợi
        NapAmType.SAN_DAU_HOA,
        NapAmType.GIAN_HA_THUY,     // 12-13: Bính Tý, Đinh Sửu
        NapAmType.GIAN_HA_THUY,
        NapAmType.THANH_DICH_THO,   // 14-15: Mậu Dần, Kỷ Mão
        NapAmType.THANH_DICH_THO,
        NapAmType.BACH_LAP_KIM,     // 16-17: Canh Thìn, Tân Tỵ
        NapAmType.BACH_LAP_KIM,
        NapAmType.DUONG_LIEU_MOC,   // 18-19: Nhâm Ngọ, Quý Mùi
        NapAmType.DUONG_LIEU_MOC,
        NapAmType.TUYEN_TRUNG_THUY, // 20-21: Giáp Thân, Ất Dậu
        NapAmType.TUYEN_TRUNG_THUY,
        NapAmType.OC_THUONG_THO,    // 22-23: Bính Tuất, Đinh Hợi
        NapAmType.OC_THUONG_THO,
        NapAmType.TICH_LICH_HOA,    // 24-25: Mậu Tý, Kỷ Sửu
        NapAmType.TICH_LICH_HOA,
        NapAmType.TUNG_BACH_MOC,    // 26-27: Canh Dần, Tân Mão
        NapAmType.TUNG_BACH_MOC,
        NapAmType.TRUONG_LUU_THUY,  // 28-29: Nhâm Thìn, Quý Tỵ
        NapAmType.TRUONG_LUU_THUY,
        NapAmType.SA_TRUNG_KIM,     // 30-31: Giáp Ngọ, Ất Mùi
        NapAmType.SA_TRUNG_KIM,
        NapAmType.SAN_HA_HOA,       // 32-33: Bính Thân, Đinh Dậu
        NapAmType.SAN_HA_HOA,
        NapAmType.BINH_DIA_MOC,     // 34-35: Mậu Tuất, Kỷ Hợi
        NapAmType.BINH_DIA_MOC,
        NapAmType.BICH_THUONG_THO,  // 36-37: Canh Tý, Tân Sửu
        NapAmType.BICH_THUONG_THO,
        NapAmType.KIM_BACH_KIM,     // 38-39: Nhâm Dần, Quý Mão
        NapAmType.KIM_BACH_KIM,
        NapAmType.PHAT_DANG_HOA,    // 40-41: Giáp Thìn, Ất Tỵ
        NapAmType.PHAT_DANG_HOA,
        NapAmType.THIEN_HA_THUY,    // 42-43: Bính Ngọ, Đinh Mùi
        NapAmType.THIEN_HA_THUY,
        NapAmType.DAI_DICH_THO,     // 44-45: Mậu Thân, Kỷ Dậu
        NapAmType.DAI_DICH_THO,
        NapAmType.THOA_XUYEN_KIM,   // 46-47: Canh Tuất, Tân Hợi
        NapAmType.THOA_XUYEN_KIM,
        NapAmType.TANG_DO_MOC,      // 48-49: Nhâm Tý, Quý Sửu
        NapAmType.TANG_DO_MOC,
        NapAmType.DAI_KHE_THUY,     // 50-51: Giáp Dần, Ất Mão
        NapAmType.DAI_KHE_THUY,
        NapAmType.SA_TRUNG_THO,     // 52-53: Bính Thìn, Đinh Tỵ
        NapAmType.SA_TRUNG_THO,
        NapAmType.THIEN_THUONG_HOA, // 54-55: Mậu Ngọ, Kỷ Mùi
        NapAmType.THIEN_THUONG_HOA,
        NapAmType.LUU_HA_MOC,       // 56-57: Canh Thân, Tân Dậu
        NapAmType.LUU_HA_MOC,
        NapAmType.DAI_HAI_THUY,     // 58-59: Nhâm Tuất, Quý Hợi
        NapAmType.DAI_HAI_THUY
    };

    /**
     * Calculate the Can-Chi index (0-59) from Thiên Can and Địa Chi.
     * Uses Chinese Remainder Theorem to find the unique index where:
     * index ≡ canIndex (mod 10) and index ≡ chiIndex (mod 12)
     * 
     * The 60 Jiazi cycle: Giáp Tý = 0, Ất Sửu = 1, ..., Quý Hợi = 59
     */
    public static int getCanChiIndex(ThienCan can, DiaChi chi) {
        int canIndex = can.getIndex();
        int chiIndex = chi.getIndex();
        // Using Chinese Remainder Theorem:
        // index = (6 * canIndex - 5 * chiIndex) mod 60
        return (6 * canIndex - 5 * chiIndex + 60) % 60;
    }

    /**
     * Get Nạp Âm for a given year based on Can-Chi.
     */
    public static NapAm getNapAm(ThienCan can, DiaChi chi) {
        int index = getCanChiIndex(can, chi);
        NapAmType type = NAP_AM_TABLE[index];
        return NapAm.builder()
                .name(type.getName())
                .description(type.getDescription())
                .nguHanh(type.getNguHanh())
                .build();
    }

    /**
     * Get Nạp Âm for a lunar year.
     */
    public static NapAm getNapAmForYear(int lunarYear) {
        ThienCan can = ThienCan.fromIndex((lunarYear + 6) % 10);
        DiaChi chi = DiaChi.fromIndex((lunarYear + 8) % 12);
        return getNapAm(can, chi);
    }
}

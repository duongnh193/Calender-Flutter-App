package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * All stars in Tu Vi astrology system.
 * Includes main stars (Chính tinh), auxiliary stars (Phụ tinh), and minor stars (Bàng tinh).
 */
@Getter
public enum Star {
    // ===== CHÍNH TINH (Main Stars) =====
    // Tử Vi group (14 main stars)
    TU_VI("Tử Vi", StarType.CHINH_TINH, NguHanh.THO),
    LIEM_TRINH("Liêm Trinh", StarType.CHINH_TINH, NguHanh.HOA),
    THIEN_DONG("Thiên Đồng", StarType.CHINH_TINH, NguHanh.THUY),
    VU_KHUC("Vũ Khúc", StarType.CHINH_TINH, NguHanh.KIM),
    THAI_DUONG("Thái Dương", StarType.CHINH_TINH, NguHanh.HOA),
    THIEN_CO("Thiên Cơ", StarType.CHINH_TINH, NguHanh.MOC),

    // Thiên Phủ group
    THIEN_PHU("Thiên Phủ", StarType.CHINH_TINH, NguHanh.THO),
    THAI_AM("Thái Âm", StarType.CHINH_TINH, NguHanh.THUY),
    THAM_LANG("Tham Lang", StarType.CHINH_TINH, NguHanh.THUY),
    CU_MON("Cự Môn", StarType.CHINH_TINH, NguHanh.THUY),
    THIEN_TUONG("Thiên Tướng", StarType.CHINH_TINH, NguHanh.THUY),
    THIEN_LUONG("Thiên Lương", StarType.CHINH_TINH, NguHanh.MOC),
    THAT_SAT("Thất Sát", StarType.CHINH_TINH, NguHanh.KIM),
    PHA_QUAN("Phá Quân", StarType.CHINH_TINH, NguHanh.THUY),

    // ===== PHỤ TINH (Auxiliary Stars) =====
    // Lục sát (6 Malefic)
    KINH_DUONG("Kình Dương", StarType.PHU_TINH, NguHanh.KIM),
    DA_LA("Đà La", StarType.PHU_TINH, NguHanh.KIM),
    HOA_TINH("Hỏa Tinh", StarType.PHU_TINH, NguHanh.HOA),
    LINH_TINH("Linh Tinh", StarType.PHU_TINH, NguHanh.HOA),
    THIEN_KHONG("Thiên Không", StarType.PHU_TINH, NguHanh.HOA),
    DIA_KIEP("Địa Kiếp", StarType.PHU_TINH, NguHanh.HOA),
    DIA_KHONG("Địa Không", StarType.PHU_TINH, NguHanh.HOA),

    // Lục cát (6 Benefic)
    VAN_XUONG("Văn Xương", StarType.PHU_TINH, NguHanh.KIM),
    VAN_KHUC("Văn Khúc", StarType.PHU_TINH, NguHanh.THUY),
    TA_PHU("Tả Phù", StarType.PHU_TINH, NguHanh.THO),
    HUU_BAT("Hữu Bật", StarType.PHU_TINH, NguHanh.THUY),
    THIEN_KHOI("Thiên Khôi", StarType.PHU_TINH, NguHanh.HOA),
    THIEN_VIET("Thiên Việt", StarType.PHU_TINH, NguHanh.HOA),

    // Tứ hóa (Four Transformations)
    HOA_LOC("Hóa Lộc", StarType.PHU_TINH, NguHanh.MOC),
    HOA_QUYEN("Hóa Quyền", StarType.PHU_TINH, NguHanh.MOC),
    HOA_KHOA("Hóa Khoa", StarType.PHU_TINH, NguHanh.THUY),
    HOA_KY("Hóa Kỵ", StarType.PHU_TINH, NguHanh.THUY),

    // Lộc Tồn group
    LOC_TON("Lộc Tồn", StarType.PHU_TINH, NguHanh.THO),

    // ===== BÀNG TINH (Minor Stars) =====
    // Thái Tuế group
    THAI_TUE("Thái Tuế", StarType.BANG_TINH, NguHanh.HOA),
    THIEU_DUONG("Thiếu Dương", StarType.BANG_TINH, NguHanh.HOA),
    TANG_MON("Tang Môn", StarType.BANG_TINH, NguHanh.MOC),
    THIEU_AM("Thiếu Âm", StarType.BANG_TINH, NguHanh.THUY),
    QUAN_PHU("Quan Phù", StarType.BANG_TINH, NguHanh.HOA),
    TU_PHU("Tử Phù", StarType.BANG_TINH, NguHanh.THUY),
    TUE_PHA("Tuế Phá", StarType.BANG_TINH, NguHanh.HOA),
    LONG_DUC("Long Đức", StarType.BANG_TINH, NguHanh.THUY),
    BACH_HO("Bạch Hổ", StarType.BANG_TINH, NguHanh.KIM),
    PHUC_DUC("Phúc Đức", StarType.BANG_TINH, NguHanh.THO),
    DIEU_KHACH("Điếu Khách", StarType.BANG_TINH, NguHanh.HOA),
    TRUC_PHU("Trực Phù", StarType.BANG_TINH, NguHanh.MOC),

    // Thiên Mã group
    THIEN_MA("Thiên Mã", StarType.BANG_TINH, NguHanh.HOA),

    // Đào Hoa group
    DAO_HOA("Đào Hoa", StarType.BANG_TINH, NguHanh.MOC),
    HONG_LOAN("Hồng Loan", StarType.BANG_TINH, NguHanh.THUY),
    THIEN_HY("Thiên Hỷ", StarType.BANG_TINH, NguHanh.THUY),

    // Other minor stars
    THIEN_HINH("Thiên Hình", StarType.BANG_TINH, NguHanh.HOA),
    THIEN_RIENG("Thiên Riêu", StarType.BANG_TINH, NguHanh.THUY),
    THIEN_Y("Thiên Y", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_QUAN("Thiên Quan", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_PHUC("Thiên Phúc", StarType.BANG_TINH, NguHanh.THO),
    THIEN_TRU("Thiên Trù", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_THO("Thiên Thọ", StarType.BANG_TINH, NguHanh.THO),
    THIEN_TAI("Thiên Tài", StarType.BANG_TINH, NguHanh.THO),
    THIEN_THU("Thiên Thụ", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_SU("Thiên Sứ", StarType.BANG_TINH, NguHanh.THUY),
    THIEN_GIAI("Thiên Giải", StarType.BANG_TINH, NguHanh.MOC),
    DIA_GIAI("Địa Giải", StarType.BANG_TINH, NguHanh.THO),

    // Đại Hao group
    DAI_HAO("Đại Hao", StarType.BANG_TINH, NguHanh.HOA),
    TIEU_HAO("Tiểu Hao", StarType.BANG_TINH, NguHanh.HOA),

    // Trường Sinh group (12 stages)
    TRUONG_SINH("Trường Sinh", StarType.TRUONG_SINH, NguHanh.MOC),
    MOC_DUC("Mộc Dục", StarType.TRUONG_SINH, NguHanh.THUY),
    QUAN_DAI("Quán Đái", StarType.TRUONG_SINH, NguHanh.MOC),
    LAM_QUAN("Lâm Quan", StarType.TRUONG_SINH, NguHanh.MOC),
    DE_VUONG("Đế Vượng", StarType.TRUONG_SINH, NguHanh.KIM),
    SUY("Suy", StarType.TRUONG_SINH, NguHanh.KIM),
    BENH("Bệnh", StarType.TRUONG_SINH, NguHanh.HOA),
    TU("Tử", StarType.TRUONG_SINH, NguHanh.THUY),
    MO("Mộ", StarType.TRUONG_SINH, NguHanh.THO),
    TUYET("Tuyệt", StarType.TRUONG_SINH, NguHanh.THUY),
    THAI("Thai", StarType.TRUONG_SINH, NguHanh.THUY),
    DUONG("Dưỡng", StarType.TRUONG_SINH, NguHanh.MOC),

    // Lưu niên stars
    LUU_HA("Lưu Hà", StarType.BANG_TINH, NguHanh.THUY),
    PHUC_BINH("Phục Binh", StarType.BANG_TINH, NguHanh.HOA),
    HOA_CAI("Hoa Cái", StarType.BANG_TINH, NguHanh.MOC),
    KIEP_SAT("Kiếp Sát", StarType.BANG_TINH, NguHanh.HOA),
    AM_SAT("Âm Sát", StarType.BANG_TINH, NguHanh.THUY),

    // Additional stars from image
    THANH_LONG("Thanh Long", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_MA_DINH("Thiên Mã (Đ)", StarType.BANG_TINH, NguHanh.HOA),
    L_THAI_TUE("L.Thái tuế", StarType.BANG_TINH, NguHanh.HOA),
    THIEN_HU("Thiên Hư", StarType.BANG_TINH, NguHanh.THUY),
    TA_PHU_DINH("Tả Phù (Đ)", StarType.BANG_TINH, NguHanh.THO),
    HY_THAN("Hỷ Thần", StarType.BANG_TINH, NguHanh.MOC),
    THAI_PHU("Thái Phụ", StarType.BANG_TINH, NguHanh.MOC),
    PHONG_CAO("Phong Cáo", StarType.BANG_TINH, NguHanh.THO),
    AN_QUANG("Ân Quang", StarType.BANG_TINH, NguHanh.HOA),
    QUAN_DOI("Quan Đới", StarType.BANG_TINH, NguHanh.MOC),
    CO_THAN("Cô Thần", StarType.BANG_TINH, NguHanh.THO),
    THIEN_DIEU("Thiên Điếu", StarType.BANG_TINH, NguHanh.HOA),
    L_DA_LA("L.Đà La", StarType.BANG_TINH, NguHanh.KIM),
    DAU_QUAN("Đẩu Quân", StarType.BANG_TINH, NguHanh.THUY),
    QUAN_PHU_2("Quan Phủ", StarType.BANG_TINH, NguHanh.THO),
    DIA_KIEP_HAM("Địa Kiếp (H)", StarType.BANG_TINH, NguHanh.HOA),
    L_TANG_MON("L.Tang môn", StarType.BANG_TINH, NguHanh.MOC),
    TAM_THAI("Tam Thai", StarType.BANG_TINH, NguHanh.MOC),
    BAT_TOA("Bát Tọa", StarType.BANG_TINH, NguHanh.THO),
    THIEN_QUY("Thiên Quý", StarType.BANG_TINH, NguHanh.THO),
    TUONG_QUAN("Tướng Quân", StarType.BANG_TINH, NguHanh.KIM),
    THIEN_KHOC("Thiên Khốc", StarType.BANG_TINH, NguHanh.THUY),
    QUOC_AN("Quốc Ấn", StarType.BANG_TINH, NguHanh.THO),
    PHUONG_CAC("Phượng Các", StarType.BANG_TINH, NguHanh.THO),
    BENH_PHU("Bệnh Phù", StarType.BANG_TINH, NguHanh.THUY),
    L_THIEN_HU("L.Thiên Hư", StarType.BANG_TINH, NguHanh.THUY),
    PHI_LIEM("Phi Liêm", StarType.BANG_TINH, NguHanh.HOA),
    PHA_TOAI("Phá Toái", StarType.BANG_TINH, NguHanh.HOA),
    DUONG_PHU("Đường Phủ", StarType.BANG_TINH, NguHanh.THO),
    TAU_THU("Tấu Thư", StarType.BANG_TINH, NguHanh.KIM),
    PHUC_DUC_2("Phúc Đức", StarType.BANG_TINH, NguHanh.THO),
    THIEN_DUC("Thiên Đức", StarType.BANG_TINH, NguHanh.HOA),
    NGUYET_DUC("Nguyệt Đức", StarType.BANG_TINH, NguHanh.THUY),
    L_VAN_TINH("L.Văn Tính", StarType.BANG_TINH, NguHanh.MOC),
    THIEN_LA("Thiên La", StarType.BANG_TINH, NguHanh.THO),
    THIEN_THUONG("Thiên Thương", StarType.BANG_TINH, NguHanh.MOC),
    L_KINH_DUONG("L.Kinh Dương", StarType.BANG_TINH, NguHanh.KIM),
    LONG_TRI("Long Trì", StarType.BANG_TINH, NguHanh.THUY),
    L_LOC_TON("L.Lộc Tồn", StarType.BANG_TINH, NguHanh.THO),
    BAC_SI("Bác Sĩ", StarType.BANG_TINH, NguHanh.THUY),
    LUC_SI("Lực Sĩ", StarType.BANG_TINH, NguHanh.HOA),
    TIEU_HAO_2("Tiểu Hao", StarType.BANG_TINH, NguHanh.HOA),
    QUAN_PHU_3("Quan Phủ", StarType.BANG_TINH, NguHanh.THO),
    DIA_VONG("Địa Võng", StarType.BANG_TINH, NguHanh.THO),
    THIEN_KHOI_2("Thiên Khôi", StarType.BANG_TINH, NguHanh.HOA),
    L_BACH_HO("L.Bạch Hổ", StarType.BANG_TINH, NguHanh.KIM),
    L_THIEN_KHOC("L.Thiên Khốc", StarType.BANG_TINH, NguHanh.THUY),
    THIEN_KHOI_HOI("Thiên Khôi", StarType.BANG_TINH, NguHanh.HOA),
    DIA_KIEP_2("Địa Kiếp", StarType.BANG_TINH, NguHanh.HOA),
    L_THIEN_MA("L.Thiên Mã", StarType.BANG_TINH, NguHanh.HOA);

    private final String text;
    private final StarType type;
    private final NguHanh nguHanh;

    Star(String text, StarType type, NguHanh nguHanh) {
        this.text = text;
        this.type = type;
        this.nguHanh = nguHanh;
    }

    public enum StarType {
        CHINH_TINH("Chính tinh", "Main Star"),
        PHU_TINH("Phụ tinh", "Auxiliary Star"),
        BANG_TINH("Bàng tinh", "Minor Star"),
        TRUONG_SINH("Trường Sinh", "Life Cycle Star");

        @Getter
        private final String text;
        @Getter
        private final String englishText;

        StarType(String text, String englishText) {
            this.text = text;
            this.englishText = englishText;
        }
    }
}

package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * 12 Cung (Palaces) in Tu Vi chart.
 * Order follows the standard placement starting from Mệnh.
 */
@Getter
public enum CungName {
    MENH(0, "Mệnh", "Destiny"),
    PHU_MAU(1, "Phụ Mẫu", "Parents"),
    PHUC_DUC(2, "Phúc Đức", "Fortune"),
    DIEN_TRACH(3, "Điền Trạch", "Property"),
    QUAN_LOC(4, "Quan Lộc", "Career"),
    NO_BOC(5, "Nô Bộc", "Servants"),
    THIEN_DI(6, "Thiên Di", "Travel"),
    TAT_ACH(7, "Tật Ách", "Health"),
    TAI_BACH(8, "Tài Bạch", "Wealth"),
    TU_TUC(9, "Tử Tức", "Children"),
    PHU_THE(10, "Phu Thê", "Spouse"),
    HUYNH_DE(11, "Huynh Đệ", "Siblings");

    private final int index;
    private final String text;
    private final String englishText;

    CungName(int index, String text, String englishText) {
        this.index = index;
        this.text = text;
        this.englishText = englishText;
    }

    public static CungName fromIndex(int index) {
        int normalizedIndex = Math.floorMod(index, 12);
        for (CungName c : values()) {
            if (c.index == normalizedIndex) {
                return c;
            }
        }
        return MENH;
    }
}

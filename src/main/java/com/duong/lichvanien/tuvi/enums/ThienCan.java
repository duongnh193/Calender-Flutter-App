package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * 10 Thiên Can (Heavenly Stems) - used for year, month, day calculations.
 */
@Getter
public enum ThienCan {
    GIAP(0, "Giáp", "giap", NguHanh.MOC, AmDuong.DUONG),
    AT(1, "Ất", "at", NguHanh.MOC, AmDuong.AM),
    BINH(2, "Bính", "binh", NguHanh.HOA, AmDuong.DUONG),
    DINH(3, "Đinh", "dinh", NguHanh.HOA, AmDuong.AM),
    MAU(4, "Mậu", "mau", NguHanh.THO, AmDuong.DUONG),
    KY(5, "Kỷ", "ky", NguHanh.THO, AmDuong.AM),
    CANH(6, "Canh", "canh", NguHanh.KIM, AmDuong.DUONG),
    TAN(7, "Tân", "tan", NguHanh.KIM, AmDuong.AM),
    NHAM(8, "Nhâm", "nham", NguHanh.THUY, AmDuong.DUONG),
    QUY(9, "Quý", "quy", NguHanh.THUY, AmDuong.AM);

    private final int index;
    private final String text;
    private final String code;
    private final NguHanh nguHanh;
    private final AmDuong amDuong;

    ThienCan(int index, String text, String code, NguHanh nguHanh, AmDuong amDuong) {
        this.index = index;
        this.text = text;
        this.code = code;
        this.nguHanh = nguHanh;
        this.amDuong = amDuong;
    }

    public static ThienCan fromIndex(int index) {
        int normalizedIndex = Math.floorMod(index, 10);
        for (ThienCan tc : values()) {
            if (tc.index == normalizedIndex) {
                return tc;
            }
        }
        return GIAP;
    }

    public static ThienCan fromCode(String code) {
        for (ThienCan tc : values()) {
            if (tc.code.equalsIgnoreCase(code)) {
                return tc;
            }
        }
        return null;
    }
}

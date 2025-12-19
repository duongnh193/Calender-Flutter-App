package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * 12 Địa Chi (Earthly Branches) - used for palace positions and hour calculations.
 */
@Getter
public enum DiaChi {
    TY(0, "Tý", "ti"),
    SUU(1, "Sửu", "suu"),
    DAN(2, "Dần", "dan"),
    MAO(3, "Mão", "mao"),
    THIN(4, "Thìn", "thin"),
    TI(5, "Tỵ", "ty"),
    NGO(6, "Ngọ", "ngo"),
    MUI(7, "Mùi", "mui"),
    THAN(8, "Thân", "than"),
    DAU(9, "Dậu", "dau"),
    TUAT(10, "Tuất", "tuat"),
    HOI(11, "Hợi", "hoi");

    private final int index;
    private final String text;
    private final String code;

    DiaChi(int index, String text, String code) {
        this.index = index;
        this.text = text;
        this.code = code;
    }

    public static DiaChi fromIndex(int index) {
        int normalizedIndex = Math.floorMod(index, 12);
        for (DiaChi dc : values()) {
            if (dc.index == normalizedIndex) {
                return dc;
            }
        }
        return TY;
    }

    public static DiaChi fromCode(String code) {
        for (DiaChi dc : values()) {
            if (dc.code.equalsIgnoreCase(code)) {
                return dc;
            }
        }
        return null;
    }

    /**
     * Get the next DiaChi (moving forward/clockwise).
     */
    public DiaChi next() {
        return fromIndex(this.index + 1);
    }

    /**
     * Get the previous DiaChi (moving backward/counter-clockwise).
     */
    public DiaChi prev() {
        return fromIndex(this.index - 1);
    }

    /**
     * Move by offset positions.
     */
    public DiaChi offset(int offset) {
        return fromIndex(this.index + offset);
    }
}

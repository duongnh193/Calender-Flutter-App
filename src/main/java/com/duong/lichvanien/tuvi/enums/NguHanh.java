package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * 5 elements (Ngũ Hành) in Vietnamese astrology.
 */
@Getter
public enum NguHanh {
    KIM(0, "Kim", "Metal", "#FFD700"),
    MOC(1, "Mộc", "Wood", "#228B22"),
    THUY(2, "Thủy", "Water", "#1E90FF"),
    HOA(3, "Hỏa", "Fire", "#FF4500"),
    THO(4, "Thổ", "Earth", "#DAA520");

    private final int index;
    private final String text;
    private final String englishText;
    private final String color;

    NguHanh(int index, String text, String englishText, String color) {
        this.index = index;
        this.text = text;
        this.englishText = englishText;
        this.color = color;
    }

    /**
     * Check if this element generates (sinh) the other.
     * Kim sinh Thủy, Thủy sinh Mộc, Mộc sinh Hỏa, Hỏa sinh Thổ, Thổ sinh Kim
     */
    public boolean generates(NguHanh other) {
        return switch (this) {
            case KIM -> other == THUY;
            case THUY -> other == MOC;
            case MOC -> other == HOA;
            case HOA -> other == THO;
            case THO -> other == KIM;
        };
    }

    /**
     * Check if this element overcomes (khắc) the other.
     * Kim khắc Mộc, Mộc khắc Thổ, Thổ khắc Thủy, Thủy khắc Hỏa, Hỏa khắc Kim
     */
    public boolean overcomes(NguHanh other) {
        return switch (this) {
            case KIM -> other == MOC;
            case MOC -> other == THO;
            case THO -> other == THUY;
            case THUY -> other == HOA;
            case HOA -> other == KIM;
        };
    }

    /**
     * Get the relationship description between two elements.
     */
    public static String getRelationship(NguHanh menh, NguHanh cuc) {
        if (menh == cuc) {
            return "Bình hòa";
        } else if (menh.generates(cuc)) {
            return "Mệnh sinh Cục";
        } else if (cuc.generates(menh)) {
            return "Cục sinh Mệnh";
        } else if (menh.overcomes(cuc)) {
            return "Mệnh khắc Cục";
        } else if (cuc.overcomes(menh)) {
            return "Cục khắc Mệnh";
        }
        return "Không xác định";
    }
}

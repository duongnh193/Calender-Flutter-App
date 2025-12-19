package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * Star brightness/strength levels (Miếu/Vượng/Đắc/Bình/Hãm).
 */
@Getter
public enum StarBrightness {
    MIEU("Miếu", "M", "Temple - Strongest"),
    VUONG("Vượng", "V", "Flourishing"),
    DAC("Đắc", "Đ", "Obtained"),
    BINH("Bình", "B", "Average"),
    HAM("Hãm", "H", "Fallen - Weakest");

    private final String text;
    private final String shortCode;
    private final String description;

    StarBrightness(String text, String shortCode, String description) {
        this.text = text;
        this.shortCode = shortCode;
        this.description = description;
    }
}

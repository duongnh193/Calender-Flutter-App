package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * Âm Dương (Yin Yang) classification.
 */
@Getter
public enum AmDuong {
    AM("Âm", "Yin"),
    DUONG("Dương", "Yang");

    private final String text;
    private final String englishText;

    AmDuong(String text, String englishText) {
        this.text = text;
        this.englishText = englishText;
    }
}

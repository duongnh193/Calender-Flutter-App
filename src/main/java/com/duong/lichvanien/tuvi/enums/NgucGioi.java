package com.duong.lichvanien.tuvi.enums;

import lombok.Getter;

/**
 * 5 Cục (Five Bureaus/Destiny Types) in Tu Vi.
 * Determines the starting position of Tử Vi star.
 */
@Getter
public enum NgucGioi {
    THUY_NHI_CUC(2, "Thủy nhị cục", NguHanh.THUY),
    MOC_TAM_CUC(3, "Mộc tam cục", NguHanh.MOC),
    KIM_TU_CUC(4, "Kim tứ cục", NguHanh.KIM),
    THO_NGU_CUC(5, "Thổ ngũ cục", NguHanh.THO),
    HOA_LUC_CUC(6, "Hỏa lục cục", NguHanh.HOA);

    private final int value;
    private final String text;
    private final NguHanh nguHanh;

    NgucGioi(int value, String text, NguHanh nguHanh) {
        this.value = value;
        this.text = text;
        this.nguHanh = nguHanh;
    }

    public static NgucGioi fromValue(int value) {
        for (NgucGioi c : values()) {
            if (c.value == value) {
                return c;
            }
        }
        return null;
    }
}

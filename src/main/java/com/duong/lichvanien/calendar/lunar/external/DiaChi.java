package com.duong.lichvanien.calendar.lunar.external;

/**
 * Địa Chi enumeration with Vietnamese labels.
 */
public enum DiaChi {
    TY("Tý"),
    SUU("Sửu"),
    DAN("Dần"),
    MAO("Mão"),
    THIN("Thìn"),
    TY_SNAKE("Tỵ"),
    NGO("Ngọ"),
    MUI("Mùi"),
    THAN("Thân"),
    DAU("Dậu"),
    TUAT("Tuất"),
    HOI("Hợi");

    private final String text;

    DiaChi(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static DiaChi fromIndex(int index) {
        DiaChi[] values = values();
        return values[Math.floorMod(index, values.length)];
    }
}

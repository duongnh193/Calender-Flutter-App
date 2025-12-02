package com.duong.lichvanien.calendar.lunar.external;

/**
 * Thiên Can enumeration with Vietnamese labels.
 */
public enum ThienCan {
    GIAP("Giáp"),
    AT("Ất"),
    BINH("Bính"),
    DINH("Đinh"),
    MAU("Mậu"),
    KY("Kỷ"),
    CANH("Canh"),
    TAN("Tân"),
    NHAM("Nhâm"),
    QUY("Quý");

    private final String text;

    ThienCan(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ThienCan fromIndex(int index) {
        ThienCan[] values = values();
        return values[Math.floorMod(index, values.length)];
    }
}

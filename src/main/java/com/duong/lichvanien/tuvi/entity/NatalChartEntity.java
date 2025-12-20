package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for storing natal chart FACT data.
 * This is the source of truth for Tu Vi chart calculations.
 * All interpretation data references this via chart_hash.
 */
@Entity
@Table(name = "natal_chart",
       uniqueConstraints = @UniqueConstraint(name = "uk_chart_hash", columnNames = {"chart_hash"}),
       indexes = {
           @Index(name = "idx_chart_hash", columnList = "chart_hash"),
           @Index(name = "idx_lunar_date", columnList = "lunar_year, lunar_month, lunar_day"),
           @Index(name = "idx_gender", columnList = "gender"),
           @Index(name = "idx_solar_date", columnList = "solar_date")
       })
@Getter
@Setter
public class NatalChartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chart_hash", nullable = false, unique = true, length = 64)
    private String chartHash;

    // Birth input
    @Column(name = "solar_date", nullable = false)
    private LocalDate solarDate;

    @Column(name = "birth_hour", nullable = false)
    private Integer birthHour;

    @Column(name = "birth_minute", nullable = false)
    private Integer birthMinute = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "is_lunar", nullable = false)
    private Boolean isLunar = false;

    @Column(name = "is_leap_month", nullable = false)
    private Boolean isLeapMonth = false;

    // Calculated lunar date (FACT)
    @Column(name = "lunar_year", nullable = false)
    private Integer lunarYear;

    @Column(name = "lunar_month", nullable = false)
    private Integer lunarMonth;

    @Column(name = "lunar_day", nullable = false)
    private Integer lunarDay;

    @Column(name = "lunar_year_can_chi", nullable = false, length = 32)
    private String lunarYearCanChi;

    @Column(name = "lunar_month_can_chi", nullable = false, length = 32)
    private String lunarMonthCanChi;

    @Column(name = "lunar_day_can_chi", nullable = false, length = 32)
    private String lunarDayCanChi;

    @Column(name = "birth_hour_can_chi", nullable = false, length = 32)
    private String birthHourCanChi;

    @Column(name = "hour_branch_index", nullable = false)
    private Integer hourBranchIndex;

    // Destiny calculations (FACT)
    @Column(name = "ban_menh", nullable = false, length = 64)
    private String banMenh;

    @Column(name = "ban_menh_ngu_hanh", nullable = false, length = 16)
    private String banMenhNguHanh;

    @Column(name = "cuc_name", nullable = false, length = 64)
    private String cucName;

    @Column(name = "cuc_value", nullable = false)
    private Integer cucValue;

    @Column(name = "cuc_ngu_hanh", nullable = false, length = 16)
    private String cucNguHanh;

    @Column(name = "am_duong", nullable = false, length = 16)
    private String amDuong;

    @Column(name = "thuan_nghich", nullable = false, length = 64)
    private String thuanNghich;

    // Main stars (FACT)
    @Column(name = "chu_menh_star_code", nullable = false, length = 32)
    private String chuMenhStarCode;

    @Column(name = "chu_than_star_code", length = 32)
    private String chuThanStarCode;

    @Column(name = "than_cu_palace_code", nullable = false, length = 16)
    private String thanCuPalaceCode;

    // Tuần/Triệt positions (FACT)
    @Column(name = "tuan_start_chi", nullable = false, length = 16)
    private String tuanStartChi;

    @Column(name = "tuan_end_chi", nullable = false, length = 16)
    private String tuanEndChi;

    @Column(name = "triet_start_chi", nullable = false, length = 16)
    private String trietStartChi;

    @Column(name = "triet_end_chi", nullable = false, length = 16)
    private String trietEndChi;

    // Metadata
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }

    public enum Gender {
        male, female
    }
}

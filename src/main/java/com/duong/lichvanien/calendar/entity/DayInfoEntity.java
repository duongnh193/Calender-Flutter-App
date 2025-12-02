package com.duong.lichvanien.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "day_info")
@Getter
@Setter
public class DayInfoEntity {

    @Id
    @Column(name = "solar_date", nullable = false)
    private LocalDate solarDate;

    @Column(name = "weekday", nullable = false)
    private int weekday;

    @Column(name = "lunar_day", nullable = false)
    private int lunarDay;

    @Column(name = "lunar_month", nullable = false)
    private int lunarMonth;

    @Column(name = "lunar_year", nullable = false)
    private int lunarYear;

    @Column(name = "lunar_leap_month", nullable = false)
    private int lunarLeapMonth;

    @Column(name = "can_chi_day", nullable = false, length = 16)
    private String canChiDay;

    @Column(name = "can_chi_month", nullable = false, length = 16)
    private String canChiMonth;

    @Column(name = "can_chi_year", nullable = false, length = 16)
    private String canChiYear;

    @Column(name = "is_good_day", nullable = false)
    private boolean goodDay;

    @Column(name = "note")
    private String note;
}

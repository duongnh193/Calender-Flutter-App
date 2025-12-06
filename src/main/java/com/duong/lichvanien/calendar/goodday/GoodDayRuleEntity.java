package com.duong.lichvanien.calendar.goodday;

import com.duong.lichvanien.calendar.entity.GoodDayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "good_day_rule")
@Getter
@Setter
public class GoodDayRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lunar_month", nullable = false)
    private int lunarMonth;

    @Column(name = "branch_code", nullable = false, length = 16)
    private String branchCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "fortune_type", nullable = false)
    private GoodDayType fortuneType;
}

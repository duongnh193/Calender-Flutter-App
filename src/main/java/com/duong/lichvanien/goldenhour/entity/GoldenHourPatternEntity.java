package com.duong.lichvanien.goldenhour.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "golden_hour_pattern")
@Getter
@Setter
public class GoldenHourPatternEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_branch_code", length = 16, nullable = false, unique = true)
    private String dayBranchCode;

    @Column(name = "good_branch_codes", length = 255, nullable = false)
    private String goodBranchCodes;
}

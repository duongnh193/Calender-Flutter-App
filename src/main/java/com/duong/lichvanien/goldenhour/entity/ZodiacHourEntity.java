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
@Table(name = "zodiac_hour")
@Getter
@Setter
public class ZodiacHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_code", length = 16, nullable = false, unique = true)
    private String branchCode;

    @Column(name = "start_hour", nullable = false)
    private int startHour;

    @Column(name = "end_hour", nullable = false)
    private int endHour;
}

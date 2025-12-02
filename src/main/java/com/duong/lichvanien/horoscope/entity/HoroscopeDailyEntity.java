package com.duong.lichvanien.horoscope.entity;

import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "horoscope_daily")
@Getter
@Setter
public class HoroscopeDailyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zodiac_id", nullable = false)
    private ZodiacEntity zodiac;

    @Column(name = "solar_date", nullable = false)
    private LocalDate solarDate;

    @Column(name = "general", columnDefinition = "TEXT")
    private String general;

    @Column(name = "love", columnDefinition = "TEXT")
    private String love;

    @Column(name = "career", columnDefinition = "TEXT")
    private String career;

    @Column(name = "finance", columnDefinition = "TEXT")
    private String finance;

    @Column(name = "health", columnDefinition = "TEXT")
    private String health;

    @Column(name = "lucky_color")
    private String luckyColor;

    @Column(name = "lucky_number")
    private String luckyNumber;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

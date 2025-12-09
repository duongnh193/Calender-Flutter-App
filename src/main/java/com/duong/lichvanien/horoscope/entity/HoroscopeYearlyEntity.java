package com.duong.lichvanien.horoscope.entity;

import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "horoscope_yearly")
@Getter
@Setter
public class HoroscopeYearlyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zodiac_id", nullable = false)
    private ZodiacEntity zodiac;

    @Column(name = "`year`", nullable = false)
    private int year;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "love", columnDefinition = "TEXT")
    private String love;

    @Column(name = "career", columnDefinition = "TEXT")
    private String career;

    @Column(name = "fortune", columnDefinition = "TEXT")
    private String fortune;

    @Column(name = "health", columnDefinition = "TEXT")
    private String health;

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

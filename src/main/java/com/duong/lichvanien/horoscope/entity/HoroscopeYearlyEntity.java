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

    @Column(name = "finance", columnDefinition = "TEXT")
    private String finance;

    @Column(name = "health", columnDefinition = "TEXT")
    private String health;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

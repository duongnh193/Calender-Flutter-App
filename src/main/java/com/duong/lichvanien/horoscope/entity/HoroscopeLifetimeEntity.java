package com.duong.lichvanien.horoscope.entity;

import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "horoscope_lifetime")
@Getter
@Setter
public class HoroscopeLifetimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zodiac_id", nullable = false)
    private ZodiacEntity zodiac;

    @Column(name = "can_chi", nullable = false, length = 64)
    private String canChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "career", columnDefinition = "TEXT")
    private String career;

    @Column(name = "love", columnDefinition = "TEXT")
    private String love;

    @Column(name = "health", columnDefinition = "TEXT")
    private String health;

    @Column(name = "family", columnDefinition = "TEXT")
    private String family;

    @Column(name = "fortune", columnDefinition = "TEXT")
    private String fortune;

    @Column(name = "unlucky", columnDefinition = "TEXT")
    private String unlucky;

    @Column(name = "advice", columnDefinition = "TEXT")
    private String advice;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Gender {
        male, female
    }
}


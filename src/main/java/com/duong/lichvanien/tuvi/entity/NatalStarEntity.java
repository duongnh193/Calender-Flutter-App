package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for storing star FACT data in natal palaces.
 * Multiple stars can be in one palace, each with exact properties and order.
 */
@Entity
@Table(name = "natal_star",
       indexes = {
           @Index(name = "idx_natal_palace_id", columnList = "natal_palace_id"),
           @Index(name = "idx_star_code", columnList = "star_code"),
           @Index(name = "idx_star_type", columnList = "star_type"),
           @Index(name = "idx_star_order", columnList = "star_order")
       })
@Getter
@Setter
public class NatalStarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "natal_palace_id", nullable = false, foreignKey = @ForeignKey(name = "fk_star_natal_palace"))
    private NatalPalaceEntity natalPalace;

    // Star identification (FACT)
    @Column(name = "star_code", nullable = false, length = 32)
    private String starCode;

    @Column(name = "star_type", nullable = false, length = 32)
    private String starType;

    @Column(name = "star_ngu_hanh", length = 16)
    private String starNguHanh;

    // Brightness (FACT - calculated from position and cục)
    @Column(name = "brightness", nullable = false, length = 32)
    private String brightness;

    @Column(name = "brightness_code", length = 4)
    private String brightnessCode;

    // Star properties (FACT)
    @Column(name = "is_positive")
    private Boolean isPositive;

    // Order in palace (FACT - important for interpretation)
    @Column(name = "star_order", nullable = false)
    private Integer starOrder;
}

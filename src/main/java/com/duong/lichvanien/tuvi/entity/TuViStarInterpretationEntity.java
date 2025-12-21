package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for storing interpretation of a single star within a palace.
 * Multiple stars can be in one palace, each with its own interpretation.
 */
@Entity
@Table(name = "tuvi_star_interpretation",
       indexes = {
           @Index(name = "idx_palace_interpretation_id", columnList = "palace_interpretation_id"),
           @Index(name = "idx_star_code", columnList = "star_code")
       })
@Getter
@Setter
public class TuViStarInterpretationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "palace_interpretation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_star_palace_interpretation"))
    private TuViPalaceInterpretationEntity palaceInterpretation;

    /**
     * Star code for reference only (e.g., "TU_VI", "THAM_LANG").
     * This is NOT FACT storage - FACT data (code, type, brightness, etc.) is in natal_star table.
     * Used only to match interpretation with the correct star.
     */
    @Column(name = "star_code", nullable = false, length = 32)
    private String starCode;

    // Interpretation content (ONLY interpretation, NO FACT)
    @Column(name = "interpretation", columnDefinition = "TEXT")
    private String interpretation;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
}

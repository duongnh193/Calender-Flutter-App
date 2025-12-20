package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity for storing interpretation of a single palace in Tu Vi chart.
 * One interpretation has 12 palace interpretations (one for each palace).
 */
@Entity
@Table(name = "tuvi_palace_interpretation",
       uniqueConstraints = @UniqueConstraint(name = "uk_interpretation_palace", columnNames = {"interpretation_id", "palace_code"}),
       indexes = {
           @Index(name = "idx_interpretation_id", columnList = "interpretation_id"),
           @Index(name = "idx_palace_code", columnList = "palace_code")
       })
@Getter
@Setter
public class TuViPalaceInterpretationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interpretation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_palace_interpretation"))
    private TuViInterpretationEntity interpretation;

    /**
     * Palace code for reference only (e.g., "MENH", "QUAN_LOC").
     * This is NOT FACT storage - FACT data is in natal_palace table.
     * Used only to match interpretation with the correct palace.
     */
    @Column(name = "palace_code", nullable = false, length = 16)
    private String palaceCode;

    // Interpretation content (ONLY interpretation, NO FACT)
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "detailed_analysis", columnDefinition = "TEXT")
    private String detailedAnalysis;

    @Column(name = "gender_analysis", columnDefinition = "TEXT")
    private String genderAnalysis;

    /**
     * Interpretation of Tuần/Triệt effects.
     * This is INTERPRETATION, not FACT (hasTuan/hasTriet are in natal_palace).
     */
    @Column(name = "tuan_triet_effect", columnDefinition = "TEXT")
    private String tuanTrietEffect;

    @Column(name = "advice_section", columnDefinition = "TEXT")
    private String adviceSection;

    @Column(name = "conclusion", columnDefinition = "TEXT")
    private String conclusion;

    @OneToMany(mappedBy = "palaceInterpretation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TuViStarInterpretationEntity> starInterpretations = new ArrayList<>();
}

package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for storing Tu Vi chart interpretations in database.
 * Main table containing overview section and metadata.
 */
@Entity
@Table(name = "tuvi_interpretation", 
       uniqueConstraints = @UniqueConstraint(name = "uk_chart_hash_gender", columnNames = {"chart_hash", "gender"}),
       indexes = {
           @Index(name = "idx_chart_hash", columnList = "chart_hash"),
           @Index(name = "idx_gender", columnList = "gender"),
           @Index(name = "idx_birth_date", columnList = "birth_date")
       })
@Getter
@Setter
public class TuViInterpretationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to FACT (chart_hash in natal_chart table)
    @Column(name = "chart_hash", nullable = false, length = 64)
    private String chartHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    /**
     * Overview section data stored as JSON.
     * This contains ONLY interpretation content, NOT FACT data.
     * All FACT data should be retrieved from natal_chart via chart_hash.
     */
    @Column(name = "overview_data", nullable = false, columnDefinition = "JSON")
    private String overviewData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Gender {
        male, female
    }
}

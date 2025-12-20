package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity for interpretation rules - maps FACT conditions to fragments.
 */
@Entity
@Table(name = "interpretation_rules",
       indexes = {
           @Index(name = "idx_fragment_code", columnList = "fragment_code")
       })
@Getter
@Setter
public class InterpretationRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fragment_code", nullable = false, length = 64)
    private String fragmentCode;

    @Column(name = "conditions", nullable = false, columnDefinition = "JSON")
    private String conditions; // JSON string

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Note: fragment_code is just a string reference, not a foreign key
    // Use InterpretationFragmentRepository.findByFragmentCode() to get the fragment entity if needed

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

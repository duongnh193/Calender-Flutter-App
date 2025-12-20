package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity for interpretation fragments - reusable Tu Vi interpretation units.
 */
@Entity
@Table(name = "interpretation_fragments",
       indexes = {
           @Index(name = "idx_fragment_code", columnList = "fragment_code"),
           @Index(name = "idx_tone", columnList = "tone"),
           @Index(name = "idx_priority", columnList = "priority")
       })
@Getter
@Setter
public class InterpretationFragmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fragment_code", nullable = false, unique = true, length = 64)
    private String fragmentCode;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "tone", nullable = false, length = 16)
    private Tone tone;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public enum Tone {
        positive, neutral, negative
    }
}

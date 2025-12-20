package com.duong.lichvanien.tuvi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity for storing palace FACT data in natal chart.
 * One natal chart has 12 palaces.
 */
@Entity
@Table(name = "natal_palace",
       uniqueConstraints = @UniqueConstraint(name = "uk_chart_palace", columnNames = {"natal_chart_id", "palace_code"}),
       indexes = {
           @Index(name = "idx_natal_chart_id", columnList = "natal_chart_id"),
           @Index(name = "idx_palace_code", columnList = "palace_code"),
           @Index(name = "idx_palace_index", columnList = "palace_index")
       })
@Getter
@Setter
public class NatalPalaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "natal_chart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_palace_natal_chart"))
    private NatalChartEntity natalChart;

    // Palace identification (FACT)
    @Column(name = "palace_index", nullable = false)
    private Integer palaceIndex;

    @Column(name = "palace_code", nullable = false, length = 16)
    private String palaceCode;

    @Column(name = "palace_chi", nullable = false, length = 16)
    private String palaceChi;

    @Column(name = "can_chi_prefix", length = 32)
    private String canChiPrefix;

    // Trường Sinh stage (FACT)
    @Column(name = "truong_sinh_stage", length = 32)
    private String truongSinhStage;

    // Đại Vận (FACT)
    @Column(name = "dai_van_start_age")
    private Integer daiVanStartAge;

    @Column(name = "dai_van_label", length = 32)
    private String daiVanLabel;

    // Tuần/Triệt (FACT)
    @Column(name = "has_tuan", nullable = false)
    private Boolean hasTuan = false;

    @Column(name = "has_triet", nullable = false)
    private Boolean hasTriet = false;

    // Thân cư (FACT)
    @Column(name = "is_than_cu", nullable = false)
    private Boolean isThanCu = false;

    @OneToMany(mappedBy = "natalPalace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NatalStarEntity> stars = new ArrayList<>();
}

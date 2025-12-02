package com.duong.lichvanien.zodiac.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zodiac")
@Getter
@Setter
public class ZodiacEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 16)
    private String code;

    @Column(name = "name_vi", nullable = false, length = 32)
    private String nameVi;

    @Column(name = "order_no", nullable = false)
    private int orderNo;
}

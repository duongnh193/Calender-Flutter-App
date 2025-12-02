package com.duong.lichvanien.zodiac.repository;

import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZodiacRepository extends JpaRepository<ZodiacEntity, Long> {
    Optional<ZodiacEntity> findByCode(String code);
}

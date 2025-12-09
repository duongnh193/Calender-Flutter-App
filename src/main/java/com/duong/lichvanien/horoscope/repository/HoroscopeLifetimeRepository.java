package com.duong.lichvanien.horoscope.repository;

import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HoroscopeLifetimeRepository extends JpaRepository<HoroscopeLifetimeEntity, Long> {

    Optional<HoroscopeLifetimeEntity> findByCanChiAndGender(String canChi, HoroscopeLifetimeEntity.Gender gender);

    Optional<HoroscopeLifetimeEntity> findByZodiacAndCanChiAndGender(
            ZodiacEntity zodiac, String canChi, HoroscopeLifetimeEntity.Gender gender);

    @Query("SELECT h FROM HoroscopeLifetimeEntity h WHERE " +
           "LOWER(REPLACE(h.canChi, ' ', '')) = LOWER(REPLACE(:canChi, ' ', '')) " +
           "AND h.gender = :gender")
    Optional<HoroscopeLifetimeEntity> findByNormalizedCanChiAndGender(
            @Param("canChi") String canChi,
            @Param("gender") HoroscopeLifetimeEntity.Gender gender);
}


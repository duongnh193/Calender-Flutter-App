package com.duong.lichvanien.horoscope.repository;

import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoroscopeLifetimeRepository extends JpaRepository<HoroscopeLifetimeEntity, Long> {

    Optional<HoroscopeLifetimeEntity> findByCanChiAndGender(String canChi, HoroscopeLifetimeEntity.Gender gender);

    Optional<HoroscopeLifetimeEntity> findByZodiacAndCanChiAndGender(
            ZodiacEntity zodiac, String canChi, HoroscopeLifetimeEntity.Gender gender);

    /**
     * Find by normalized Can-Chi (ignoring spaces and case) and gender.
     * Returns first match if multiple exist due to data issues.
     */
    @Query("SELECT h FROM HoroscopeLifetimeEntity h WHERE " +
           "LOWER(REPLACE(h.canChi, ' ', '')) = LOWER(REPLACE(:canChi, ' ', '')) " +
           "AND h.gender = :gender " +
           "ORDER BY h.id ASC")
    List<HoroscopeLifetimeEntity> findAllByNormalizedCanChiAndGender(
            @Param("canChi") String canChi,
            @Param("gender") HoroscopeLifetimeEntity.Gender gender);

    /**
     * Find first match by normalized Can-Chi and gender.
     */
    default Optional<HoroscopeLifetimeEntity> findByNormalizedCanChiAndGender(
            String canChi, HoroscopeLifetimeEntity.Gender gender) {
        List<HoroscopeLifetimeEntity> results = findAllByNormalizedCanChiAndGender(canChi, gender);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}


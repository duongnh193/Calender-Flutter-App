package com.duong.lichvanien.goldenhour.repository;

import com.duong.lichvanien.goldenhour.entity.GoldenHourPatternEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoldenHourPatternRepository extends JpaRepository<GoldenHourPatternEntity, Long> {

    Optional<GoldenHourPatternEntity> findByDayBranchCode(String branchCode);
}

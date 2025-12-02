package com.duong.lichvanien.goldenhour.repository;

import com.duong.lichvanien.goldenhour.entity.ZodiacHourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ZodiacHourRepository extends JpaRepository<ZodiacHourEntity, Long> {

    List<ZodiacHourEntity> findByBranchCodeIn(Collection<String> branchCodes);
}

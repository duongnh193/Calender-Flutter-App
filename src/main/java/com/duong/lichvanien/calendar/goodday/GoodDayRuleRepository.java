package com.duong.lichvanien.calendar.goodday;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodDayRuleRepository extends JpaRepository<GoodDayRuleEntity, Long> {
    Optional<GoodDayRuleEntity> findByLunarMonthAndBranchCode(int lunarMonth, String branchCode);
}

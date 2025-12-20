package com.duong.lichvanien.tuvi.repository;

import com.duong.lichvanien.tuvi.entity.InterpretationRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InterpretationRuleEntity.
 */
@Repository
public interface InterpretationRuleRepository extends JpaRepository<InterpretationRuleEntity, Long> {

    /**
     * Find all rules for a specific fragment code.
     */
    List<InterpretationRuleEntity> findByFragmentCode(String fragmentCode);

    /**
     * Find all rules that might match a given palace.
     * Note: This is a simple filter - actual matching requires JSON parsing in service layer.
     * JSON_EXTRACT returns JSON string with quotes, so we compare with JSON_UNQUOTE or quoted string.
     */
    @Query(value = "SELECT * FROM interpretation_rules WHERE JSON_UNQUOTE(JSON_EXTRACT(conditions, '$.palace')) = :palaceCode", 
           nativeQuery = true)
    List<InterpretationRuleEntity> findByPalaceCode(@Param("palaceCode") String palaceCode);
}

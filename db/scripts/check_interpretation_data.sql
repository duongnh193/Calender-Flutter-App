-- Script để kiểm tra data interpretation trong database
-- Chạy: mysql -u root -p lich_van_nien < check_interpretation_data.sql

-- 1. Kiểm tra fragments
SELECT '=== FRAGMENTS ===' as '';
SELECT COUNT(*) as total_fragments FROM interpretation_fragments;
SELECT COUNT(*) as menh_fragments FROM interpretation_fragments WHERE fragment_code LIKE '%_MENH_%';
SELECT COUNT(*) as combination_fragments FROM interpretation_fragments 
    WHERE fragment_code LIKE 'TU_PHU%' 
       OR fragment_code LIKE 'SAT_PHA_THAM%' 
       OR fragment_code LIKE 'KHONG_KIEP%' 
       OR fragment_code LIKE 'KINH_DA%' 
       OR fragment_code LIKE 'XUONG_KHUC%';
SELECT COUNT(*) as tuan_triet_fragments FROM interpretation_fragments 
    WHERE fragment_code LIKE 'TUAN%' OR fragment_code LIKE 'TRIET%';

-- Sample fragments
SELECT '=== SAMPLE FRAGMENTS ===' as '';
SELECT fragment_code, LEFT(content, 50) as content_preview, tone, priority 
FROM interpretation_fragments 
WHERE fragment_code LIKE '%_MENH_%' 
ORDER BY priority, fragment_code 
LIMIT 5;

-- 2. Kiểm tra rules
SELECT '=== RULES ===' as '';
SELECT COUNT(*) as total_rules FROM interpretation_rules;
SELECT COUNT(*) as menh_rules FROM interpretation_rules WHERE fragment_code LIKE '%_MENH_%';
SELECT COUNT(*) as combination_rules FROM interpretation_rules 
    WHERE fragment_code LIKE 'TU_PHU%' 
       OR fragment_code LIKE 'SAT_PHA_THAM%' 
       OR fragment_code LIKE 'KHONG_KIEP%' 
       OR fragment_code LIKE 'KINH_DA%' 
       OR fragment_code LIKE 'XUONG_KHUC%';
SELECT COUNT(*) as tuan_triet_rules FROM interpretation_rules 
    WHERE fragment_code LIKE 'TUAN%' OR fragment_code LIKE 'TRIET%';

-- Sample rules
SELECT '=== SAMPLE RULES ===' as '';
SELECT fragment_code, JSON_PRETTY(conditions) as conditions
FROM interpretation_rules 
WHERE fragment_code LIKE '%_MENH_%' 
LIMIT 3;

-- 3. Kiểm tra orphaned rules (rules không có fragment tương ứng)
SELECT '=== ORPHANED RULES (should be 0) ===' as '';
SELECT COUNT(*) as orphaned_rules 
FROM interpretation_rules r 
LEFT JOIN interpretation_fragments f ON r.fragment_code = f.fragment_code 
WHERE f.fragment_code IS NULL;

-- 4. Kiểm tra FACT data (natal charts)
SELECT '=== FACT DATA (NATAL CHARTS) ===' as '';
SELECT COUNT(*) as total_charts FROM natal_chart;
SELECT COUNT(*) as total_palaces FROM natal_palace;
SELECT COUNT(*) as total_stars FROM natal_star;

-- Sample chart
SELECT '=== SAMPLE CHART ===' as '';
SELECT id, chart_hash, gender, cuc_value, created_at 
FROM natal_chart 
ORDER BY created_at DESC 
LIMIT 1;

-- 5. Kiểm tra interpretations đã generate
SELECT '=== GENERATED INTERPRETATIONS ===' as '';
SELECT COUNT(*) as total_interpretations FROM tuvi_interpretation;
SELECT chart_hash, gender, created_at, ai_model 
FROM tuvi_interpretation 
ORDER BY created_at DESC 
LIMIT 5;

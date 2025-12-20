-- ============================================================
-- RULES BỔ SUNG CHO CÁC TRƯỜNG HỢP CÒN THIẾU (PHẦN CUỐI)
-- 1. THIEN_CO_MENH (5 brightness levels)
-- 2. THIEN_DONG_MENH (5 brightness levels)
-- 3. THIEN_TUONG_MENH (5 brightness levels)
-- 4. THAI_AM_MENH (5 brightness levels)
-- 5. VU_KHUC_MENH (5 brightness levels)
-- 6. CU_MON_MENH (5 brightness levels)
-- 7. LIEM_TRINH_MENH (5 brightness levels)
-- 8. THIEN_PHU_TAT_ACH (5 brightness levels)
-- 9. THAT_SAT_TAI_BACH (5 brightness levels)
-- 10. PHA_QUAN_TAI_BACH (5 brightness levels)
-- Total: 50 rules
-- ============================================================

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES

-- THIEN_CO tại MENH
('THIEN_CO_MENH_MIEU', '{"type": "PALACE", "stars": ["THIEN_CO"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THIEN_CO_MENH_VUONG', '{"type": "PALACE", "stars": ["THIEN_CO"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THIEN_CO_MENH_DAC', '{"type": "PALACE", "stars": ["THIEN_CO"], "palace": "MENH", "brightness": ["DAC"]}'),
('THIEN_CO_MENH_BINH', '{"type": "PALACE", "stars": ["THIEN_CO"], "palace": "MENH", "brightness": ["BINH"]}'),
('THIEN_CO_MENH_HAM', '{"type": "PALACE", "stars": ["THIEN_CO"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THIEN_DONG tại MENH
('THIEN_DONG_MENH_MIEU', '{"type": "PALACE", "stars": ["THIEN_DONG"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THIEN_DONG_MENH_VUONG', '{"type": "PALACE", "stars": ["THIEN_DONG"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THIEN_DONG_MENH_DAC', '{"type": "PALACE", "stars": ["THIEN_DONG"], "palace": "MENH", "brightness": ["DAC"]}'),
('THIEN_DONG_MENH_BINH', '{"type": "PALACE", "stars": ["THIEN_DONG"], "palace": "MENH", "brightness": ["BINH"]}'),
('THIEN_DONG_MENH_HAM', '{"type": "PALACE", "stars": ["THIEN_DONG"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THIEN_TUONG tại MENH
('THIEN_TUONG_MENH_MIEU', '{"type": "PALACE", "stars": ["THIEN_TUONG"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THIEN_TUONG_MENH_VUONG', '{"type": "PALACE", "stars": ["THIEN_TUONG"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THIEN_TUONG_MENH_DAC', '{"type": "PALACE", "stars": ["THIEN_TUONG"], "palace": "MENH", "brightness": ["DAC"]}'),
('THIEN_TUONG_MENH_BINH', '{"type": "PALACE", "stars": ["THIEN_TUONG"], "palace": "MENH", "brightness": ["BINH"]}'),
('THIEN_TUONG_MENH_HAM', '{"type": "PALACE", "stars": ["THIEN_TUONG"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THAI_AM tại MENH
('THAI_AM_MENH_MIEU', '{"type": "PALACE", "stars": ["THAI_AM"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THAI_AM_MENH_VUONG', '{"type": "PALACE", "stars": ["THAI_AM"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THAI_AM_MENH_DAC', '{"type": "PALACE", "stars": ["THAI_AM"], "palace": "MENH", "brightness": ["DAC"]}'),
('THAI_AM_MENH_BINH', '{"type": "PALACE", "stars": ["THAI_AM"], "palace": "MENH", "brightness": ["BINH"]}'),
('THAI_AM_MENH_HAM', '{"type": "PALACE", "stars": ["THAI_AM"], "palace": "MENH", "brightness": ["HAM"]}'),

-- VU_KHUC tại MENH
('VU_KHUC_MENH_MIEU', '{"type": "PALACE", "stars": ["VU_KHUC"], "palace": "MENH", "brightness": ["MIEU"]}'),
('VU_KHUC_MENH_VUONG', '{"type": "PALACE", "stars": ["VU_KHUC"], "palace": "MENH", "brightness": ["VUONG"]}'),
('VU_KHUC_MENH_DAC', '{"type": "PALACE", "stars": ["VU_KHUC"], "palace": "MENH", "brightness": ["DAC"]}'),
('VU_KHUC_MENH_BINH', '{"type": "PALACE", "stars": ["VU_KHUC"], "palace": "MENH", "brightness": ["BINH"]}'),
('VU_KHUC_MENH_HAM', '{"type": "PALACE", "stars": ["VU_KHUC"], "palace": "MENH", "brightness": ["HAM"]}'),

-- CU_MON tại MENH
('CU_MON_MENH_MIEU', '{"type": "PALACE", "stars": ["CU_MON"], "palace": "MENH", "brightness": ["MIEU"]}'),
('CU_MON_MENH_VUONG', '{"type": "PALACE", "stars": ["CU_MON"], "palace": "MENH", "brightness": ["VUONG"]}'),
('CU_MON_MENH_DAC', '{"type": "PALACE", "stars": ["CU_MON"], "palace": "MENH", "brightness": ["DAC"]}'),
('CU_MON_MENH_BINH', '{"type": "PALACE", "stars": ["CU_MON"], "palace": "MENH", "brightness": ["BINH"]}'),
('CU_MON_MENH_HAM', '{"type": "PALACE", "stars": ["CU_MON"], "palace": "MENH", "brightness": ["HAM"]}'),

-- LIEM_TRINH tại MENH
('LIEM_TRINH_MENH_MIEU', '{"type": "PALACE", "stars": ["LIEM_TRINH"], "palace": "MENH", "brightness": ["MIEU"]}'),
('LIEM_TRINH_MENH_VUONG', '{"type": "PALACE", "stars": ["LIEM_TRINH"], "palace": "MENH", "brightness": ["VUONG"]}'),
('LIEM_TRINH_MENH_DAC', '{"type": "PALACE", "stars": ["LIEM_TRINH"], "palace": "MENH", "brightness": ["DAC"]}'),
('LIEM_TRINH_MENH_BINH', '{"type": "PALACE", "stars": ["LIEM_TRINH"], "palace": "MENH", "brightness": ["BINH"]}'),
('LIEM_TRINH_MENH_HAM', '{"type": "PALACE", "stars": ["LIEM_TRINH"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THIEN_PHU tại TAT_ACH
('THIEN_PHU_TAT_ACH_MIEU', '{"type": "PALACE", "stars": ["THIEN_PHU"], "palace": "TAT_ACH", "brightness": ["MIEU"]}'),
('THIEN_PHU_TAT_ACH_VUONG', '{"type": "PALACE", "stars": ["THIEN_PHU"], "palace": "TAT_ACH", "brightness": ["VUONG"]}'),
('THIEN_PHU_TAT_ACH_DAC', '{"type": "PALACE", "stars": ["THIEN_PHU"], "palace": "TAT_ACH", "brightness": ["DAC"]}'),
('THIEN_PHU_TAT_ACH_BINH', '{"type": "PALACE", "stars": ["THIEN_PHU"], "palace": "TAT_ACH", "brightness": ["BINH"]}'),
('THIEN_PHU_TAT_ACH_HAM', '{"type": "PALACE", "stars": ["THIEN_PHU"], "palace": "TAT_ACH", "brightness": ["HAM"]}'),

-- THAT_SAT tại TAI_BACH
('THAT_SAT_TAI_BACH_MIEU', '{"type": "PALACE", "stars": ["THAT_SAT"], "palace": "TAI_BACH", "brightness": ["MIEU"]}'),
('THAT_SAT_TAI_BACH_VUONG', '{"type": "PALACE", "stars": ["THAT_SAT"], "palace": "TAI_BACH", "brightness": ["VUONG"]}'),
('THAT_SAT_TAI_BACH_DAC', '{"type": "PALACE", "stars": ["THAT_SAT"], "palace": "TAI_BACH", "brightness": ["DAC"]}'),
('THAT_SAT_TAI_BACH_BINH', '{"type": "PALACE", "stars": ["THAT_SAT"], "palace": "TAI_BACH", "brightness": ["BINH"]}'),
('THAT_SAT_TAI_BACH_HAM', '{"type": "PALACE", "stars": ["THAT_SAT"], "palace": "TAI_BACH", "brightness": ["HAM"]}'),

-- PHA_QUAN tại TAI_BACH
('PHA_QUAN_TAI_BACH_MIEU', '{"type": "PALACE", "stars": ["PHA_QUAN"], "palace": "TAI_BACH", "brightness": ["MIEU"]}'),
('PHA_QUAN_TAI_BACH_VUONG', '{"type": "PALACE", "stars": ["PHA_QUAN"], "palace": "TAI_BACH", "brightness": ["VUONG"]}'),
('PHA_QUAN_TAI_BACH_DAC', '{"type": "PALACE", "stars": ["PHA_QUAN"], "palace": "TAI_BACH", "brightness": ["DAC"]}'),
('PHA_QUAN_TAI_BACH_BINH', '{"type": "PALACE", "stars": ["PHA_QUAN"], "palace": "TAI_BACH", "brightness": ["BINH"]}'),
('PHA_QUAN_TAI_BACH_HAM', '{"type": "PALACE", "stars": ["PHA_QUAN"], "palace": "TAI_BACH", "brightness": ["HAM"]}')
;

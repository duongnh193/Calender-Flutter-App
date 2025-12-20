-- ============================================================
-- RULES BỔ SUNG CHO CÁC TRƯỜNG HỢP CÒN THIẾU
-- 1. THIEN_LUONG_MENH (5 brightness levels)
-- 2. THAI_DUONG_MENH (5 brightness levels)
-- 3. THAM_LANG_TAI_BACH (5 brightness levels)
-- Total: 15 rules
-- ============================================================

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES

-- THIEN_LUONG tại MENH
('THIEN_LUONG_MENH_MIEU', '{"type": "PALACE", "stars": ["THIEN_LUONG"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THIEN_LUONG_MENH_VUONG', '{"type": "PALACE", "stars": ["THIEN_LUONG"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THIEN_LUONG_MENH_DAC', '{"type": "PALACE", "stars": ["THIEN_LUONG"], "palace": "MENH", "brightness": ["DAC"]}'),
('THIEN_LUONG_MENH_BINH', '{"type": "PALACE", "stars": ["THIEN_LUONG"], "palace": "MENH", "brightness": ["BINH"]}'),
('THIEN_LUONG_MENH_HAM', '{"type": "PALACE", "stars": ["THIEN_LUONG"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THAI_DUONG tại MENH
('THAI_DUONG_MENH_MIEU', '{"type": "PALACE", "stars": ["THAI_DUONG"], "palace": "MENH", "brightness": ["MIEU"]}'),
('THAI_DUONG_MENH_VUONG', '{"type": "PALACE", "stars": ["THAI_DUONG"], "palace": "MENH", "brightness": ["VUONG"]}'),
('THAI_DUONG_MENH_DAC', '{"type": "PALACE", "stars": ["THAI_DUONG"], "palace": "MENH", "brightness": ["DAC"]}'),
('THAI_DUONG_MENH_BINH', '{"type": "PALACE", "stars": ["THAI_DUONG"], "palace": "MENH", "brightness": ["BINH"]}'),
('THAI_DUONG_MENH_HAM', '{"type": "PALACE", "stars": ["THAI_DUONG"], "palace": "MENH", "brightness": ["HAM"]}'),

-- THAM_LANG tại TAI_BACH
('THAM_LANG_TAI_BACH_MIEU', '{"type": "PALACE", "stars": ["THAM_LANG"], "palace": "TAI_BACH", "brightness": ["MIEU"]}'),
('THAM_LANG_TAI_BACH_VUONG', '{"type": "PALACE", "stars": ["THAM_LANG"], "palace": "TAI_BACH", "brightness": ["VUONG"]}'),
('THAM_LANG_TAI_BACH_DAC', '{"type": "PALACE", "stars": ["THAM_LANG"], "palace": "TAI_BACH", "brightness": ["DAC"]}'),
('THAM_LANG_TAI_BACH_BINH', '{"type": "PALACE", "stars": ["THAM_LANG"], "palace": "TAI_BACH", "brightness": ["BINH"]}'),
('THAM_LANG_TAI_BACH_HAM', '{"type": "PALACE", "stars": ["THAM_LANG"], "palace": "TAI_BACH", "brightness": ["HAM"]}')
;

-- ============================================================
-- INTERPRETATION RULES: CUNG MỆNH - CHÍNH TINH
-- Mỗi rule kích hoạt 1 fragment khi điều kiện FACT thỏa mãn
-- ============================================================

-- Rules cho TỬ VI tại cung Mệnh

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_MENH_MIEU', '{"palace": "MENH", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_MENH_VUONG', '{"palace": "MENH", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_MENH_DAC', '{"palace": "MENH", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_MENH_BINH', '{"palace": "MENH", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_MENH_HAM', '{"palace": "MENH", "stars": ["TU_VI"], "brightness": ["HAM"]}');

-- Rules cho THIÊN PHỦ tại cung Mệnh

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THIEN_PHU_MENH_MIEU', '{"palace": "MENH", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_MENH_VUONG', '{"palace": "MENH", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_MENH_DAC', '{"palace": "MENH", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_MENH_BINH', '{"palace": "MENH", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_MENH_HAM', '{"palace": "MENH", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- Rules cho THAM LANG tại cung Mệnh

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THAM_LANG_MENH_MIEU', '{"palace": "MENH", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_MENH_VUONG', '{"palace": "MENH", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_MENH_DAC', '{"palace": "MENH", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_MENH_BINH', '{"palace": "MENH", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_MENH_HAM', '{"palace": "MENH", "stars": ["THAM_LANG"], "brightness": ["HAM"]}');

-- Rules cho THẤT SÁT tại cung Mệnh

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THAT_SAT_MENH_MIEU', '{"palace": "MENH", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_MENH_VUONG', '{"palace": "MENH", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_MENH_DAC', '{"palace": "MENH", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_MENH_BINH', '{"palace": "MENH", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_MENH_HAM', '{"palace": "MENH", "stars": ["THAT_SAT"], "brightness": ["HAM"]}');

-- Rules cho PHÁ QUÂN tại cung Mệnh

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('PHA_QUAN_MENH_MIEU', '{"palace": "MENH", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_MENH_VUONG', '{"palace": "MENH", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_MENH_DAC', '{"palace": "MENH", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_MENH_BINH', '{"palace": "MENH", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_MENH_HAM', '{"palace": "MENH", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}');

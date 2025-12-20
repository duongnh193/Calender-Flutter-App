-- ============================================================
-- RULES FOR DETAILED INTERPRETATION FRAGMENTS
-- Rules cho các fragments dài (detailed) cho các cung và Chủ mệnh/Chủ thân
-- Format: Match theo palace + star + brightness (cho cung)
--         Match theo role (CHU_MENH/CHU_THAN) + star (cho Chủ mệnh/Chủ thân)
-- ============================================================

-- ============================
-- RULES CHO CÁC CUNG (PALACE-LEVEL)
-- Format: {STAR_CODE}_{PALACE_CODE}_{BRIGHTNESS}
-- ============================

-- QUAN_LOC rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_QUAN_LOC_MIEU', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_QUAN_LOC_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_QUAN_LOC_DAC', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_QUAN_LOC_BINH', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_QUAN_LOC_HAM', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_QUAN_LOC_MIEU', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_QUAN_LOC_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_QUAN_LOC_DAC', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_QUAN_LOC_BINH', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_QUAN_LOC_HAM', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}'),

('THAM_LANG_QUAN_LOC_MIEU', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_QUAN_LOC_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_QUAN_LOC_DAC', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_QUAN_LOC_BINH', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_QUAN_LOC_HAM', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAT_SAT_QUAN_LOC_MIEU', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_QUAN_LOC_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_QUAN_LOC_DAC', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_QUAN_LOC_BINH', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_QUAN_LOC_HAM', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('PHA_QUAN_QUAN_LOC_MIEU', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_QUAN_LOC_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_QUAN_LOC_DAC', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_QUAN_LOC_BINH', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_QUAN_LOC_HAM', '{"type": "PALACE", "palace": "QUAN_LOC", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}');

-- TAI_BACH rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_TAI_BACH_MIEU', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_TAI_BACH_VUONG', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_TAI_BACH_DAC', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_TAI_BACH_BINH', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_TAI_BACH_HAM', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_TAI_BACH_MIEU', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_TAI_BACH_VUONG', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_TAI_BACH_DAC', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_TAI_BACH_BINH', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_TAI_BACH_HAM', '{"type": "PALACE", "palace": "TAI_BACH", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- PHU_THE rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_PHU_THE_MIEU', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_PHU_THE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_PHU_THE_DAC', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_PHU_THE_BINH', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_PHU_THE_HAM', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_PHU_THE_MIEU', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_PHU_THE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_PHU_THE_DAC', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_PHU_THE_BINH', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_PHU_THE_HAM', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- TAT_ACH rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_TAT_ACH_MIEU', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_TAT_ACH_VUONG', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_TAT_ACH_DAC', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_TAT_ACH_BINH', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_TAT_ACH_HAM', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["TU_VI"], "brightness": ["HAM"]}');

-- TU_TUC rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_TU_TUC_MIEU', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_TU_TUC_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_TU_TUC_DAC', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_TU_TUC_BINH', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_TU_TUC_HAM', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_TU_TUC_MIEU', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_TU_TUC_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_TU_TUC_DAC', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_TU_TUC_BINH', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_TU_TUC_HAM', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- RULES CHO CHỦ MỆNH / CHỦ THÂN (CENTER-LEVEL)
-- Format: CHU_MENH_{STAR_CODE} hoặc CHU_THAN_{STAR_CODE}
-- Note: Cần logic đặc biệt trong InterpretationRuleMatchingService để match
--       dựa trên center.getChuMenh() hoặc center.getChuThan()
-- ============================

-- CHỦ MỆNH rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('CHU_MENH_TU_VI', '{"type": "CENTER", "chuMenh": "Tử Vi"}'),
('CHU_MENH_THIEN_PHU', '{"type": "CENTER", "chuMenh": "Thiên Phủ"}'),
('CHU_MENH_THIEN_CO', '{"type": "CENTER", "chuMenh": "Thiên Cơ"}'),
('CHU_MENH_THAI_AM', '{"type": "CENTER", "chuMenh": "Thái Âm"}'),
('CHU_MENH_THAI_DUONG', '{"type": "CENTER", "chuMenh": "Thái Dương"}'),
('CHU_MENH_VU_KHUC', '{"type": "CENTER", "chuMenh": "Vũ Khúc"}'),
('CHU_MENH_THIEN_TUONG', '{"type": "CENTER", "chuMenh": "Thiên Tướng"}'),
('CHU_MENH_THIEN_LUONG', '{"type": "CENTER", "chuMenh": "Thiên Lương"}'),
('CHU_MENH_LIEM_TRINH', '{"type": "CENTER", "chuMenh": "Liêm Trinh"}'),
('CHU_MENH_THAT_SAT', '{"type": "CENTER", "chuMenh": "Thất Sát"}'),
('CHU_MENH_PHA_QUAN', '{"type": "CENTER", "chuMenh": "Phá Quân"}'),
('CHU_MENH_THAM_LANG', '{"type": "CENTER", "chuMenh": "Tham Lang"}');

-- CHỦ THÂN rules
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('CHU_THAN_TU_VI', '{"type": "CENTER", "chuThan": "Tử Vi"}'),
('CHU_THAN_THIEN_CO', '{"type": "CENTER", "chuThan": "Thiên Cơ"}'),
('CHU_THAN_THAI_AM', '{"type": "CENTER", "chuThan": "Thái Âm"}'),
('CHU_THAN_THIEN_LUONG', '{"type": "CENTER", "chuThan": "Thiên Lương"}'),
('CHU_THAN_THIEN_PHU', '{"type": "CENTER", "chuThan": "Thiên Phủ"}'),
('CHU_THAN_VAN_KHUC', '{"type": "CENTER", "chuThan": "Văn Khúc"}'),
('CHU_THAN_THIEN_DONG', '{"type": "CENTER", "chuThan": "Thiên Đồng"}');

-- ============================
-- PHUC_DUC rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_PHUC_DUC_MIEU', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_PHUC_DUC_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_PHUC_DUC_DAC', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_PHUC_DUC_BINH', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_PHUC_DUC_HAM', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_PHUC_DUC_MIEU', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_PHUC_DUC_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_PHUC_DUC_DAC', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_PHUC_DUC_BINH', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_PHUC_DUC_HAM', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- THIEN_DI rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_THIEN_DI_MIEU', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_THIEN_DI_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_THIEN_DI_DAC', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_THIEN_DI_BINH', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_THIEN_DI_HAM', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_THIEN_DI_MIEU', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_THIEN_DI_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_THIEN_DI_DAC', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_THIEN_DI_BINH', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_THIEN_DI_HAM', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- NO_BOC rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_NO_BOC_MIEU', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_NO_BOC_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_NO_BOC_DAC', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_NO_BOC_BINH', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_NO_BOC_HAM', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_NO_BOC_MIEU', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_NO_BOC_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_NO_BOC_DAC', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_NO_BOC_BINH', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_NO_BOC_HAM', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- DIEN_TRACH rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_DIEN_TRACH_MIEU', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_DIEN_TRACH_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_DIEN_TRACH_DAC', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_DIEN_TRACH_BINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_DIEN_TRACH_HAM', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_DIEN_TRACH_MIEU', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_DIEN_TRACH_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_DIEN_TRACH_DAC', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_DIEN_TRACH_BINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_DIEN_TRACH_HAM', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- PHU_MAU rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_PHU_MAU_MIEU', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_PHU_MAU_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_PHU_MAU_DAC', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_PHU_MAU_BINH', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_PHU_MAU_HAM', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_PHU_MAU_MIEU', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_PHU_MAU_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_PHU_MAU_DAC', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_PHU_MAU_BINH', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_PHU_MAU_HAM', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

-- ============================
-- HUYNH_DE rules
-- ============================
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_VI_HUYNH_DE_MIEU', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["TU_VI"], "brightness": ["MIEU"]}'),
('TU_VI_HUYNH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["TU_VI"], "brightness": ["VUONG"]}'),
('TU_VI_HUYNH_DE_DAC', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["TU_VI"], "brightness": ["DAC"]}'),
('TU_VI_HUYNH_DE_BINH', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["TU_VI"], "brightness": ["BINH"]}'),
('TU_VI_HUYNH_DE_HAM', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["TU_VI"], "brightness": ["HAM"]}'),

('THIEN_PHU_HUYNH_DE_MIEU', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THIEN_PHU"], "brightness": ["MIEU"]}'),
('THIEN_PHU_HUYNH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THIEN_PHU"], "brightness": ["VUONG"]}'),
('THIEN_PHU_HUYNH_DE_DAC', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THIEN_PHU"], "brightness": ["DAC"]}'),
('THIEN_PHU_HUYNH_DE_BINH', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THIEN_PHU"], "brightness": ["BINH"]}'),
('THIEN_PHU_HUYNH_DE_HAM', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THIEN_PHU"], "brightness": ["HAM"]}');

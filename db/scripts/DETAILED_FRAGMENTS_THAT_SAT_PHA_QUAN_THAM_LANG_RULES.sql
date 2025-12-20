-- ============================================================
-- RULES FOR DETAILED FRAGMENTS: THAT_SAT, PHA_QUAN, THAM_LANG
-- Cho 9 cung: TAT_ACH, DIEN_TRACH, PHU_THE, TU_TUC, PHUC_DUC, THIEN_DI, NO_BOC, PHU_MAU, HUYNH_DE
-- 5 brightness levels: MIEU, VUONG, DAC, BINH, HAM
-- Total: 3 stars × 9 palaces × 5 brightness = 135 rules
-- ============================================================

INSERT INTO interpretation_rules (fragment_code, conditions) VALUES

-- THAT_SAT rules
('THAT_SAT_TAT_ACH_MIEU', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_TAT_ACH_VUONG', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_TAT_ACH_DAC', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_TAT_ACH_BINH', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_TAT_ACH_HAM', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_DIEN_TRACH_MIEU', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_DIEN_TRACH_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_DIEN_TRACH_DAC', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_DIEN_TRACH_BINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_DIEN_TRACH_HAM', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_PHU_THE_MIEU', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_PHU_THE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_PHU_THE_DAC', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_PHU_THE_BINH', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_PHU_THE_HAM', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_TU_TUC_MIEU', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_TU_TUC_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_TU_TUC_DAC', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_TU_TUC_BINH', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_TU_TUC_HAM', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_PHUC_DUC_MIEU', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_PHUC_DUC_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_PHUC_DUC_DAC', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_PHUC_DUC_BINH', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_PHUC_DUC_HAM', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_THIEN_DI_MIEU', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_THIEN_DI_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_THIEN_DI_DAC', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_THIEN_DI_BINH', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_THIEN_DI_HAM', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_NO_BOC_MIEU', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_NO_BOC_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_NO_BOC_DAC', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_NO_BOC_BINH', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_NO_BOC_HAM', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_PHU_MAU_MIEU', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_PHU_MAU_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_PHU_MAU_DAC', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_PHU_MAU_BINH', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_PHU_MAU_HAM', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

('THAT_SAT_HUYNH_DE_MIEU', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAT_SAT"], "brightness": ["MIEU"]}'),
('THAT_SAT_HUYNH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAT_SAT"], "brightness": ["VUONG"]}'),
('THAT_SAT_HUYNH_DE_DAC', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAT_SAT"], "brightness": ["DAC"]}'),
('THAT_SAT_HUYNH_DE_BINH', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAT_SAT"], "brightness": ["BINH"]}'),
('THAT_SAT_HUYNH_DE_HAM', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAT_SAT"], "brightness": ["HAM"]}'),

-- PHA_QUAN rules
('PHA_QUAN_TAT_ACH_MIEU', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_TAT_ACH_VUONG', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_TAT_ACH_DAC', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_TAT_ACH_BINH', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_TAT_ACH_HAM', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_DIEN_TRACH_MIEU', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_DIEN_TRACH_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_DIEN_TRACH_DAC', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_DIEN_TRACH_BINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_DIEN_TRACH_HAM', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_PHU_THE_MIEU', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_PHU_THE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_PHU_THE_DAC', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_PHU_THE_BINH', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_PHU_THE_HAM', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_TU_TUC_MIEU', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_TU_TUC_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_TU_TUC_DAC', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_TU_TUC_BINH', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_TU_TUC_HAM', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_PHUC_DUC_MIEU', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_PHUC_DUC_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_PHUC_DUC_DAC', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_PHUC_DUC_BINH', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_PHUC_DUC_HAM', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_THIEN_DI_MIEU', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_THIEN_DI_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_THIEN_DI_DAC', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_THIEN_DI_BINH', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_THIEN_DI_HAM', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_NO_BOC_MIEU', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_NO_BOC_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_NO_BOC_DAC', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_NO_BOC_BINH', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_NO_BOC_HAM', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_PHU_MAU_MIEU', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_PHU_MAU_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_PHU_MAU_DAC', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_PHU_MAU_BINH', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_PHU_MAU_HAM', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

('PHA_QUAN_HUYNH_DE_MIEU', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["PHA_QUAN"], "brightness": ["MIEU"]}'),
('PHA_QUAN_HUYNH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["PHA_QUAN"], "brightness": ["VUONG"]}'),
('PHA_QUAN_HUYNH_DE_DAC', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["PHA_QUAN"], "brightness": ["DAC"]}'),
('PHA_QUAN_HUYNH_DE_BINH', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["PHA_QUAN"], "brightness": ["BINH"]}'),
('PHA_QUAN_HUYNH_DE_HAM', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["PHA_QUAN"], "brightness": ["HAM"]}'),

-- THAM_LANG rules
('THAM_LANG_TAT_ACH_MIEU', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_TAT_ACH_VUONG', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_TAT_ACH_DAC', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_TAT_ACH_BINH', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_TAT_ACH_HAM', '{"type": "PALACE", "palace": "TAT_ACH", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_DIEN_TRACH_MIEU', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_DIEN_TRACH_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_DIEN_TRACH_DAC', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_DIEN_TRACH_BINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_DIEN_TRACH_HAM', '{"type": "PALACE", "palace": "DIEN_TRACH", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_PHU_THE_MIEU', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_PHU_THE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_PHU_THE_DAC', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_PHU_THE_BINH', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_PHU_THE_HAM', '{"type": "PALACE", "palace": "PHU_THE", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_TU_TUC_MIEU', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_TU_TUC_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_TU_TUC_DAC', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_TU_TUC_BINH', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_TU_TUC_HAM', '{"type": "PALACE", "palace": "TU_TUC", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_PHUC_DUC_MIEU', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_PHUC_DUC_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_PHUC_DUC_DAC', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_PHUC_DUC_BINH', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_PHUC_DUC_HAM', '{"type": "PALACE", "palace": "PHUC_DUC", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_THIEN_DI_MIEU', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_THIEN_DI_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_THIEN_DI_DAC', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_THIEN_DI_BINH', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_THIEN_DI_HAM', '{"type": "PALACE", "palace": "THIEN_DI", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_NO_BOC_MIEU', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_NO_BOC_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_NO_BOC_DAC', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_NO_BOC_BINH', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_NO_BOC_HAM', '{"type": "PALACE", "palace": "NO_BOC", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_PHU_MAU_MIEU', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_PHU_MAU_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_PHU_MAU_DAC', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_PHU_MAU_BINH', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_PHU_MAU_HAM', '{"type": "PALACE", "palace": "PHU_MAU", "stars": ["THAM_LANG"], "brightness": ["HAM"]}'),

('THAM_LANG_HUYNH_DE_MIEU', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAM_LANG"], "brightness": ["MIEU"]}'),
('THAM_LANG_HUYNH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAM_LANG"], "brightness": ["VUONG"]}'),
('THAM_LANG_HUYNH_DE_DAC', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAM_LANG"], "brightness": ["DAC"]}'),
('THAM_LANG_HUYNH_DE_BINH', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAM_LANG"], "brightness": ["BINH"]}'),
('THAM_LANG_HUYNH_DE_HAM', '{"type": "PALACE", "palace": "HUYNH_DE", "stars": ["THAM_LANG"], "brightness": ["HAM"]}');

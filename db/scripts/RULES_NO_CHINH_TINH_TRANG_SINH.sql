-- ============================================================
-- RULES CHO TRƯỜNG HỢP CUNG KHÔNG CÓ CHÍNH TINH
-- Chỉ có vòng Tràng Sinh (12 trạng thái)
-- Format: Match khi palace không có Chính tinh và có trạng thái Tràng Sinh tương ứng
-- Mỗi fragment có rule cho tất cả 12 cung (dùng chung fragment)
-- ============================================================

-- Note: Logic matching cần được update để hỗ trợ:
-- 1. Check has_chinh_tinh: false
-- 2. Check trang_sinh_state: <STATE_NAME>
-- 3. Check palace code (vẫn cần thiết vì findByPalaceCode được dùng)

-- Rules cho NO_CHINH_TINH_TRUONG_SINH (Trường Sinh)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}'),
('NO_CHINH_TINH_TRUONG_SINH', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "TRUONG_SINH"}');

-- Rules cho NO_CHINH_TINH_MOC_DUC (Mộc Dục)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}'),
('NO_CHINH_TINH_MOC_DUC', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "MOC_DUC"}');

-- Rules cho NO_CHINH_TINH_QUAN_DAI (Quán Đái)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}'),
('NO_CHINH_TINH_QUAN_DAI', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "QUAN_DAI"}');

-- Rules cho NO_CHINH_TINH_LAM_QUAN (Lâm Quan)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}'),
('NO_CHINH_TINH_LAM_QUAN', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "LAM_QUAN"}');

-- Rules cho NO_CHINH_TINH_DE_VUONG (Đế Vượng)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}'),
('NO_CHINH_TINH_DE_VUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "DE_VUONG"}');

-- Rules cho NO_CHINH_TINH_SUY (Suy)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}'),
('NO_CHINH_TINH_SUY', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "SUY"}');

-- Rules cho NO_CHINH_TINH_BENH (Bệnh)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}'),
('NO_CHINH_TINH_BENH', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "BENH"}');

-- Rules cho NO_CHINH_TINH_TU (Tử)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "TU"}'),
('NO_CHINH_TINH_TU', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "TU"}');

-- Rules cho NO_CHINH_TINH_MO (Mộ)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "MO"}'),
('NO_CHINH_TINH_MO', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "MO"}');

-- Rules cho NO_CHINH_TINH_TUYET (Tuyệt)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}'),
('NO_CHINH_TINH_TUYET', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "TUYET"}');

-- Rules cho NO_CHINH_TINH_THAI (Thai)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}'),
('NO_CHINH_TINH_THAI', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "THAI"}');

-- Rules cho NO_CHINH_TINH_DUONG (Dưỡng)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "MENH", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "PHU_MAU", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "PHUC_DUC", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "DIEN_TRACH", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "QUAN_LOC", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "NO_BOC", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "THIEN_DI", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "TAT_ACH", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "TAI_BACH", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "TU_TUC", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "PHU_THE", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}'),
('NO_CHINH_TINH_DUONG', '{"type": "PALACE", "palace": "HUYNH_DE", "has_chinh_tinh": false, "trang_sinh_state": "DUONG"}');

-- ============================================================
-- STEP 8: OVERVIEW SECTION RULES - CENTER LEVEL
-- Rules cho các fragments trong OverviewSection
-- ============================================================

-- ============================
-- 1. RULES CHO BẢN MỆNH (Nạp Âm) theo Ngũ Hành
-- ============================

-- Bản mệnh KIM - base rule (chỉ cần banMenhNguHanh = KIM)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM"}');

-- Bản mệnh KIM + Cục
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_KIM_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM", "cucNguHanh": "KIM"}'),
('BAN_MENH_KIM_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM", "cucNguHanh": "MOC"}'),
('BAN_MENH_KIM_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM", "cucNguHanh": "THUY"}'),
('BAN_MENH_KIM_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM", "cucNguHanh": "HOA"}'),
('BAN_MENH_KIM_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "KIM", "cucNguHanh": "THO"}');

-- Bản mệnh MỘC
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC"}'),
('BAN_MENH_MOC_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC", "cucNguHanh": "KIM"}'),
('BAN_MENH_MOC_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC", "cucNguHanh": "MOC"}'),
('BAN_MENH_MOC_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC", "cucNguHanh": "THUY"}'),
('BAN_MENH_MOC_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC", "cucNguHanh": "HOA"}'),
('BAN_MENH_MOC_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "MOC", "cucNguHanh": "THO"}');

-- Bản mệnh THỦY
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY"}'),
('BAN_MENH_THUY_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY", "cucNguHanh": "KIM"}'),
('BAN_MENH_THUY_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY", "cucNguHanh": "MOC"}'),
('BAN_MENH_THUY_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY", "cucNguHanh": "THUY"}'),
('BAN_MENH_THUY_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY", "cucNguHanh": "HOA"}'),
('BAN_MENH_THUY_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "THUY", "cucNguHanh": "THO"}');

-- Bản mệnh HỎA
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA"}'),
('BAN_MENH_HOA_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA", "cucNguHanh": "KIM"}'),
('BAN_MENH_HOA_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA", "cucNguHanh": "MOC"}'),
('BAN_MENH_HOA_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA", "cucNguHanh": "THUY"}'),
('BAN_MENH_HOA_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA", "cucNguHanh": "HOA"}'),
('BAN_MENH_HOA_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "HOA", "cucNguHanh": "THO"}');

-- Bản mệnh THỔ
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('BAN_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "THO"}'),
('BAN_MENH_THO_MENH_KIM', '{"type": "OVERVIEW", "banMenhNguHanh": "THO", "cucNguHanh": "KIM"}'),
('BAN_MENH_THO_MENH_MOC', '{"type": "OVERVIEW", "banMenhNguHanh": "THO", "cucNguHanh": "MOC"}'),
('BAN_MENH_THO_MENH_THUY', '{"type": "OVERVIEW", "banMenhNguHanh": "THO", "cucNguHanh": "THUY"}'),
('BAN_MENH_THO_MENH_HOA', '{"type": "OVERVIEW", "banMenhNguHanh": "THO", "cucNguHanh": "HOA"}'),
('BAN_MENH_THO_MENH_THO', '{"type": "OVERVIEW", "banMenhNguHanh": "THO", "cucNguHanh": "THO"}');

-- ============================
-- 2. RULES CHO CỤC MỆNH
-- ============================

-- Note: Cục được xác định từ cucNguHanh và cucValue
-- Sử dụng cucName để match (e.g., "Kim tứ cục", "Thổ ngũ cục")
-- Hoặc dùng cucNguHanh để match đơn giản hơn
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('CUC_KIM_NGU', '{"type": "OVERVIEW", "cucNguHanh": "KIM"}'),
('CUC_MOC_NGU', '{"type": "OVERVIEW", "cucNguHanh": "MOC"}'),
('CUC_THUY_NHI', '{"type": "OVERVIEW", "cucNguHanh": "THUY"}'),
('CUC_HOA_LUC', '{"type": "OVERVIEW", "cucNguHanh": "HOA"}'),
('CUC_THO_NGU', '{"type": "OVERVIEW", "cucNguHanh": "THO"}');

-- ============================
-- 3. RULES CHO THUẬN / NGHỊCH
-- ============================

-- Note: ThuanNghich trong CenterInfo có format như "Dương nam - Thuận lý" hoặc "Âm nam - Nghịch lý"
-- Sử dụng pattern matching với LIKE hoặc contains để match
-- Format rule: check if thuanNghich contains "Thuận lý" or "Nghịch lý"
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THUAN_LY_DUONG_NAM', '{"type": "OVERVIEW", "thuanNghich_contains": "Thuận lý", "gender": "male", "amDuong": "Dương"}'),
('THUAN_LY_AM_NU', '{"type": "OVERVIEW", "thuanNghich_contains": "Thuận lý", "gender": "female", "amDuong": "Âm"}'),
('NGHICH_LY_AM_NAM', '{"type": "OVERVIEW", "thuanNghich_contains": "Nghịch lý", "gender": "male", "amDuong": "Âm"}'),
('NGHICH_LY_DUONG_NU', '{"type": "OVERVIEW", "thuanNghich_contains": "Nghịch lý", "gender": "female", "amDuong": "Dương"}'),
('THUAN_LY', '{"type": "OVERVIEW", "thuanNghich_contains": "Thuận lý"}'),
('NGHICH_LY', '{"type": "OVERVIEW", "thuanNghich_contains": "Nghịch lý"}');

-- ============================
-- 4. RULES CHO THÂN CƯ (Lai nhân)
-- ============================

-- Note: Thân cư được xác định từ thanCu field trong CenterInfo
-- Format có thể là "Mệnh", "Quan Lộc", "Tài Bạch", etc.
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THAN_CU_MENH', '{"type": "OVERVIEW", "thanCu": "Mệnh"}'),
('THAN_CU_QUAN_LOC', '{"type": "OVERVIEW", "thanCu": "Quan Lộc"}'),
('THAN_CU_TAI_BACH', '{"type": "OVERVIEW", "thanCu": "Tài Bạch"}'),
('THAN_CU_PHU_THE', '{"type": "OVERVIEW", "thanCu": "Phu Thê"}'),
('THAN_CU_TAT_ACH', '{"type": "OVERVIEW", "thanCu": "Tật Ách"}'),
('THAN_CU_TU_TUC', '{"type": "OVERVIEW", "thanCu": "Tử Tức"}'),
('THAN_CU_DIEN_TRACH', '{"type": "OVERVIEW", "thanCu": "Điền Trạch"}'),
('THAN_CU_PHU_MAU', '{"type": "OVERVIEW", "thanCu": "Phụ Mẫu"}'),
('THAN_CU_HUYNH_DE', '{"type": "OVERVIEW", "thanCu": "Huynh Đệ"}'),
('THAN_CU_PHUC_DUC', '{"type": "OVERVIEW", "thanCu": "Phúc Đức"}'),
('THAN_CU_NO_BOC', '{"type": "OVERVIEW", "thanCu": "Nô Bộc"}'),
('THAN_CU_THIEN_DI', '{"type": "OVERVIEW", "thanCu": "Thiên Di"}');

-- Thân Mệnh đồng cung (đặc biệt - cần check thanMenhDongCung = true)
-- Note: thanMenhDongCung là boolean, được tính từ logic (Thân cư palace = Mệnh palace)
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('THAN_MENH_DONG_CUNG', '{"type": "OVERVIEW", "thanMenhDongCung": true}');

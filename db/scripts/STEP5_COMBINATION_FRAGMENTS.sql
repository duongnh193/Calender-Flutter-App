-- ============================================================
-- STEP 5: COMBINATION FRAGMENTS - CUNG MỆNH
-- Tổ hợp sao quan trọng (priority cao hơn fragment đơn)
-- ============================================================

-- ============================
-- 1. TỬ PHỦ (Tử Vi + Thiên Phủ)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('TU_PHU_MENH_MIEU', 'Tử Phủ đồng cung miếu địa tại Mệnh chủ quyền quý, phú lưỡng toàn, có địa vị và tài sản.', 'positive', 1),
('TU_PHU_MENH_VUONG', 'Tử Phủ đồng cung vượng địa tại Mệnh chủ phú quý song toàn, uy quyền và giàu có.', 'positive', 1),
('TU_PHU_MENH_DAC', 'Tử Phủ đồng cung đắc địa tại Mệnh có quyền lực và tài sản ổn định.', 'positive', 1),
('TU_PHU_MENH_BINH', 'Tử Phủ đồng cung bình địa tại Mệnh có địa vị và tài sản trung bình.', 'neutral', 2),
('TU_PHU_MENH_HAM', 'Tử Phủ đồng cung hãm địa tại Mệnh thiếu quyền uy và tài sản, khó phát huy cả hai.', 'negative', 2);

-- Rules cho Tử Phủ
-- Note: Combination rules require BOTH stars present in the same palace
-- Brightness check: at least one star has specified brightness or higher
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TU_PHU_MENH_MIEU', '{"palace": "MENH", "required_stars": ["TU_VI", "THIEN_PHU"], "combination_type": "TU_PHU", "min_brightness": "MIEU"}'),
('TU_PHU_MENH_VUONG', '{"palace": "MENH", "required_stars": ["TU_VI", "THIEN_PHU"], "combination_type": "TU_PHU", "min_brightness": "VUONG"}'),
('TU_PHU_MENH_DAC', '{"palace": "MENH", "required_stars": ["TU_VI", "THIEN_PHU"], "combination_type": "TU_PHU", "min_brightness": "DAC"}'),
('TU_PHU_MENH_BINH', '{"palace": "MENH", "required_stars": ["TU_VI", "THIEN_PHU"], "combination_type": "TU_PHU", "min_brightness": "BINH"}'),
('TU_PHU_MENH_HAM', '{"palace": "MENH", "required_stars": ["TU_VI", "THIEN_PHU"], "combination_type": "TU_PHU", "min_brightness": "HAM"}');

-- ============================
-- 2. SÁT PHÁ THAM (Thất Sát + Phá Quân + Tham Lang)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('SAT_PHA_THAM_MENH_MIEU', 'Sát Phá Tham tại Mệnh miếu địa chủ biến động mạnh mẽ, quyết đoán trong kinh doanh và sự nghiệp.', 'positive', 1),
('SAT_PHA_THAM_MENH_VUONG', 'Sát Phá Tham tại Mệnh vượng địa tính cách quyết liệt, có khả năng đổi mới và thành công qua thay đổi.', 'positive', 1),
('SAT_PHA_THAM_MENH_DAC', 'Sát Phá Tham tại Mệnh đắc địa có tính cách năng động, thích hợp nghề nghiệp cần dũng khí và sáng tạo.', 'positive', 1),
('SAT_PHA_THAM_MENH_BINH', 'Sát Phá Tham tại Mệnh bình địa tính cách có phần biến động, không ổn định hoàn toàn.', 'neutral', 2),
('SAT_PHA_THAM_MENH_HAM', 'Sát Phá Tham tại Mệnh hãm địa tính cách phá hoại, dễ gây rối, khó giữ ổn định trong cuộc sống.', 'negative', 2);

-- Rules cho Sát Phá Tham
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('SAT_PHA_THAM_MENH_MIEU', '{"palace": "MENH", "required_stars": ["THAT_SAT", "PHA_QUAN", "THAM_LANG"], "combination_type": "SAT_PHA_THAM", "min_brightness": "MIEU"}'),
('SAT_PHA_THAM_MENH_VUONG', '{"palace": "MENH", "required_stars": ["THAT_SAT", "PHA_QUAN", "THAM_LANG"], "combination_type": "SAT_PHA_THAM", "min_brightness": "VUONG"}'),
('SAT_PHA_THAM_MENH_DAC', '{"palace": "MENH", "required_stars": ["THAT_SAT", "PHA_QUAN", "THAM_LANG"], "combination_type": "SAT_PHA_THAM", "min_brightness": "DAC"}'),
('SAT_PHA_THAM_MENH_BINH', '{"palace": "MENH", "required_stars": ["THAT_SAT", "PHA_QUAN", "THAM_LANG"], "combination_type": "SAT_PHA_THAM", "min_brightness": "BINH"}'),
('SAT_PHA_THAM_MENH_HAM', '{"palace": "MENH", "required_stars": ["THAT_SAT", "PHA_QUAN", "THAM_LANG"], "combination_type": "SAT_PHA_THAM", "min_brightness": "HAM"}');

-- ============================
-- 3. KHÔNG KIẾP (Thiên Không + Địa Kiếp)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('KHONG_KIEP_MENH_MIEU', 'Không Kiếp tại Mệnh miếu địa tính cách phóng khoáng, không bị ràng buộc, thích tự do.', 'neutral', 2),
('KHONG_KIEP_MENH_VUONG', 'Không Kiếp tại Mệnh vượng địa tính cách phóng túng, khó tích tài, dễ hao tán.', 'negative', 2),
('KHONG_KIEP_MENH_DAC', 'Không Kiếp tại Mệnh đắc địa tính cách không chấp trước, ít lo lắng về vật chất.', 'neutral', 2),
('KHONG_KIEP_MENH_BINH', 'Không Kiếp tại Mệnh bình địa tính cách có phần phóng túng, tài sản khó tích.', 'negative', 3),
('KHONG_KIEP_MENH_HAM', 'Không Kiếp tại Mệnh hãm địa tính cách vô định, tài sản hao tán nhanh, khó giữ của.', 'negative', 2);

-- Rules cho Không Kiếp
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('KHONG_KIEP_MENH_MIEU', '{"palace": "MENH", "required_stars": ["THIEN_KHONG", "DIA_KIEP"], "combination_type": "KHONG_KIEP", "min_brightness": "MIEU"}'),
('KHONG_KIEP_MENH_VUONG', '{"palace": "MENH", "required_stars": ["THIEN_KHONG", "DIA_KIEP"], "combination_type": "KHONG_KIEP", "min_brightness": "VUONG"}'),
('KHONG_KIEP_MENH_DAC', '{"palace": "MENH", "required_stars": ["THIEN_KHONG", "DIA_KIEP"], "combination_type": "KHONG_KIEP", "min_brightness": "DAC"}'),
('KHONG_KIEP_MENH_BINH', '{"palace": "MENH", "required_stars": ["THIEN_KHONG", "DIA_KIEP"], "combination_type": "KHONG_KIEP", "min_brightness": "BINH"}'),
('KHONG_KIEP_MENH_HAM', '{"palace": "MENH", "required_stars": ["THIEN_KHONG", "DIA_KIEP"], "combination_type": "KHONG_KIEP", "min_brightness": "HAM"}');

-- ============================
-- 4. KÌNH ĐÀ (Kình Dương + Đà La)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('KINH_DA_MENH_MIEU', 'Kình Đà tại Mệnh miếu địa tính cách cương quyết, có khả năng đột phá nhưng dễ gây tranh cãi.', 'neutral', 2),
('KINH_DA_MENH_VUONG', 'Kình Đà tại Mệnh vượng địa tính cách cứng rắn, quyết liệt trong hành động, dễ xung đột.', 'negative', 2),
('KINH_DA_MENH_DAC', 'Kình Đà tại Mệnh đắc địa tính cách quyết đoán, có khả năng vượt khó.', 'neutral', 2),
('KINH_DA_MENH_BINH', 'Kình Đà tại Mệnh bình địa tính cách có phần cứng rắn, dễ gây mâu thuẫn.', 'negative', 3),
('KINH_DA_MENH_HAM', 'Kình Đà tại Mệnh hãm địa tính cách hung hãn, dễ gây thù oán, khó hòa hợp.', 'negative', 2);

-- Rules cho Kình Đà
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('KINH_DA_MENH_MIEU', '{"palace": "MENH", "required_stars": ["KINH_DUONG", "DA_LA"], "combination_type": "KINH_DA", "min_brightness": "MIEU"}'),
('KINH_DA_MENH_VUONG', '{"palace": "MENH", "required_stars": ["KINH_DUONG", "DA_LA"], "combination_type": "KINH_DA", "min_brightness": "VUONG"}'),
('KINH_DA_MENH_DAC', '{"palace": "MENH", "required_stars": ["KINH_DUONG", "DA_LA"], "combination_type": "KINH_DA", "min_brightness": "DAC"}'),
('KINH_DA_MENH_BINH', '{"palace": "MENH", "required_stars": ["KINH_DUONG", "DA_LA"], "combination_type": "KINH_DA", "min_brightness": "BINH"}'),
('KINH_DA_MENH_HAM', '{"palace": "MENH", "required_stars": ["KINH_DUONG", "DA_LA"], "combination_type": "KINH_DA", "min_brightness": "HAM"}');

-- ============================
-- 5. XƯƠNG KHÚC (Văn Xương + Văn Khúc)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('XUONG_KHUC_MENH_MIEU', 'Xương Khúc tại Mệnh miếu địa chủ văn tài, học thức, có khả năng văn chương và nghệ thuật.', 'positive', 1),
('XUONG_KHUC_MENH_VUONG', 'Xương Khúc tại Mệnh vượng địa văn tài xuất chúng, có danh tiếng trong học thuật hoặc nghệ thuật.', 'positive', 1),
('XUONG_KHUC_MENH_DAC', 'Xương Khúc tại Mệnh đắc địa có tài văn chương, học thức tốt.', 'positive', 2),
('XUONG_KHUC_MENH_BINH', 'Xương Khúc tại Mệnh bình địa có học thức trung bình, văn tài không nổi bật.', 'neutral', 3),
('XUONG_KHUC_MENH_HAM', 'Xương Khúc tại Mệnh hãm địa học thức khó phát triển, văn tài kém.', 'negative', 2);

-- Rules cho Xương Khúc
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('XUONG_KHUC_MENH_MIEU', '{"palace": "MENH", "required_stars": ["VAN_XUONG", "VAN_KHUC"], "combination_type": "XUONG_KHUC", "min_brightness": "MIEU"}'),
('XUONG_KHUC_MENH_VUONG', '{"palace": "MENH", "required_stars": ["VAN_XUONG", "VAN_KHUC"], "combination_type": "XUONG_KHUC", "min_brightness": "VUONG"}'),
('XUONG_KHUC_MENH_DAC', '{"palace": "MENH", "required_stars": ["VAN_XUONG", "VAN_KHUC"], "combination_type": "XUONG_KHUC", "min_brightness": "DAC"}'),
('XUONG_KHUC_MENH_BINH', '{"palace": "MENH", "required_stars": ["VAN_XUONG", "VAN_KHUC"], "combination_type": "XUONG_KHUC", "min_brightness": "BINH"}'),
('XUONG_KHUC_MENH_HAM', '{"palace": "MENH", "required_stars": ["VAN_XUONG", "VAN_KHUC"], "combination_type": "XUONG_KHUC", "min_brightness": "HAM"}');

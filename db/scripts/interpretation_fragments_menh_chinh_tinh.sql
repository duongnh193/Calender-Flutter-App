-- ============================================================
-- INTERPRETATION FRAGMENTS: CUNG MỆNH - CHÍNH TINH
-- Dựa trên Tử Vi Đẩu Số Bắc phái truyền thống
-- ============================================================

-- TỬ VI tại cung Mệnh

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('TU_VI_MENH_MIEU', 'Tử Vi miếu địa tại cung Mệnh chủ quyền uy, khả năng lãnh đạo và tự chủ cao.', 'positive', 1),
('TU_VI_MENH_VUONG', 'Tử Vi vượng địa tại cung Mệnh chủ phú quý, uy quyền, có khả năng chỉ huy.', 'positive', 1),
('TU_VI_MENH_DAC', 'Tử Vi đắc địa tại cung Mệnh chủ thanh cao, có địa vị và danh vọng.', 'positive', 2),
('TU_VI_MENH_BINH', 'Tử Vi bình địa tại cung Mệnh tính cách trung hòa, ít thể hiện quyền uy rõ rệt.', 'neutral', 3),
('TU_VI_MENH_HAM', 'Tử Vi hãm địa tại cung Mệnh thiếu quyền uy, dễ bị chi phối, khó phát huy lãnh đạo.', 'negative', 2);

-- THIÊN PHỦ tại cung Mệnh

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THIEN_PHU_MENH_MIEU', 'Thiên Phủ miếu địa tại cung Mệnh chủ phú có, tài sản tích lũy, tính cách khoan hòa.', 'positive', 1),
('THIEN_PHU_MENH_VUONG', 'Thiên Phủ vượng địa tại cung Mệnh chủ giàu có, có khả năng tích tài, nhân từ.', 'positive', 1),
('THIEN_PHU_MENH_DAC', 'Thiên Phủ đắc địa tại cung Mệnh có tài sản ổn định, tính cách nhân hậu.', 'positive', 2),
('THIEN_PHU_MENH_BINH', 'Thiên Phủ bình địa tại cung Mệnh tài sản trung bình, tính cách hòa nhã.', 'neutral', 3),
('THIEN_PHU_MENH_HAM', 'Thiên Phủ hãm địa tại cung Mệnh tài sản khó tích, dễ hao tán, tính cách kém khoan hòa.', 'negative', 2);

-- THAM LANG tại cung Mệnh

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THAM_LANG_MENH_MIEU', 'Tham Lang miếu địa tại cung Mệnh chủ thông minh, tài trí, có khả năng kinh doanh.', 'positive', 1),
('THAM_LANG_MENH_VUONG', 'Tham Lang vượng địa tại cung Mệnh thông minh lanh lợi, giỏi buôn bán, nhiều cơ hội.', 'positive', 1),
('THAM_LANG_MENH_DAC', 'Tham Lang đắc địa tại cung Mệnh có tài trí, khéo léo trong giao tiếp và làm ăn.', 'positive', 2),
('THAM_LANG_MENH_BINH', 'Tham Lang bình địa tại cung Mệnh tính cách linh hoạt, có khả năng thích ứng.', 'neutral', 3),
('THAM_LANG_MENH_HAM', 'Tham Lang hãm địa tại cung Mệnh thiếu thông minh thực tế, dễ tham lam vô độ, tài sản hao tán.', 'negative', 2);

-- THẤT SÁT tại cung Mệnh

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THAT_SAT_MENH_MIEU', 'Thất Sát miếu địa tại cung Mệnh chủ quyền uy, dũng cảm, có khả năng chỉ huy quân đội hoặc lĩnh vực cần quyết đoán.', 'positive', 1),
('THAT_SAT_MENH_VUONG', 'Thất Sát vượng địa tại cung Mệnh tính cách quyết đoán mạnh mẽ, có khả năng lãnh đạo trong nghề nghiệp cần sức mạnh.', 'positive', 1),
('THAT_SAT_MENH_DAC', 'Thất Sát đắc địa tại cung Mệnh có tính quyết đoán, can đảm, phù hợp nghề nghiệp cần dũng khí.', 'positive', 2),
('THAT_SAT_MENH_BINH', 'Thất Sát bình địa tại cung Mệnh tính cách cương quyết trung bình, có lúc quyết đoán có lúc do dự.', 'neutral', 3),
('THAT_SAT_MENH_HAM', 'Thất Sát hãm địa tại cung Mệnh thiếu quyết đoán, dễ dao động, khó thành công trong lĩnh vực cần dũng khí.', 'negative', 2);

-- PHÁ QUÂN tại cung Mệnh

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('PHA_QUAN_MENH_MIEU', 'Phá Quân miếu địa tại cung Mệnh chủ biến động tích cực, có khả năng phá cực tạo mới, dám thay đổi.', 'positive', 1),
('PHA_QUAN_MENH_VUONG', 'Phá Quân vượng địa tại cung Mệnh tính cách mạnh mẽ trong thay đổi, có khả năng đổi mới và cách tân.', 'positive', 1),
('PHA_QUAN_MENH_DAC', 'Phá Quân đắc địa tại cung Mệnh có tính cách năng động, không ngại thay đổi, thích hợp nghề nghiệp cần sáng tạo.', 'positive', 2),
('PHA_QUAN_MENH_BINH', 'Phá Quân bình địa tại cung Mệnh tính cách có phần biến động, không ổn định hoàn toàn.', 'neutral', 3),
('PHA_QUAN_MENH_HAM', 'Phá Quân hãm địa tại cung Mệnh tính cách phá hoại, dễ gây rối, khó giữ ổn định trong cuộc sống.', 'negative', 2);

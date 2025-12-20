-- ============================================================
-- STEP 8: OVERVIEW SECTION FRAGMENTS - CENTER LEVEL
-- Fragments cho các phần trong OverviewSection
-- Dựa trên Tử Vi Đẩu Số Bắc phái truyền thống
-- ============================================================

-- ============================
-- 1. BẢN MỆNH (Nạp Âm) - theo Ngũ Hành
-- ============================

-- Bản mệnh KIM
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('BAN_MENH_KIM', 'Bản mệnh thuộc Kim, tính cách cương nghị, quyết đoán, có khả năng tổ chức và quản lý.', 'neutral', 3),
('BAN_MENH_KIM_MENH_KIM', 'Bản mệnh Kim gặp Cục Kim, cùng hành tương hòa, tính cách ổn định, dễ phát triển theo hướng cương nghị.', 'positive', 2),
('BAN_MENH_KIM_MENH_MOC', 'Bản mệnh Kim gặp Cục Mộc, Kim khắc Mộc, có xu hướng mạnh mẽ, quyết đoán nhưng dễ cứng nhắc.', 'neutral', 2),
('BAN_MENH_KIM_MENH_THUY', 'Bản mệnh Kim gặp Cục Thủy, Kim sinh Thủy, có khả năng nuôi dưỡng và phát triển, nhưng dễ hao tán tài lực.', 'neutral', 2),
('BAN_MENH_KIM_MENH_HOA', 'Bản mệnh Kim gặp Cục Hỏa, Hỏa khắc Kim, tính cách dễ bị ức chế, cần kiên nhẫn và linh hoạt.', 'negative', 2),
('BAN_MENH_KIM_MENH_THO', 'Bản mệnh Kim gặp Cục Thổ, Thổ sinh Kim, được hỗ trợ tốt, dễ tích lũy và phát triển ổn định.', 'positive', 2);

-- Bản mệnh MỘC
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('BAN_MENH_MOC', 'Bản mệnh thuộc Mộc, tính cách nhân từ, hòa nhã, có khả năng phát triển và thích ứng.', 'neutral', 3),
('BAN_MENH_MOC_MENH_KIM', 'Bản mệnh Mộc gặp Cục Kim, Kim khắc Mộc, tính cách dễ bị áp chế, cần mềm mỏng và kiên trì.', 'negative', 2),
('BAN_MENH_MOC_MENH_MOC', 'Bản mệnh Mộc gặp Cục Mộc, cùng hành tương hòa, tính cách phát triển mạnh, dễ thích ứng và mở rộng.', 'positive', 2),
('BAN_MENH_MOC_MENH_THUY', 'Bản mệnh Mộc gặp Cục Thủy, Thủy sinh Mộc, được nuôi dưỡng tốt, dễ phát triển và thăng tiến.', 'positive', 2),
('BAN_MENH_MOC_MENH_HOA', 'Bản mệnh Mộc gặp Cục Hỏa, Mộc sinh Hỏa, có khả năng hỗ trợ và phát triển, nhưng dễ hao tổn sức lực.', 'neutral', 2),
('BAN_MENH_MOC_MENH_THO', 'Bản mệnh Mộc gặp Cục Thổ, Mộc khắc Thổ, có xu hướng mạnh mẽ trong hành động, nhưng dễ gây xung đột.', 'neutral', 2);

-- Bản mệnh THỦY
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('BAN_MENH_THUY', 'Bản mệnh thuộc Thủy, tính cách linh hoạt, thông minh, có khả năng thích ứng và biến hóa.', 'neutral', 3),
('BAN_MENH_THUY_MENH_KIM', 'Bản mệnh Thủy gặp Cục Kim, Kim sinh Thủy, được nuôi dưỡng tốt, dễ phát triển và thăng tiến.', 'positive', 2),
('BAN_MENH_THUY_MENH_MOC', 'Bản mệnh Thủy gặp Cục Mộc, Thủy sinh Mộc, có khả năng hỗ trợ và phát triển, nhưng dễ hao tổn tài lực.', 'neutral', 2),
('BAN_MENH_THUY_MENH_THUY', 'Bản mệnh Thủy gặp Cục Thủy, cùng hành tương hòa, tính cách linh hoạt mạnh, dễ biến hóa và thích ứng.', 'positive', 2),
('BAN_MENH_THUY_MENH_HOA', 'Bản mệnh Thủy gặp Cục Hỏa, Thủy khắc Hỏa, có xu hướng kiềm chế và điều hòa, nhưng dễ xung đột.', 'neutral', 2),
('BAN_MENH_THUY_MENH_THO', 'Bản mệnh Thủy gặp Cục Thổ, Thổ khắc Thủy, tính cách dễ bị áp chế, cần kiên nhẫn và linh hoạt.', 'negative', 2);

-- Bản mệnh HỎA
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('BAN_MENH_HOA', 'Bản mệnh thuộc Hỏa, tính cách nhiệt tình, năng động, có khả năng truyền cảm hứng và dẫn dắt.', 'neutral', 3),
('BAN_MENH_HOA_MENH_KIM', 'Bản mệnh Hỏa gặp Cục Kim, Hỏa khắc Kim, có xu hướng mạnh mẽ và quyết đoán, nhưng dễ gây xung đột.', 'neutral', 2),
('BAN_MENH_HOA_MENH_MOC', 'Bản mệnh Hỏa gặp Cục Mộc, Mộc sinh Hỏa, được hỗ trợ tốt, dễ phát triển và thăng tiến mạnh.', 'positive', 2),
('BAN_MENH_HOA_MENH_THUY', 'Bản mệnh Hỏa gặp Cục Thủy, Thủy khắc Hỏa, tính cách dễ bị ức chế, cần kiên nhẫn và linh hoạt.', 'negative', 2),
('BAN_MENH_HOA_MENH_HOA', 'Bản mệnh Hỏa gặp Cục Hỏa, cùng hành tương hòa, tính cách nhiệt tình mạnh, dễ phát triển và thăng tiến.', 'positive', 2),
('BAN_MENH_HOA_MENH_THO', 'Bản mệnh Hỏa gặp Cục Thổ, Hỏa sinh Thổ, có khả năng hỗ trợ và phát triển, nhưng dễ hao tổn sức lực.', 'neutral', 2);

-- Bản mệnh THỔ
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('BAN_MENH_THO', 'Bản mệnh thuộc Thổ, tính cách ổn định, trung hậu, có khả năng tích lũy và giữ gìn.', 'neutral', 3),
('BAN_MENH_THO_MENH_KIM', 'Bản mệnh Thổ gặp Cục Kim, Thổ sinh Kim, có khả năng nuôi dưỡng và phát triển, dễ tích lũy tài sản.', 'positive', 2),
('BAN_MENH_THO_MENH_MOC', 'Bản mệnh Thổ gặp Cục Mộc, Mộc khắc Thổ, tính cách dễ bị áp chế, cần kiên nhẫn và ổn định.', 'negative', 2),
('BAN_MENH_THO_MENH_THUY', 'Bản mệnh Thổ gặp Cục Thủy, Thổ khắc Thủy, có xu hướng kiềm chế và điều hòa, nhưng dễ xung đột.', 'neutral', 2),
('BAN_MENH_THO_MENH_HOA', 'Bản mệnh Thổ gặp Cục Hỏa, Hỏa sinh Thổ, được hỗ trợ tốt, dễ tích lũy và phát triển ổn định.', 'positive', 2),
('BAN_MENH_THO_MENH_THO', 'Bản mệnh Thổ gặp Cục Thổ, cùng hành tương hòa, tính cách ổn định mạnh, dễ tích lũy và phát triển.', 'positive', 2);

-- ============================
-- 2. CỤC MỆNH - theo Cục name/value
-- ============================

-- Cục mệnh - theo Ngũ Hành
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('CUC_KIM_NGU', 'Kim cục thể hiện tính cách cương nghị, quyết đoán, có khả năng tổ chức và quản lý tốt.', 'neutral', 3),
('CUC_MOC_NGU', 'Mộc cục thể hiện tính cách nhân từ, hòa nhã, có khả năng phát triển và mở rộng.', 'neutral', 3),
('CUC_THUY_NHI', 'Thủy cục thể hiện tính cách linh hoạt, thông minh, có khả năng thích ứng và biến hóa.', 'neutral', 3),
('CUC_HOA_LUC', 'Hỏa cục thể hiện tính cách nhiệt tình, năng động, có khả năng truyền cảm hứng.', 'neutral', 3),
('CUC_THO_NGU', 'Thổ cục thể hiện tính cách ổn định, trung hậu, có khả năng tích lũy và giữ gìn.', 'neutral', 3);

-- ============================
-- 3. THUẬN / NGHỊCH
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THUAN_LY', 'Thuận lý thể hiện xu hướng phát triển thuận chiều, dễ thích ứng và hòa hợp với môi trường.', 'positive', 3),
('NGHICH_LY', 'Nghịch lý thể hiện xu hướng phát triển ngược chiều, cần nỗ lực và kiên trì để đạt được mục tiêu.', 'neutral', 3),
('THUAN_LY_DUONG_NAM', 'Dương nam Thuận lý có xu hướng phát triển mạnh mẽ, dễ thành công trong các lĩnh vực cần quyết đoán.', 'positive', 2),
('THUAN_LY_AM_NU', 'Âm nữ Thuận lý có xu hướng phát triển hài hòa, dễ thành công trong các lĩnh vực cần sự mềm mỏng.', 'positive', 2),
('NGHICH_LY_AM_NAM', 'Âm nam Nghịch lý cần nỗ lực nhiều hơn, nhưng có khả năng đạt được thành tựu qua sự kiên trì.', 'neutral', 2),
('NGHICH_LY_DUONG_NU', 'Dương nữ Nghịch lý cần cân bằng giữa tính cương và tính nhu, dễ thành công khi biết điều hòa.', 'neutral', 2);

-- ============================
-- 4. THÂN CƯ (Lai nhân)
-- ============================

-- Thân cư các cung
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THAN_CU_MENH', 'Thân cư Mệnh cho thấy thân thể và tính cách hợp nhất, dễ phát huy năng lực cá nhân.', 'positive', 2),
('THAN_CU_QUAN_LOC', 'Thân cư Quan Lộc cho thấy sự nghiệp và thân thể gắn liền, dễ thành công trong nghề nghiệp.', 'positive', 2),
('THAN_CU_TAI_BACH', 'Thân cư Tài Bạch cho thấy tài sản và thân thể liên quan, dễ tích lũy của cải.', 'positive', 2),
('THAN_CU_PHU_THE', 'Thân cư Phu Thê cho thấy hôn nhân ảnh hưởng đến thân thể, cần chú ý đến sức khỏe trong quan hệ.', 'neutral', 2),
('THAN_CU_TAT_ACH', 'Thân cư Tật Ách cho thấy cần chú ý đến sức khỏe, dễ gặp vấn đề về thể chất.', 'negative', 2),
('THAN_CU_TU_TUC', 'Thân cư Tử Tức cho thấy con cái ảnh hưởng đến thân thể, dễ có nhiều lo lắng về sức khỏe.', 'neutral', 2),
('THAN_CU_DIEN_TRACH', 'Thân cư Điền Trạch cho thấy nhà cửa và thân thể liên quan, dễ có sự ổn định về nơi ở.', 'positive', 2),
('THAN_CU_PHU_MAU', 'Thân cư Phụ Mẫu cho thấy cha mẹ ảnh hưởng đến thân thể, dễ thừa hưởng sức khỏe từ gia đình.', 'neutral', 2),
('THAN_CU_HUYNH_DE', 'Thân cư Huynh Đệ cho thấy anh em ảnh hưởng đến thân thể, dễ có sự hỗ trợ từ người thân.', 'neutral', 2),
('THAN_CU_PHUC_DUC', 'Thân cư Phúc Đức cho thấy phúc đức ảnh hưởng đến thân thể, dễ có sức khỏe tốt nhờ tích đức.', 'positive', 2),
('THAN_CU_NO_BOC', 'Thân cư Nô Bộc cho thấy người dưới ảnh hưởng đến thân thể, dễ có sự hỗ trợ từ cấp dưới.', 'neutral', 2),
('THAN_CU_THIEN_DI', 'Thân cư Thiên Di cho thấy di chuyển ảnh hưởng đến thân thể, dễ có sự thay đổi về nơi ở.', 'neutral', 2);

-- Thân Mệnh đồng cung (đặc biệt)
INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('THAN_MENH_DONG_CUNG', 'Thân Mệnh đồng cung cho thấy tính cách và thân thể hòa hợp, dễ phát huy toàn diện năng lực cá nhân.', 'positive', 1);

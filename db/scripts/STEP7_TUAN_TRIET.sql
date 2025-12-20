-- ============================================================
-- STEP 7: TUẦN / TRIỆT OVERRIDE - CUNG MỆNH
-- Tác động của Tuần và Triệt tại cung Mệnh
-- ============================================================

-- ============================
-- TUẦN tại cung Mệnh
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('TUAN_MENH_GENERAL', 'Tuần tại cung Mệnh làm giảm hiệu lực các sao, khó phát huy đầy đủ tính chất tốt.', 'negative', 2),
('TUAN_MENH_STRONG', 'Tuần tại cung Mệnh làm các sao miếu vượng khó phát huy, hiệu lực giảm đáng kể.', 'negative', 1),
('TUAN_MENH_WEAK', 'Tuần tại cung Mệnh làm các sao hãm càng yếu hơn, khó cải thiện.', 'negative', 2);

-- Rules cho Tuần
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TUAN_MENH_GENERAL', '{"palace": "MENH", "has_tuan": true}'),
('TUAN_MENH_STRONG', '{"palace": "MENH", "has_tuan": true, "stars_brightness": ["MIEU", "VUONG"]}'),
('TUAN_MENH_WEAK', '{"palace": "MENH", "has_tuan": true, "stars_brightness": ["HAM"]}');

-- ============================
-- TRIỆT tại cung Mệnh
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('TRIET_MENH_GENERAL', 'Triệt tại cung Mệnh cản trở hiệu lực các sao, làm chậm phát triển.', 'negative', 2),
('TRIET_MENH_STRONG', 'Triệt tại cung Mệnh cản trở sao miếu vượng, hiệu lực bị giảm nhiều.', 'negative', 1),
('TRIET_MENH_WEAK', 'Triệt tại cung Mệnh làm sao hãm càng khó phát huy, cản trở cải thiện.', 'negative', 2);

-- Rules cho Triệt
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TRIET_MENH_GENERAL', '{"palace": "MENH", "has_triet": true}'),
('TRIET_MENH_STRONG', '{"palace": "MENH", "has_triet": true, "stars_brightness": ["MIEU", "VUONG"]}'),
('TRIET_MENH_WEAK', '{"palace": "MENH", "has_triet": true, "stars_brightness": ["HAM"]}');

-- ============================
-- TUẦN + TRIỆT cùng lúc (hiếm nhưng có thể)
-- ============================

INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES
('TUAN_TRIET_MENH', 'Tuần Triệt cùng tại cung Mệnh cản trở mạnh mẽ, các sao khó phát huy hiệu lực.', 'negative', 1);

-- Rules cho Tuần + Triệt
INSERT INTO interpretation_rules (fragment_code, conditions) VALUES
('TUAN_TRIET_MENH', '{"palace": "MENH", "has_tuan": true, "has_triet": true}');

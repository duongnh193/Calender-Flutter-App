/* ============================================================
   SEED DATA FOR LICH_VAN_NIEN BACKEND
   - zodiac
   - zodiac_hour
   - golden_hour_pattern
   - horoscope_yearly (template)
   ============================================================ */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1) ZODIAC (12 con giáp)
-- ============================================

TRUNCATE TABLE zodiac;

INSERT INTO zodiac (code, name_vi, order_no) VALUES
                                                 ('ti',   'Tý',   1),
                                                 ('suu',  'Sửu',  2),
                                                 ('dan',  'Dần',  3),
                                                 ('mao',  'Mão',  4),
                                                 ('thin', 'Thìn', 5),
                                                 ('ty',   'Tỵ',   6),
                                                 ('ngo',  'Ngọ',  7),
                                                 ('mui',  'Mùi',  8),
                                                 ('than', 'Thân', 9),
                                                 ('dau',  'Dậu',  10),
                                                 ('tuat', 'Tuất', 11),
                                                 ('hoi',  'Hợi',  12);

-- ============================================
-- 2) ZODIAC_HOUR (dùng code ASCII làm key)
-- ============================================

TRUNCATE TABLE zodiac_hour;

INSERT INTO zodiac_hour (branch_code, start_hour, end_hour) VALUES
                                                                ('ti',   23, 1),
                                                                ('suu',   1, 3),
                                                                ('dan',   3, 5),
                                                                ('mao',   5, 7),
                                                                ('thin',  7, 9),
                                                                ('ty',    9, 11),
                                                                ('ngo',  11, 13),
                                                                ('mui',  13, 15),
                                                                ('than', 15, 17),
                                                                ('dau',  17, 19),
                                                                ('tuat', 19, 21),
                                                                ('hoi',  21, 23);

-- ============================================
-- 3) GOLDEN_HOUR_PATTERN
--    Dùng code ASCII thay vì chữ Việt làm key
-- ============================================

TRUNCATE TABLE golden_hour_pattern;

/*
  Mapping tham khảo:
  - day_branch_code = code con giáp của NGÀY (ti, suu, dan,...)
  - good_branch_codes = CSV code con giáp GIỜ hoàng đạo
  (ở đây tao giữ pattern tương tự trước, chỉ chuyển sang code)
*/

INSERT INTO golden_hour_pattern (day_branch_code, good_branch_codes) VALUES
                                                                         ('ti',   'ti,suu,thin,ty,than,dau'),
                                                                         ('suu',  'ti,dan,mao,ngo,than,dau'),
                                                                         ('dan',  'suu,thin,ty,than,tuat,hoi'),
                                                                         ('mao',  'ti,mao,ngo,mui,dau,hoi'),
                                                                         ('thin', 'ti,suu,thin,than,dau,hoi'),
                                                                         ('ty',   'dan,mao,ty,ngo,mui,tuat'),
                                                                         ('ngo',  'ti,suu,mao,ngo,than,dau'),
                                                                         ('mui',  'dan,thin,ty,mui,tuat,hoi'),
                                                                         ('than', 'ti,thin,ty,than,dau,hoi'),
                                                                         ('dau',  'suu,mao,ngo,mui,tuat,hoi'),
                                                                         ('tuat', 'dan,thin,ngo,than,tuat,hoi'),
                                                                         ('hoi',  'ti,suu,thin,mui,than,dau');

-- ============================================
-- 4) TEMPLATE TỬ VI NĂM
-- ============================================

TRUNCATE TABLE horoscope_yearly;

/* MySQL dùng CONCAT, không phải || */
INSERT INTO horoscope_yearly
(zodiac_id, year, summary, love, career, finance, health)
SELECT
    id,
    2025,
    CONCAT('Khái quát tử vi năm 2025 của tuổi ', name_vi),
    'Tình duyên năm 2025...',
    'Công danh sự nghiệp 2025...',
    'Tài chính năm 2025...',
    'Sức khỏe năm 2025...'
FROM zodiac;

-- ============================================
-- 5) TEMPLATE TỬ VI NGÀY (OPTIONAL)
-- ============================================

-- Ví dụ 1 bản ghi mẫu cho tuổi Tý ngày 2025-01-01:

INSERT INTO horoscope_daily
(zodiac_id, solar_date, general, love, career, finance, health, lucky_color, lucky_number)
VALUES
(
  (SELECT id FROM zodiac WHERE code = 'ti'),
  '2025-01-01',
  'Hôm nay bạn sẽ gặp nhiều vận may...',
  'Tình duyên hài hòa...',
  'Công việc thuận lợi...',
  'Tài chính ổn định...',
  'Sức khỏe tốt...',
  'Xanh dương',
  '6, 18'
);

SET FOREIGN_KEY_CHECKS = 1;

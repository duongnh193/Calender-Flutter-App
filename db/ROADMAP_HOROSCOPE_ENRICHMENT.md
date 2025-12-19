# Roadmap: Làm phong phú dữ liệu tử vi (80% so với mẫu)

## 📊 Phân tích chênh lệch

### Hiện trạng
- **Script generate**: Text đơn giản, lặp lại, chỉ có 1 template
- **Daily horoscope**: Giống nhau cho mọi ngày
- **Schema**: Chỉ có các field cơ bản (overview, career, love, health, fortune)
- **Metadata**: Rất ít, chỉ có generated_by, lucky_color, lucky_number

### Mẫu yêu cầu (từ screenshots)

#### Lifetime Horoscope cần có:
1. **Tổng quát** - Đoạn văn dài, có thơ ca
2. **Tình duyên** - Phân nhóm theo tháng sinh (3 nhóm khác nhau)
3. **Gia đình sự nghiệp** - Nhiều đoạn văn, có dự đoán theo độ tuổi (30, 36, 37+)
4. **Tài vận** - Chi tiết về tài chính
5. **Tuổi hợp làm ăn** - Danh sách cụ thể các tuổi hợp
6. **Năm khó khăn nhất** - Liệt kê các năm cụ thể (26, 29, 33, 40)
7. **Ngày giờ xuất hành hợp nhất** - Hướng, giờ, ngày cụ thể
8. **Diễn biến từng năm** - Chi tiết từng độ tuổi (26-29, 30-35, 36-40, 41-45, 51-60)
9. **Tuổi đại kỵ** - Danh sách các tuổi xung khắc
10. **Nghi lễ cúng sao** - Hướng dẫn chi tiết về nghi lễ, màu sắc, giờ
11. **Metadata**: Cung, Xương, Tướng tinh, Mạng, Con nhà, Độ mạng

#### Yearly Horoscope cần có:
1. **Cung Mệnh** - Danh sách sao, giải thích chi tiết
2. **Cung Xung Chiếu** - Sao và ảnh hưởng
3. **Cung Tam Hợp** - Sao và lời khuyên
4. **Cung Nhị Hợp** - Sao và cảnh báo
5. **Vận hạn** - Hạn năm, hạn tuổi, cách hóa giải
6. **Tứ trụ** - Tổng quan, công việc, tài chính, sức khỏe
7. **Luận giải theo phương diện** - Công danh, Tài chính, Tình duyên, Sức khỏe, Ngoại giao
8. **Dự đoán theo tháng** - Chi tiết từng tháng (1-12)
9. **Phong thủy may mắn** - Người xông nhà, ngày xuất hành, ngày khai trương
10. **Q&A format** - Sinh con, làm ăn với tuổi nào, xây nhà, mua xe
11. **Lời kết** - Tổng hợp nhiều khía cạnh
12. **Metadata**: Sao hạn, màu sắc hợp/kỵ, ngũ hành năm

#### Monthly Horoscope cần có:
- Chi tiết từng tháng với nhiều khía cạnh: tài chính, công việc, gia đình, sức khỏe
- Mỗi tháng có variation khác nhau (không lặp lại)

#### Daily Horoscope cần có:
- Variation theo ngày (không giống nhau)
- Chi tiết hơn về career, love, health, fortune

## 🎯 Mục tiêu: Đạt 80% so với mẫu

### Phương án triển khai

#### Giai đoạn 1: Mở rộng Schema và Metadata (Tuần 1-2)
- Sử dụng `metadata` JSON để lưu structured content
- Tạo migration để thêm các field mới nếu cần
- Cập nhật script generate để populate metadata đầy đủ

#### Giai đoạn 2: Cải tiến Script Generate (Tuần 3-4)
- Tạo 30+ template đa dạng cho mỗi loại content
- Variation dựa trên zodiac, can-chi, ngày tháng
- Seed-based generation để đảm bảo consistency nhưng vẫn đa dạng

#### Giai đoạn 3: Tích hợp AI (Optional, Tuần 5-6)
- Sử dụng GPT-4/Claude để generate nội dung tự nhiên
- Batch generation để tối ưu chi phí
- Cache kết quả

## 📁 Cấu trúc file sẽ tạo

```
db/
├── ROADMAP_HOROSCOPE_ENRICHMENT.md (file này)
├── migrations/
│   └── V4__extend_horoscope_metadata.sql (mở rộng schema nếu cần)
├── generate_horoscopes_improved.py (script cải tiến với nhiều template)
├── generate_horoscopes_ai.py (script tích hợp AI)
└── templates/
    ├── lifetime_templates.py (30+ template cho lifetime)
    ├── yearly_templates.py (30+ template cho yearly)
    ├── monthly_templates.py (30+ template cho monthly)
    └── daily_templates.py (30+ template cho daily)
```

## 🔧 Chi tiết triển khai

### 1. Metadata Structure

Metadata sẽ được lưu dưới dạng JSON với cấu trúc:

```json
{
  "generated_by": "gen_v2_improved",
  "sections": {
    "tong_quat": "...",
    "tinh_duyen": {
      "group1": "...",
      "group2": "...",
      "group3": "..."
    },
    "gia_dinh_su_nghiep": "...",
    "tai_van": "...",
    "tuoi_hop_lam_an": ["Canh Thìn", "Bính Tuất", ...],
    "nam_kho_khan": [26, 29, 33, 40],
    "dien_bien_tung_nam": {
      "26-29": "...",
      "30-35": "...",
      ...
    },
    "tuoi_dai_ky": ["Quý Mùi", "Giáp Thân", ...],
    "nghi_le": {
      "ngay": 25,
      "gio": "19h-21h",
      "huong": "Đông",
      "mau_sac": "xanh",
      "so_den": 20
    }
  },
  "astrology": {
    "cung": "CÀN",
    "xuong": "CON RỒNG",
    "tuong_tinh": "CON LẠC ĐÀ",
    "mang": "BẠCH LẠP KIM",
    "con_nha": "BẠCH ĐẾ",
    "do_mang": "Ông Quan Đế độ mạng"
  },
  "yearly_sections": {
    "cung_menh": {
      "stars": ["Đào Hoa", "Đại Hao", ...],
      "interpretation": "..."
    },
    "cung_xung_chieu": {...},
    "cung_tam_hop": {...},
    "van_han": {
      "han_nam": "...",
      "han_tuoi": "...",
      "hoa_giai": "..."
    },
    "tu_tru": {
      "tong_quan": "...",
      "cong_viec": "...",
      "tai_chinh": "...",
      "suc_khoe": "..."
    },
    "phong_thuy": {
      "nguoi_xong_nha": "1994",
      "ngay_xuat_hanh": "mùng 9 âm lịch",
      "huong": "Tây, Tây Nam",
      "ngay_khai_truong": "..."
    },
    "qa": [
      {"question": "...", "answer": "..."},
      ...
    ]
  },
  "monthly_details": {
    "1": {"tai_chinh": "...", "cong_viec": "...", "gia_dinh": "...", "suc_khoe": "..."},
    "2": {...},
    ...
  }
}
```

### 2. Template System

Mỗi loại horoscope sẽ có 30+ template khác nhau, được chọn dựa trên:
- Zodiac code (hash-based selection)
- Can-Chi combination
- Year (cho yearly/monthly/daily)
- Day of year (cho daily)

### 3. AI Integration

Script AI sẽ:
- Sử dụng OpenAI GPT-4 hoặc Anthropic Claude
- Generate content theo batch để tối ưu chi phí
- Cache kết quả vào database
- Fallback về template-based nếu AI fail

## 📈 Kết quả mong đợi

Sau khi triển khai:
- **Lifetime**: 10+ sections với nội dung đa dạng
- **Yearly**: 8+ sections với chi tiết đầy đủ
- **Monthly**: Variation theo tháng, không lặp lại
- **Daily**: Variation theo ngày, không giống nhau
- **Metadata**: Đầy đủ thông tin astrological

## ⚠️ Lưu ý

1. **Chi phí AI**: Nếu dùng AI, cần tính toán chi phí (khoảng $0.01-0.05 per horoscope)
2. **Thời gian generate**: Với AI, thời gian sẽ lâu hơn (cần rate limiting)
3. **Quality control**: Cần review một số sample để đảm bảo chất lượng
4. **Backward compatibility**: Đảm bảo frontend vẫn hoạt động với data cũ

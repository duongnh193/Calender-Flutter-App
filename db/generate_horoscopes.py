#!/usr/bin/env python3
"""
generate_horoscopes_full.py

Generates SQL INSERT files for:
 - insert_horoscope_lifetime.sql  (60 can-chi * 2 genders = 120 records)
 - insert_horoscope_yearly.sql    (12 zodiac * years)
 - insert_horoscope_monthly.sql   (12 zodiac * years * 12 months)
 - insert_horoscope_daily.sql     (12 zodiac * years * days)

Usage examples:
  python3 generate_horoscopes_full.py --out-dir ./generated_sql
  python3 generate_horoscopes_full.py --out-dir ./generated_sql --years 2024 2025 2026
  python3 generate_horoscopes_full.py --out-dir ./generated_sql --sample-only
"""

import argparse
import json
import random
from pathlib import Path
from datetime import date, timedelta

# Zodiac order assumed to map to zodiac.id = 1..12 in your DB
ZODIACS = [
    ("ti","Tý"),("suu","Sửu"),("dan","Dần"),("mao","Mão"),
    ("thin","Thìn"),("ty","Tỵ"),("ngo","Ngọ"),("mui","Mùi"),
    ("than","Thân"),("dau","Dậu"),("tuat","Tuất"),("hoi","Hợi")
]
CAN = ["Giáp","Ất","Bính","Đinh","Mậu","Kỷ","Canh","Tân","Nhâm","Quý"]

# Helpers
def safe_sql(s):
    if s is None:
        return "NULL"
    if not isinstance(s, str):
        s = str(s)
    s = s.replace("\\", "\\\\").replace("'", "''")
    return f"'{s}'"

def make_paragraph(seed, approx_words=200):
    base = (
        f"{seed}. Người tuổi này có những nét đặc trưng riêng về tính cách và vận mệnh. "
        "Trong nhiều giai đoạn, họ gặp thử thách xen lẫn cơ hội; yếu tố kiên trì và nỗ lực "
        "quyết định phần lớn kết quả. Giữ sức khỏe, tiết chế chi tiêu và xây dựng quan hệ "
        "là điều cần thiết để gia tăng phúc lợi. Lưu ý các năm hạn như Tam Tai, Kim Lâu, "
        "và chủ động phòng tránh rủi ro trong những năm bất lợi."
    )
    parts = []
    while len(" ".join(parts).split()) < approx_words:
        parts.append(base)
    return " ".join(parts)

def make_short(seed, approx_words=60):
    return make_paragraph(seed, approx_words=approx_words)

# Generators
def gen_lifetime(out_dir: Path, sample_only: bool):
    out = out_dir / "insert_horoscope_lifetime.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_lifetime.sql\nSET NAMES utf8mb4;\n")
        pairs = []
        for can in CAN:
            for code, chi in ZODIACS:
                pairs.append((can, chi, f"{can} {chi}", code))
        for can, chi, can_chi_text, zodiac_code in pairs:
            zodiac_id = [z[0] for z in ZODIACS].index(zodiac_code) + 1
            for gender in ("male","female"):
                # sample-only guard
                if sample_only and f.tell() > 200000:
                    continue
                overview = make_paragraph(f"Tổng quan trọn đời cho {can_chi_text} ({gender})", approx_words=400)
                career = make_paragraph(f"Công danh & sự nghiệp cho {can_chi_text}", approx_words=300)
                love = make_paragraph(f"Tình cảm & hôn nhân cho {can_chi_text}", approx_words=280)
                health = make_paragraph(f"Sức khỏe cho {can_chi_text}", approx_words=220)
                family = make_paragraph(f"Gia đạo cho {can_chi_text}", approx_words=200)
                fortune = make_paragraph(f"Tài lộc cho {can_chi_text}", approx_words=260)
                unlucky = "Một số hạn cần lưu ý: Tam Tai, Kim Lâu, Hoang Ốc theo chu kỳ tuổi."
                advice = make_paragraph(f"Lời khuyên cho {can_chi_text}", approx_words=200)
                metadata = json.dumps({"generated_by":"gen_v1","zodiac_code":zodiac_code}, ensure_ascii=False)
                sql = ("INSERT INTO horoscope_lifetime (zodiac_id, can_chi, gender, overview, career, love, health, family, fortune, unlucky, advice, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {safe_sql(can_chi_text)}, {safe_sql(gender)}, {safe_sql(overview)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(family)}, {safe_sql(fortune)}, {safe_sql(unlucky)}, {safe_sql(advice)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                f.write(sql)
    return out

def gen_yearly(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_yearly.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_yearly.sql\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                if sample_only and f.tell() > 200000:
                    continue
                summary = make_paragraph(f"Tổng quan {name} năm {y}", approx_words=300)
                career = make_paragraph(f"Công danh {name} năm {y}", approx_words=220)
                love = make_paragraph(f"Tình cảm {name} năm {y}", approx_words=220)
                finance = make_paragraph(f"Tài chính {name} năm {y}", approx_words=220)
                health = make_paragraph(f"Sức khỏe {name} năm {y}", approx_words=200)
                warnings = "Các hạn và lưu ý: kiểm tra từng tuổi cụ thể."
                metadata = json.dumps({
                    "generated_by":"gen_v1",
                    "lucky_color": random.choice(["Đỏ","Xanh","Đen","Trắng","Vàng","Tím"]),
                    "lucky_number": ",".join(map(str, random.sample(range(1,10),2)))
                }, ensure_ascii=False)
                sql = ("INSERT INTO horoscope_yearly (zodiac_id, year, summary, career, love, health, fortune, warnings, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {y}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(finance)}, {safe_sql(warnings)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                f.write(sql)
    return out

def gen_monthly(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_monthly.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_monthly.sql\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                for m in range(1,13):
                    if sample_only and f.tell() > 300000:
                        continue
                    summary = make_paragraph(f"Tổng quan {name} tháng {m}/{y}", approx_words=250)
                    career = make_short(f"Công việc {name} tháng {m}/{y}", approx_words=120)
                    love = make_short(f"Tình cảm {name} tháng {m}/{y}", approx_words=120)
                    health = make_short(f"Sức khỏe {name} tháng {m}/{y}", approx_words=100)
                    fortune = make_short(f"Tài lộc {name} tháng {m}/{y}", approx_words=120)
                    metadata = json.dumps({"generated_by":"gen_v1","month":m,"year":y}, ensure_ascii=False)
                    sql = ("INSERT INTO horoscope_monthly (zodiac_id, year, month, summary, career, love, health, fortune, metadata, created_at, updated_at) VALUES "
                           f"({zodiac_id}, {y}, {m}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(fortune)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                    f.write(sql)
    return out

def gen_daily(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_daily.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_daily.sql\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                start = date(y,1,1)
                end = date(y,12,31)
                cur = start
                while cur <= end:
                    if sample_only and f.tell() > 500000:
                        cur = cur + timedelta(days=1)
                        continue
                    summary = make_short(f"Tổng quan ngày {cur} tuổi {name}", approx_words=90)
                    career = "Nên: tập trung công việc chính, tận dụng cơ hội hợp tác."
                    love = "Tình cảm: chú ý giao tiếp, tránh hiểu nhầm."
                    health = "Sức khỏe: giữ nhịp sinh hoạt, nghỉ ngơi hợp lý."
                    fortune = "Tài lộc: ổn định, tránh đầu tư mạo hiểm."
                    lucky_color = random.choice(["Đỏ","Xanh","Đen","Trắng","Vàng","Tím"])
                    lucky_number = ",".join(map(str, random.sample(range(1,10),2)))
                    metadata = json.dumps({"generated_by":"gen_v1","date":cur.isoformat()}, ensure_ascii=False)
                    sql = ("INSERT INTO horoscope_daily (zodiac_id, solar_date, summary, career, love, health, fortune, lucky_color, lucky_number, metadata, created_at, updated_at) VALUES "
                           f"({zodiac_id}, {safe_sql(cur.isoformat())}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(fortune)}, {safe_sql(lucky_color)}, {safe_sql(lucky_number)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                    f.write(sql)
                    cur = cur + timedelta(days=1)
    return out

# CLI
def main():
    parser = argparse.ArgumentParser(description="Generate horoscope SQL inserts (lifetime/yearly/monthly/daily).")
    parser.add_argument("--out-dir", default="./generated_horoscope.sql", help="Output directory")
    parser.add_argument("--years", nargs="+", type=int, default=[2024,2025,2026], help="Years to generate")
    parser.add_argument("--sample-only", action="store_true", help="Generate small sample only")
    args = parser.parse_args()

    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    print("Generating files into:", out_dir.resolve())
    f1 = gen_lifetime(out_dir, sample_only=args.sample_only)     # lifetime = can-chi × gender
    f2 = gen_yearly(out_dir, args.years, sample_only=args.sample_only)
    f3 = gen_monthly(out_dir, args.years, sample_only=args.sample_only)
    f4 = gen_daily(out_dir, args.years, sample_only=args.sample_only)

    print("Done. Files written:")
    for p in (f1,f2,f3,f4):
        print(" -", p.name)

if __name__ == "__main__":
    main()

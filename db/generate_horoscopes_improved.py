#!/usr/bin/env python3
"""
generate_horoscopes_improved.py

Improved version with 30+ templates per section to avoid repetition.
Generates SQL INSERT files for:
 - insert_horoscope_lifetime.sql  (60 can-chi * 2 genders = 120 records)
 - insert_horoscope_yearly.sql    (12 zodiac * years)
 - insert_horoscope_monthly.sql   (12 zodiac * years * 12 months)
 - insert_horoscope_daily.sql     (12 zodiac * years * days)

Usage examples:
  python3 generate_horoscopes_improved.py --out-dir ./generated_sql
  python3 generate_horoscopes_improved.py --out-dir ./generated_sql --years 2024 2025 2026
  python3 generate_horoscopes_improved.py --out-dir ./generated_sql --sample-only
"""

import argparse
import json
import random
import hashlib
from pathlib import Path
from datetime import date, timedelta

# Import templates
import sys
sys.path.insert(0, str(Path(__file__).parent))
from templates import lifetime_templates, yearly_templates, monthly_templates, daily_templates

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

def get_seed(can_chi, zodiac_code, gender, year=None, month=None, day=None):
    """Generate consistent seed for template selection"""
    seed_str = f"{can_chi}_{zodiac_code}_{gender}"
    if year:
        seed_str += f"_{year}"
    if month:
        seed_str += f"_{month}"
    if day:
        seed_str += f"_{day}"
    return int(hashlib.md5(seed_str.encode()).hexdigest(), 16)

# Generators
def gen_lifetime(out_dir: Path, sample_only: bool):
    out = out_dir / "insert_horoscope_lifetime.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_lifetime.sql (Improved version)\nSET NAMES utf8mb4;\n")
        pairs = []
        for can in CAN:
            for code, chi in ZODIACS:
                pairs.append((can, chi, f"{can} {chi}", code))
        
        for can, chi, can_chi_text, zodiac_code in pairs:
            zodiac_id = [z[0] for z in ZODIACS].index(zodiac_code) + 1
            for gender in ("male","female"):
                if sample_only and f.tell() > 200000:
                    continue
                
                seed = get_seed(can_chi_text, zodiac_code, gender)
                
                # Generate content using templates
                overview = lifetime_templates.get_template(
                    lifetime_templates.OVERVIEW_TEMPLATES, seed
                )(can_chi_text, gender)
                
                career = lifetime_templates.get_template(
                    lifetime_templates.CAREER_TEMPLATES, seed + 1
                )(can_chi_text, gender)
                
                # Love with month groups
                love_group1 = lifetime_templates.get_template(
                    lifetime_templates.LOVE_TEMPLATES_GROUP1, seed + 2
                )(can_chi_text, gender)
                love_group2 = lifetime_templates.get_template(
                    lifetime_templates.LOVE_TEMPLATES_GROUP2, seed + 3
                )(can_chi_text, gender)
                love_group3 = lifetime_templates.get_template(
                    lifetime_templates.LOVE_TEMPLATES_GROUP3, seed + 4
                )(can_chi_text, gender)
                love = f"{love_group1}\n\n{love_group2}\n\n{love_group3}"
                
                health = lifetime_templates.get_template(
                    lifetime_templates.OVERVIEW_TEMPLATES, seed + 5
                )(can_chi_text, gender)
                
                family = lifetime_templates.get_template(
                    lifetime_templates.CAREER_TEMPLATES, seed + 6
                )(can_chi_text, gender)
                
                fortune = lifetime_templates.get_template(
                    lifetime_templates.OVERVIEW_TEMPLATES, seed + 7
                )(can_chi_text, gender)
                
                unlucky = lifetime_templates.get_template(
                    lifetime_templates.DIFFICULT_YEARS_TEMPLATES, seed + 8
                )(can_chi_text)
                
                advice = lifetime_templates.get_template(
                    lifetime_templates.RITUAL_GUIDANCE_TEMPLATES, seed + 9
                )(can_chi_text)
                
                # Compatible and incompatible ages
                compatible_ages = ["Canh Thìn", "Bính Tuất", "Kỷ Sửu", "Đinh Sửu"]
                incompatible_ages = ["Quý Mùi", "Giáp Thân", "Canh Dần", "Ất Mùi", "Bính Thân", "Mậu Dần", "Nhâm Thân"]
                difficult_years = [26, 29, 33, 40]
                
                # Yearly progression
                yearly_progression = {
                    "26-29": lifetime_templates.get_template(
                        lifetime_templates.YEARLY_PROGRESSION_TEMPLATES, seed + 10
                    )(can_chi_text),
                    "30-35": "Từ năm 30 tuổi đến năm 35 tuổi, cuộc sống sẽ có nhiều biến chuyển tích cực. Năm 30 tuổi là năm hoàn toàn đẹp. Năm 31 tuổi gặp nhiều trở ngại trong công việc và nghề nghiệp. Năm 32 tuổi (tháng 8 và 9) cần thận trọng để tránh ốm đau hoặc rủi ro. Năm 33 tuổi gặp nhiều thăng trầm và đau buồn. Năm 34 tuổi cuộc sống đầy đủ về tài chính, công việc và tình cảm. Năm 35 tuổi may mắn đồng hành.",
                    "36-40": "Từ năm 36 tuổi đến năm 40 tuổi, cuộc sống ổn định hơn. Năm 36 tuổi là năm ổn định, không có sự cố lớn. Năm 37 tuổi có nhiều cơ hội thuận lợi cho công việc và gia đình. Năm 38 tuổi sẽ gặp lo lắng trong tình cảm và cuộc sống gia đình. Năm 39 và 40 tuổi có nhiều triển vọng để xây dựng cuộc sống.",
                    "41-45": "Từ năm 41 tuổi đến năm 45 tuổi, cần chú ý đến sức khỏe. Năm 41 tuổi (tháng 12) cần chú ý đến sức khỏe để tránh ốm đau và bệnh tật. Năm 42 tuổi gặp một số rủi ro nhỏ, nhưng không ảnh hưởng đáng kể đến sự nghiệp.",
                }
                
                # Build metadata
                metadata = {
                    "generated_by": "gen_v2_improved",
                    "zodiac_code": zodiac_code,
                    "sections": {
                        "tong_quat": overview,
                        "tinh_duyen": {
                            "group1": love_group1,
                            "group2": love_group2,
                            "group3": love_group3,
                        },
                        "gia_dinh_su_nghiep": family,
                        "tai_van": fortune,
                        "tuoi_hop_lam_an": compatible_ages,
                        "nam_kho_khan": difficult_years,
                        "dien_bien_tung_nam": yearly_progression,
                        "tuoi_dai_ky": incompatible_ages,
                        "nghi_le": {
                            "ngay": 25,
                            "gio": "19h-21h",
                            "huong": "Đông",
                            "mau_sac": "xanh",
                            "so_den": 20,
                        }
                    },
                    "astrology": {
                        "cung": "CÀN",  # This should be calculated based on can_chi
                        "xuong": chi,
                        "mang": "BẠCH LẠP KIM",  # This should be calculated
                    }
                }
                
                sql = ("INSERT INTO horoscope_lifetime (zodiac_id, can_chi, gender, overview, career, love, health, family, fortune, unlucky, advice, love_by_month_group1, love_by_month_group2, love_by_month_group3, compatible_ages, difficult_years, incompatible_ages, yearly_progression, ritual_guidance, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {safe_sql(can_chi_text)}, {safe_sql(gender)}, {safe_sql(overview)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(family)}, {safe_sql(fortune)}, {safe_sql(unlucky)}, {safe_sql(advice)}, {safe_sql(love_group1)}, {safe_sql(love_group2)}, {safe_sql(love_group3)}, {safe_sql(json.dumps(compatible_ages, ensure_ascii=False))}, {safe_sql(json.dumps(difficult_years, ensure_ascii=False))}, {safe_sql(json.dumps(incompatible_ages, ensure_ascii=False))}, {safe_sql(json.dumps(yearly_progression, ensure_ascii=False))}, {safe_sql(advice)}, {safe_sql(json.dumps(metadata, ensure_ascii=False))}, NOW(), NOW());\n")
                f.write(sql)
    return out

def gen_yearly(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_yearly.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_yearly.sql (Improved version)\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                if sample_only and f.tell() > 200000:
                    continue
                
                seed = get_seed("", code, "", year=y)
                
                # Generate content using templates
                summary = yearly_templates.get_template(
                    yearly_templates.TU_TRU_TEMPLATES, seed
                )(name, y)
                
                career = yearly_templates.get_template(
                    yearly_templates.CUNG_MENH_TEMPLATES, seed + 1
                )(name, y)
                
                love = yearly_templates.get_template(
                    yearly_templates.CUNG_TAM_HOP_TEMPLATES, seed + 2
                )(name, y)
                
                finance = yearly_templates.get_template(
                    yearly_templates.TU_TRU_TEMPLATES, seed + 3
                )(name, y)
                
                health = yearly_templates.get_template(
                    yearly_templates.CUNG_NHI_HOP_TEMPLATES, seed + 4
                )(name, y)
                
                warnings = yearly_templates.get_template(
                    yearly_templates.VAN_HAN_TEMPLATES, seed + 5
                )(name, y)
                
                # Structured sections
                cung_menh = yearly_templates.get_template(
                    yearly_templates.CUNG_MENH_TEMPLATES, seed + 6
                )(name, y)
                
                cung_xung_chieu = yearly_templates.get_template(
                    yearly_templates.CUNG_XUNG_CHIEU_TEMPLATES, seed + 7
                )(name, y)
                
                cung_tam_hop = yearly_templates.get_template(
                    yearly_templates.CUNG_TAM_HOP_TEMPLATES, seed + 8
                )(name, y)
                
                cung_nhi_hop = yearly_templates.get_template(
                    yearly_templates.CUNG_NHI_HOP_TEMPLATES, seed + 9
                )(name, y)
                
                van_han_json = {
                    "han_nam": warnings,
                    "han_tuoi": "Hoang Ốc",
                    "hoa_giai": "Thực hiện các biện pháp phòng tránh, cẩn thận trong giao thông, tập thể dục thường xuyên."
                }
                
                tu_tru_json = {
                    "tong_quan": summary,
                    "cong_viec": career,
                    "tai_chinh": finance,
                    "suc_khoe": health
                }
                
                phong_thuy_json = {
                    "nguoi_xong_nha": str(y - 4),
                    "ngay_xuat_hanh": "mùng 9 âm lịch",
                    "huong": "Tây, Tây Nam",
                    "ngay_khai_truong": "Tháng 4, 6, 9 âm lịch"
                }
                
                # Q&A section
                qa_list = []
                for qa_template in yearly_templates.QA_TEMPLATES[:3]:  # First 3 Q&A
                    qa_list.append({
                        "question": qa_template["question"](name, y),
                        "answer": qa_template["answer"](name, y)
                    })
                
                conclusion = yearly_templates.get_template(
                    yearly_templates.CONCLUSION_TEMPLATES, seed + 10
                )(name, y)
                
                # Monthly breakdown
                monthly_breakdown = {}
                for m in range(1, 13):
                    monthly_breakdown[str(m)] = yearly_templates.get_template(
                        yearly_templates.MONTHLY_BREAKDOWN_TEMPLATES, seed + m
                    )(name, y)
                
                # Metadata
                metadata = {
                    "generated_by": "gen_v2_improved",
                    "lucky_color": random.choice(["Đỏ", "Xanh", "Hồng", "Vàng"]),
                    "lucky_number": ",".join(map(str, random.sample(range(1, 10), 2))),
                    "sao_han": "Mộc Đức",
                    "mau_sac_hop": ["Đỏ", "Hồng"],
                    "mau_sac_ky": ["Đen"],
                    "ngu_hanh_nam": "Hỏa"
                }
                
                sql = ("INSERT INTO horoscope_yearly (zodiac_id, year, summary, career, love, health, fortune, warnings, cung_menh, cung_xung_chieu, cung_tam_hop, cung_nhi_hop, van_han, tu_tru, phong_thuy, qa_section, conclusion, monthly_breakdown, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {y}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(finance)}, {safe_sql(warnings)}, {safe_sql(cung_menh)}, {safe_sql(cung_xung_chieu)}, {safe_sql(cung_tam_hop)}, {safe_sql(cung_nhi_hop)}, {safe_sql(json.dumps(van_han_json, ensure_ascii=False))}, {safe_sql(json.dumps(tu_tru_json, ensure_ascii=False))}, {safe_sql(json.dumps(phong_thuy_json, ensure_ascii=False))}, {safe_sql(json.dumps(qa_list, ensure_ascii=False))}, {safe_sql(conclusion)}, {safe_sql(json.dumps(monthly_breakdown, ensure_ascii=False))}, {safe_sql(json.dumps(metadata, ensure_ascii=False))}, NOW(), NOW());\n")
                f.write(sql)
    return out

def gen_monthly(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_monthly.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_monthly.sql (Improved version)\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                for m in range(1, 13):
                    if sample_only and f.tell() > 300000:
                        continue
                    
                    seed = get_seed("", code, "", year=y, month=m)
                    
                    # Generate varied content per month
                    summary = monthly_templates.get_monthly_template(
                        "career", name, m, y, seed
                    ) + " " + monthly_templates.get_monthly_template(
                        "fortune", name, m, y, seed + 1
                    )
                    
                    career = monthly_templates.get_monthly_template(
                        "career", name, m, y, seed + 2
                    )
                    
                    love = monthly_templates.get_monthly_template(
                        "love", name, m, y, seed + 3
                    )
                    
                    health = monthly_templates.get_monthly_template(
                        "health", name, m, y, seed + 4
                    )
                    
                    fortune = monthly_templates.get_monthly_template(
                        "fortune", name, m, y, seed + 5
                    )
                    
                    metadata = {
                        "generated_by": "gen_v2_improved",
                        "month": m,
                        "year": y
                    }
                    
                    sql = ("INSERT INTO horoscope_monthly (zodiac_id, year, month, summary, career, love, health, fortune, metadata, created_at, updated_at) VALUES "
                           f"({zodiac_id}, {y}, {m}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(fortune)}, {safe_sql(json.dumps(metadata, ensure_ascii=False))}, NOW(), NOW());\n")
                    f.write(sql)
    return out

def gen_daily(out_dir: Path, years, sample_only: bool):
    out = out_dir / "insert_horoscope_daily.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_daily.sql (Improved version)\nSET NAMES utf8mb4;\n")
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                start = date(y, 1, 1)
                end = date(y, 12, 31)
                cur = start
                while cur <= end:
                    if sample_only and f.tell() > 500000:
                        cur = cur + timedelta(days=1)
                        continue
                    
                    date_str = cur.isoformat()
                    seed = get_seed("", code, "", year=y, day=cur.timetuple().tm_yday)
                    
                    # Generate varied content per day
                    summary = daily_templates.get_daily_template(
                        "career", name, date_str, seed
                    ) + " " + daily_templates.get_daily_template(
                        "fortune", name, date_str, seed + 1
                    )
                    
                    career = daily_templates.get_daily_template(
                        "career", name, date_str, seed + 2
                    )
                    
                    love = daily_templates.get_daily_template(
                        "love", name, date_str, seed + 3
                    )
                    
                    health = daily_templates.get_daily_template(
                        "health", name, date_str, seed + 4
                    )
                    
                    fortune = daily_templates.get_daily_template(
                        "fortune", name, date_str, seed + 5
                    )
                    
                    lucky_color = random.choice(["Đỏ", "Xanh", "Đen", "Trắng", "Vàng", "Tím", "Hồng"])
                    lucky_number = ",".join(map(str, random.sample(range(1, 10), 2)))
                    
                    metadata = {
                        "generated_by": "gen_v2_improved",
                        "date": date_str
                    }
                    
                    sql = ("INSERT INTO horoscope_daily (zodiac_id, solar_date, summary, career, love, health, fortune, lucky_color, lucky_number, metadata, created_at, updated_at) VALUES "
                           f"({zodiac_id}, {safe_sql(date_str)}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(fortune)}, {safe_sql(lucky_color)}, {safe_sql(lucky_number)}, {safe_sql(json.dumps(metadata, ensure_ascii=False))}, NOW(), NOW());\n")
                    f.write(sql)
                    cur = cur + timedelta(days=1)
    return out

# CLI
def main():
    parser = argparse.ArgumentParser(description="Generate improved horoscope SQL inserts with diverse templates.")
    parser.add_argument("--out-dir", default="./generated_horoscope.sql", help="Output directory")
    parser.add_argument("--years", nargs="+", type=int, default=[2024, 2025, 2026], help="Years to generate")
    parser.add_argument("--sample-only", action="store_true", help="Generate small sample only")
    args = parser.parse_args()

    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    print("Generating improved horoscope files into:", out_dir.resolve())
    f1 = gen_lifetime(out_dir, sample_only=args.sample_only)
    f2 = gen_yearly(out_dir, args.years, sample_only=args.sample_only)
    f3 = gen_monthly(out_dir, args.years, sample_only=args.sample_only)
    f4 = gen_daily(out_dir, args.years, sample_only=args.sample_only)

    print("Done. Files written:")
    for p in (f1, f2, f3, f4):
        print(" -", p.name)

if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""
generate_horoscopes_ai.py

AI-powered horoscope generation using OpenAI GPT-4 or Anthropic Claude.
This script generates more natural and diverse content compared to template-based generation.

Requirements:
  pip install openai anthropic

Usage:
  export OPENAI_API_KEY=your_key  # or ANTHROPIC_API_KEY
  python3 generate_horoscopes_ai.py --out-dir ./generated_sql --years 2024 2025 --provider openai
  python3 generate_horoscopes_ai.py --out-dir ./generated_sql --years 2024 2025 --provider anthropic --sample-only
"""

import argparse
import json
import time
from pathlib import Path
from datetime import date, timedelta

try:
    import openai
    OPENAI_AVAILABLE = True
except ImportError:
    OPENAI_AVAILABLE = False

try:
    import anthropic
    ANTHROPIC_AVAILABLE = True
except ImportError:
    ANTHROPIC_AVAILABLE = False

# Zodiac order
ZODIACS = [
    ("ti","Tý"),("suu","Sửu"),("dan","Dần"),("mao","Mão"),
    ("thin","Thìn"),("ty","Tỵ"),("ngo","Ngọ"),("mui","Mùi"),
    ("than","Thân"),("dau","Dậu"),("tuat","Tuất"),("hoi","Hợi")
]
CAN = ["Giáp","Ất","Bính","Đinh","Mậu","Kỷ","Canh","Tân","Nhâm","Quý"]

def safe_sql(s):
    if s is None:
        return "NULL"
    if not isinstance(s, str):
        s = str(s)
    s = s.replace("\\", "\\\\").replace("'", "''")
    return f"'{s}'"

def generate_with_openai(prompt, model="gpt-4", max_tokens=2000):
    """Generate content using OpenAI GPT-4"""
    if not OPENAI_AVAILABLE:
        raise ImportError("openai package not installed. Run: pip install openai")
    
    client = openai.OpenAI()
    
    try:
        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": "Bạn là một chuyên gia tử vi Việt Nam. Hãy viết nội dung tử vi chi tiết, tự nhiên và đa dạng bằng tiếng Việt."},
                {"role": "user", "content": prompt}
            ],
            max_tokens=max_tokens,
            temperature=0.8,  # Higher temperature for more diversity
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        print(f"Error generating with OpenAI: {e}")
        return None

def generate_with_anthropic(prompt, model="claude-3-opus-20240229", max_tokens=2000):
    """Generate content using Anthropic Claude"""
    if not ANTHROPIC_AVAILABLE:
        raise ImportError("anthropic package not installed. Run: pip install anthropic")
    
    client = anthropic.Anthropic()
    
    try:
        message = client.messages.create(
            model=model,
            max_tokens=max_tokens,
            temperature=0.8,
            system="Bạn là một chuyên gia tử vi Việt Nam. Hãy viết nội dung tử vi chi tiết, tự nhiên và đa dạng bằng tiếng Việt.",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        return message.content[0].text.strip()
    except Exception as e:
        print(f"Error generating with Anthropic: {e}")
        return None

def generate_content(prompt, provider="openai", model=None):
    """Generate content using specified provider"""
    if provider == "openai":
        model = model or "gpt-4"
        return generate_with_openai(prompt, model)
    elif provider == "anthropic":
        model = model or "claude-3-opus-20240229"
        return generate_with_anthropic(prompt, model)
    else:
        raise ValueError(f"Unknown provider: {provider}")

def gen_lifetime_ai(out_dir: Path, provider, sample_only: bool):
    """Generate lifetime horoscope using AI"""
    out = out_dir / "insert_horoscope_lifetime_ai.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_lifetime_ai.sql (AI-generated)\nSET NAMES utf8mb4;\n")
        
        pairs = []
        for can in CAN:
            for code, chi in ZODIACS:
                pairs.append((can, chi, f"{can} {chi}", code))
        
        count = 0
        for can, chi, can_chi_text, zodiac_code in pairs:
            zodiac_id = [z[0] for z in ZODIACS].index(zodiac_code) + 1
            for gender in ("male", "female"):
                if sample_only and count >= 5:  # Limit for sample
                    break
                
                gender_vn = "nam" if gender == "male" else "nữ"
                
                # Generate overview
                prompt_overview = f"""Viết một đoạn văn dài (khoảng 400 từ) về tổng quan trọn đời cho người tuổi {can_chi_text} {gender_vn} mạng. 
Hãy viết tự nhiên, đa dạng, không lặp lại. Bao gồm thông tin về tính cách, vận mệnh, và các giai đoạn quan trọng trong cuộc đời."""
                
                print(f"Generating overview for {can_chi_text} {gender_vn}...")
                overview = generate_content(prompt_overview, provider)
                if not overview:
                    overview = f"Tổng quan trọn đời cho {can_chi_text} {gender_vn} mạng."
                
                time.sleep(1)  # Rate limiting
                
                # Generate career
                prompt_career = f"""Viết một đoạn văn (khoảng 300 từ) về công danh và sự nghiệp cho người tuổi {can_chi_text} {gender_vn} mạng.
Hãy viết chi tiết về các giai đoạn phát triển sự nghiệp, các cơ hội và thách thức."""
                
                print(f"Generating career for {can_chi_text} {gender_vn}...")
                career = generate_content(prompt_career, provider)
                if not career:
                    career = f"Công danh và sự nghiệp cho {can_chi_text}."
                
                time.sleep(1)
                
                # Generate love with month groups
                prompt_love = f"""Viết về tình duyên cho người tuổi {can_chi_text} {gender_vn} mạng, chia thành 3 nhóm:
- Nhóm 1 (tháng 5, 6, 9): sẽ phải trải qua thay đổi đến ba lần
- Nhóm 2 (tháng 1, 2, 7, 10, 11, 12): sẽ phải trải qua hai lần thay đổi
- Nhóm 3 (tháng 3, 4, 8): tình duyên thuận lợi
Hãy viết chi tiết cho từng nhóm."""
                
                print(f"Generating love for {can_chi_text} {gender_vn}...")
                love = generate_content(prompt_love, provider)
                if not love:
                    love = f"Tình duyên cho {can_chi_text}."
                
                time.sleep(1)
                
                # Generate other sections similarly...
                health = f"Sức khỏe cho {can_chi_text} {gender_vn} mạng."
                family = f"Gia đạo cho {can_chi_text} {gender_vn} mạng."
                fortune = f"Tài lộc cho {can_chi_text} {gender_vn} mạng."
                unlucky = "Một số hạn cần lưu ý: Tam Tai, Kim Lâu, Hoang Ốc theo chu kỳ tuổi."
                advice = f"Lời khuyên cho {can_chi_text} {gender_vn} mạng."
                
                metadata = json.dumps({
                    "generated_by": f"gen_ai_{provider}",
                    "zodiac_code": zodiac_code
                }, ensure_ascii=False)
                
                sql = ("INSERT INTO horoscope_lifetime (zodiac_id, can_chi, gender, overview, career, love, health, family, fortune, unlucky, advice, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {safe_sql(can_chi_text)}, {safe_sql(gender)}, {safe_sql(overview)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(family)}, {safe_sql(fortune)}, {safe_sql(unlucky)}, {safe_sql(advice)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                f.write(sql)
                
                count += 1
                print(f"Completed {count} records. Waiting 2 seconds before next...")
                time.sleep(2)  # Rate limiting between records
    
    return out

def gen_yearly_ai(out_dir: Path, years, provider, sample_only: bool):
    """Generate yearly horoscope using AI"""
    out = out_dir / "insert_horoscope_yearly_ai.sql"
    with out.open("w", encoding="utf-8") as f:
        f.write("-- insert_horoscope_yearly_ai.sql (AI-generated)\nSET NAMES utf8mb4;\n")
        
        count = 0
        for idx, (code, name) in enumerate(ZODIACS):
            zodiac_id = idx + 1
            for y in years:
                if sample_only and count >= 5:
                    break
                
                # Generate summary
                prompt = f"""Viết một đoạn văn dài (khoảng 500 từ) về tử vi năm {y} cho người tuổi {name}.
Bao gồm: Cung Mệnh với các sao và giải thích, Cung Xung Chiếu, Cung Tam Hợp, Vận hạn, Tứ trụ, và các khía cạnh về công việc, tài chính, tình cảm, sức khỏe.
Hãy viết tự nhiên, chi tiết và đa dạng."""
                
                print(f"Generating yearly horoscope for {name} year {y}...")
                summary = generate_content(prompt, provider, max_tokens=3000)
                if not summary:
                    summary = f"Tổng quan {name} năm {y}."
                
                # Split into sections (simplified)
                career = summary[:500] if len(summary) > 500 else summary
                love = summary[500:1000] if len(summary) > 1000 else summary
                finance = summary[1000:1500] if len(summary) > 1500 else summary
                health = summary[1500:2000] if len(summary) > 2000 else summary
                warnings = "Các hạn và lưu ý cần chú ý trong năm."
                
                metadata = json.dumps({
                    "generated_by": f"gen_ai_{provider}",
                    "lucky_color": "Đỏ",
                    "lucky_number": "3,7"
                }, ensure_ascii=False)
                
                sql = ("INSERT INTO horoscope_yearly (zodiac_id, year, summary, career, love, health, fortune, warnings, metadata, created_at, updated_at) VALUES "
                       f"({zodiac_id}, {y}, {safe_sql(summary)}, {safe_sql(career)}, {safe_sql(love)}, {safe_sql(health)}, {safe_sql(finance)}, {safe_sql(warnings)}, {safe_sql(metadata)}, NOW(), NOW());\n")
                f.write(sql)
                
                count += 1
                time.sleep(2)
    
    return out

# CLI
def main():
    parser = argparse.ArgumentParser(description="Generate AI-powered horoscope SQL inserts.")
    parser.add_argument("--out-dir", default="./generated_horoscope_ai.sql", help="Output directory")
    parser.add_argument("--years", nargs="+", type=int, default=[2024, 2025], help="Years to generate")
    parser.add_argument("--provider", choices=["openai", "anthropic"], default="openai", help="AI provider")
    parser.add_argument("--sample-only", action="store_true", help="Generate small sample only (for testing)")
    args = parser.parse_args()

    # Check availability
    if args.provider == "openai" and not OPENAI_AVAILABLE:
        print("Error: openai package not installed. Run: pip install openai")
        return
    if args.provider == "anthropic" and not ANTHROPIC_AVAILABLE:
        print("Error: anthropic package not installed. Run: pip install anthropic")
        return

    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    print(f"Generating AI-powered horoscope files using {args.provider} into:", out_dir.resolve())
    print("WARNING: This will make API calls and may incur costs!")
    
    if not args.sample_only:
        response = input("Continue? (yes/no): ")
        if response.lower() != "yes":
            print("Cancelled.")
            return

    f1 = gen_lifetime_ai(out_dir, args.provider, sample_only=args.sample_only)
    f2 = gen_yearly_ai(out_dir, args.years, args.provider, sample_only=args.sample_only)

    print("Done. Files written:")
    for p in (f1, f2):
        if p:
            print(" -", p.name)

if __name__ == "__main__":
    main()

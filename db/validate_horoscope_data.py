#!/usr/bin/env python3
"""
validate_horoscope_data.py

Validates horoscope SQL files before import:
- Checks for duplicate keys
- Validates foreign key references
- Counts expected rows
- Validates data format

Usage:
  python3 validate_horoscope_data.py [--dir ./generated_horoscope.sql] [--verbose]
"""

import argparse
import re
import sys
from pathlib import Path
from collections import defaultdict
from typing import Dict, List, Tuple, Set

# Expected zodiac IDs (1-12)
ZODIAC_IDS = set(range(1, 13))

# Can-Chi combinations (60 total)
CAN = ["Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"]
CHI = ["Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"]

def get_can_chi_combinations() -> Set[str]:
    """Generate all 60 valid Can-Chi combinations."""
    combinations = set()
    for i in range(60):
        can = CAN[i % 10]
        chi = CHI[i % 12]
        combinations.add(f"{can} {chi}")
    return combinations

VALID_CAN_CHI = get_can_chi_combinations()
VALID_GENDERS = {"male", "female"}


class ValidationResult:
    def __init__(self, file_name: str):
        self.file_name = file_name
        self.row_count = 0
        self.errors: List[str] = []
        self.warnings: List[str] = []
        self.duplicates: Dict[str, int] = defaultdict(int)

    def add_error(self, message: str):
        self.errors.append(message)

    def add_warning(self, message: str):
        self.warnings.append(message)

    def is_valid(self) -> bool:
        return len(self.errors) == 0


def parse_insert_values(line: str) -> Tuple[str, List[str]]:
    """Parse INSERT statement to extract table name and values."""
    # Match INSERT INTO table_name (...) VALUES (...)
    match = re.match(r"INSERT INTO (\w+)\s*\([^)]+\)\s*VALUES\s*\((.+)\);?$", line, re.IGNORECASE)
    if match:
        table = match.group(1)
        values_str = match.group(2)
        # Simple value extraction (handles basic cases)
        values = []
        in_quote = False
        current = []
        for char in values_str:
            if char == "'" and (not current or current[-1] != '\\'):
                in_quote = not in_quote
                current.append(char)
            elif char == ',' and not in_quote:
                values.append(''.join(current).strip())
                current = []
            else:
                current.append(char)
        if current:
            values.append(''.join(current).strip())
        return table, values
    return None, []


def validate_lifetime_file(file_path: Path, verbose: bool) -> ValidationResult:
    """Validate horoscope_lifetime.sql file."""
    result = ValidationResult(file_path.name)
    seen_keys: Set[Tuple[str, str, str]] = set()  # (zodiac_id, can_chi, gender)

    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('--') or line.startswith('SET'):
                continue

            table, values = parse_insert_values(line)
            if table and table.lower() == 'horoscope_lifetime':
                result.row_count += 1
                
                if len(values) >= 3:
                    try:
                        zodiac_id = int(values[0])
                        can_chi = values[1].strip("'")
                        gender = values[2].strip("'")

                        # Validate zodiac_id
                        if zodiac_id not in ZODIAC_IDS:
                            result.add_error(f"Line {line_num}: Invalid zodiac_id {zodiac_id}")

                        # Validate can_chi
                        if can_chi not in VALID_CAN_CHI:
                            result.add_warning(f"Line {line_num}: Unusual can_chi '{can_chi}'")

                        # Validate gender
                        if gender not in VALID_GENDERS:
                            result.add_error(f"Line {line_num}: Invalid gender '{gender}'")

                        # Check for duplicates
                        key = (str(zodiac_id), can_chi, gender)
                        if key in seen_keys:
                            result.duplicates[f"{can_chi}:{gender}"] += 1
                            result.add_error(f"Line {line_num}: Duplicate key ({zodiac_id}, {can_chi}, {gender})")
                        seen_keys.add(key)

                    except (ValueError, IndexError) as e:
                        result.add_error(f"Line {line_num}: Parse error - {e}")

    if verbose:
        print(f"  Processed {result.row_count} rows")

    return result


def validate_yearly_file(file_path: Path, verbose: bool) -> ValidationResult:
    """Validate horoscope_yearly.sql file."""
    result = ValidationResult(file_path.name)
    seen_keys: Set[Tuple[int, int]] = set()  # (zodiac_id, year)

    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('--') or line.startswith('SET'):
                continue

            table, values = parse_insert_values(line)
            if table and table.lower() == 'horoscope_yearly':
                result.row_count += 1

                if len(values) >= 2:
                    try:
                        zodiac_id = int(values[0])
                        year = int(values[1])

                        if zodiac_id not in ZODIAC_IDS:
                            result.add_error(f"Line {line_num}: Invalid zodiac_id {zodiac_id}")

                        if year < 1900 or year > 2100:
                            result.add_warning(f"Line {line_num}: Year {year} outside typical range")

                        key = (zodiac_id, year)
                        if key in seen_keys:
                            result.duplicates[f"{zodiac_id}:{year}"] += 1
                            result.add_error(f"Line {line_num}: Duplicate key ({zodiac_id}, {year})")
                        seen_keys.add(key)

                    except (ValueError, IndexError) as e:
                        result.add_error(f"Line {line_num}: Parse error - {e}")

    if verbose:
        print(f"  Processed {result.row_count} rows")

    return result


def validate_monthly_file(file_path: Path, verbose: bool) -> ValidationResult:
    """Validate horoscope_monthly.sql file."""
    result = ValidationResult(file_path.name)
    seen_keys: Set[Tuple[int, int, int]] = set()  # (zodiac_id, year, month)

    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('--') or line.startswith('SET'):
                continue

            table, values = parse_insert_values(line)
            if table and table.lower() == 'horoscope_monthly':
                result.row_count += 1

                if len(values) >= 3:
                    try:
                        zodiac_id = int(values[0])
                        year = int(values[1])
                        month = int(values[2])

                        if zodiac_id not in ZODIAC_IDS:
                            result.add_error(f"Line {line_num}: Invalid zodiac_id {zodiac_id}")

                        if month < 1 or month > 12:
                            result.add_error(f"Line {line_num}: Invalid month {month}")

                        key = (zodiac_id, year, month)
                        if key in seen_keys:
                            result.duplicates[f"{zodiac_id}:{year}-{month}"] += 1
                            result.add_error(f"Line {line_num}: Duplicate key ({zodiac_id}, {year}, {month})")
                        seen_keys.add(key)

                    except (ValueError, IndexError) as e:
                        result.add_error(f"Line {line_num}: Parse error - {e}")

    if verbose:
        print(f"  Processed {result.row_count} rows")

    return result


def validate_daily_file(file_path: Path, verbose: bool) -> ValidationResult:
    """Validate horoscope_daily.sql file."""
    result = ValidationResult(file_path.name)
    seen_keys: Set[Tuple[int, str]] = set()  # (zodiac_id, date)

    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('--') or line.startswith('SET'):
                continue

            table, values = parse_insert_values(line)
            if table and table.lower() == 'horoscope_daily':
                result.row_count += 1

                if len(values) >= 2:
                    try:
                        zodiac_id = int(values[0])
                        date = values[1].strip("'")

                        if zodiac_id not in ZODIAC_IDS:
                            result.add_error(f"Line {line_num}: Invalid zodiac_id {zodiac_id}")

                        # Basic date format check
                        if not re.match(r'\d{4}-\d{2}-\d{2}', date):
                            result.add_error(f"Line {line_num}: Invalid date format '{date}'")

                        key = (zodiac_id, date)
                        if key in seen_keys:
                            result.duplicates[f"{zodiac_id}:{date}"] += 1
                            result.add_error(f"Line {line_num}: Duplicate key ({zodiac_id}, {date})")
                        seen_keys.add(key)

                    except (ValueError, IndexError) as e:
                        result.add_error(f"Line {line_num}: Parse error - {e}")

    if verbose:
        print(f"  Processed {result.row_count} rows")

    return result


def main():
    parser = argparse.ArgumentParser(description="Validate horoscope SQL files before import")
    parser.add_argument("--dir", default="./generated_horoscope.sql", help="Directory containing SQL files")
    parser.add_argument("--verbose", "-v", action="store_true", help="Verbose output")
    args = parser.parse_args()

    sql_dir = Path(args.dir)
    if not sql_dir.exists():
        print(f"Error: Directory not found: {sql_dir}")
        sys.exit(1)

    print("=" * 60)
    print("Horoscope Data Validator")
    print("=" * 60)
    print(f"Directory: {sql_dir.resolve()}")
    print()

    validators = {
        "insert_horoscope_lifetime.sql": validate_lifetime_file,
        "insert_horoscope_yearly.sql": validate_yearly_file,
        "insert_horoscope_monthly.sql": validate_monthly_file,
        "insert_horoscope_daily.sql": validate_daily_file,
    }

    all_valid = True
    total_rows = 0
    results: List[ValidationResult] = []

    for file_name, validator in validators.items():
        file_path = sql_dir / file_name
        if not file_path.exists():
            print(f"⚠ File not found: {file_name}")
            continue

        print(f"Validating {file_name}...")
        result = validator(file_path, args.verbose)
        results.append(result)
        total_rows += result.row_count

        if result.is_valid():
            print(f"  ✓ Valid ({result.row_count} rows)")
        else:
            print(f"  ✗ Invalid ({len(result.errors)} errors)")
            all_valid = False
            if args.verbose:
                for error in result.errors[:10]:
                    print(f"    - {error}")
                if len(result.errors) > 10:
                    print(f"    ... and {len(result.errors) - 10} more errors")

        if result.warnings and args.verbose:
            print(f"  ⚠ {len(result.warnings)} warnings")
            for warning in result.warnings[:5]:
                print(f"    - {warning}")

        if result.duplicates:
            print(f"  ⚠ {len(result.duplicates)} duplicate keys detected")

        print()

    print("=" * 60)
    print("Summary")
    print("=" * 60)
    print(f"Total files validated: {len(results)}")
    print(f"Total rows: {total_rows:,}")
    print(f"Status: {'✓ All valid' if all_valid else '✗ Validation failed'}")

    # Expected row counts
    print()
    print("Expected row counts (approximate):")
    print("  Lifetime: 120 (60 can-chi × 2 genders)")
    print("  Yearly:   12 × years (e.g., 36 for 3 years)")
    print("  Monthly:  12 × years × 12 months")
    print("  Daily:    12 × years × ~365 days")

    sys.exit(0 if all_valid else 1)


if __name__ == "__main__":
    main()


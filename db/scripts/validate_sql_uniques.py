#!/usr/bin/env python3
"""
validate_sql_uniques.py

Validates that generated SQL files don't have duplicate unique keys.
Run this before importing to prevent constraint violations.

Usage:
  python3 validate_sql_uniques.py --dir ./generated_horoscope.sql
  python3 validate_sql_uniques.py --verbose
"""

import argparse
import re
import sys
from pathlib import Path
from collections import defaultdict
from typing import Dict, List, Set, Tuple

# Unique key definitions for each table
UNIQUE_KEYS = {
    "horoscope_lifetime": ["zodiac_id", "can_chi", "gender"],
    "horoscope_yearly": ["zodiac_id", "year"],
    "horoscope_monthly": ["zodiac_id", "year", "month"],
    "horoscope_daily": ["zodiac_id", "solar_date"],
}


def extract_values(line: str) -> Tuple[str, List[str]]:
    """Extract table name and values from INSERT statement."""
    match = re.match(r"INSERT INTO (\w+)\s*\([^)]+\)\s*VALUES\s*\((.+)\);?$", line, re.IGNORECASE)
    if not match:
        return None, []
    
    table = match.group(1)
    values_str = match.group(2)
    
    # Parse values (handle quoted strings)
    values = []
    current = []
    in_quote = False
    escape_next = False
    
    for char in values_str:
        if escape_next:
            current.append(char)
            escape_next = False
            continue
        
        if char == '\\':
            escape_next = True
            current.append(char)
            continue
            
        if char == "'" and not escape_next:
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


def get_unique_key(table: str, values: List[str]) -> str:
    """Extract unique key values based on table definition."""
    if table not in UNIQUE_KEYS:
        return None
    
    # Column indices for each table (based on INSERT column order)
    column_indices = {
        "horoscope_lifetime": {"zodiac_id": 0, "can_chi": 1, "gender": 2},
        "horoscope_yearly": {"zodiac_id": 0, "year": 1},
        "horoscope_monthly": {"zodiac_id": 0, "year": 1, "month": 2},
        "horoscope_daily": {"zodiac_id": 0, "solar_date": 1},
    }
    
    key_cols = UNIQUE_KEYS[table]
    indices = column_indices.get(table, {})
    
    key_parts = []
    for col in key_cols:
        idx = indices.get(col)
        if idx is not None and idx < len(values):
            val = values[idx].strip("'\"")
            key_parts.append(f"{col}={val}")
    
    return "|".join(key_parts)


def validate_file(file_path: Path, verbose: bool) -> Tuple[int, int, Dict[str, List[int]]]:
    """
    Validate a SQL file for duplicate unique keys.
    
    Returns: (total_rows, duplicate_count, duplicates_dict)
    """
    total_rows = 0
    duplicates = defaultdict(list)
    seen_keys: Dict[str, int] = {}
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('--') or line.startswith('SET'):
                continue
            
            table, values = extract_values(line)
            if not table:
                continue
            
            total_rows += 1
            
            unique_key = get_unique_key(table.lower(), values)
            if unique_key:
                if unique_key in seen_keys:
                    duplicates[unique_key].append(line_num)
                    if verbose:
                        print(f"  Duplicate at line {line_num}: {unique_key}")
                        print(f"    First seen at line {seen_keys[unique_key]}")
                else:
                    seen_keys[unique_key] = line_num
    
    return total_rows, len(duplicates), dict(duplicates)


def main():
    parser = argparse.ArgumentParser(description="Validate SQL files for duplicate unique keys")
    parser.add_argument("--dir", default="./generated_horoscope.sql", help="Directory with SQL files")
    parser.add_argument("--verbose", "-v", action="store_true", help="Show duplicate details")
    args = parser.parse_args()
    
    sql_dir = Path(args.dir)
    if not sql_dir.exists():
        print(f"Error: Directory not found: {sql_dir}")
        sys.exit(1)
    
    print("=" * 60)
    print("SQL Uniqueness Validator")
    print("=" * 60)
    print(f"Directory: {sql_dir.resolve()}")
    print()
    
    files_to_check = [
        "insert_horoscope_lifetime.sql",
        "insert_horoscope_yearly.sql",
        "insert_horoscope_monthly.sql",
        "insert_horoscope_daily.sql",
    ]
    
    all_valid = True
    total_duplicates = 0
    
    for file_name in files_to_check:
        file_path = sql_dir / file_name
        if not file_path.exists():
            print(f"⚠ File not found: {file_name}")
            continue
        
        print(f"Checking {file_name}...")
        total_rows, dup_count, duplicates = validate_file(file_path, args.verbose)
        
        if dup_count == 0:
            print(f"  ✓ Valid - {total_rows} rows, no duplicates")
        else:
            print(f"  ✗ Invalid - {total_rows} rows, {dup_count} duplicate keys")
            all_valid = False
            total_duplicates += dup_count
            
            if not args.verbose and duplicates:
                # Show first few duplicates
                sample = list(duplicates.items())[:3]
                for key, lines in sample:
                    print(f"    Duplicate: {key} (lines: {lines[:3]}...)")
        
        print()
    
    print("=" * 60)
    print("Summary")
    print("=" * 60)
    print(f"Status: {'✓ All files valid' if all_valid else f'✗ {total_duplicates} duplicate keys found'}")
    
    if not all_valid:
        print()
        print("To fix duplicates:")
        print("  1. Regenerate the SQL files with unique data")
        print("  2. Or manually remove duplicate entries")
        print("  3. Or use INSERT IGNORE when importing")
    
    sys.exit(0 if all_valid else 1)


if __name__ == "__main__":
    main()


#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script to check for missing interpretation fragments and rules.
Compares expected combinations (star × palace × brightness) with database.
"""

import subprocess
import json
import sys

# All possible combinations
ALL_PALACES = ['MENH', 'QUAN_LOC', 'TAI_BACH', 'PHU_THE', 'TAT_ACH', 'TU_TUC', 
               'DIEN_TRACH', 'PHU_MAU', 'HUYNH_DE', 'PHUC_DUC', 'NO_BOC', 'THIEN_DI']

ALL_STARS = ['TU_VI', 'THIEN_CO', 'THAI_DUONG', 'VU_KHUC', 'THIEN_DONG', 'LIEM_TRINH', 
             'THIEN_PHU', 'THAI_AM', 'THAM_LANG', 'CU_MON', 'THIEN_TUONG', 'THIEN_LUONG', 
             'THAT_SAT', 'PHA_QUAN']

ALL_BRIGHTNESS = ['MIEU', 'VUONG', 'DAC', 'BINH', 'HAM']

def run_mysql_query(query):
    """Execute MySQL query and return results."""
    try:
        result = subprocess.run(
            ['mysql', '-uroot', '-proot', 'lich_van_nien', '-N', '-e', query],
            capture_output=True,
            text=True,
            check=True
        )
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        print(f"Error executing query: {e.stderr}", file=sys.stderr)
        return None

def get_existing_fragments():
    """Get all existing fragment codes from database."""
    query = "SELECT fragment_code FROM interpretation_fragments WHERE fragment_code NOT LIKE '%_CHU_THAN_%' AND fragment_code NOT LIKE 'CHU_MENH_%' AND fragment_code NOT LIKE 'BAN_MENH_%' AND fragment_code NOT LIKE 'CUC_%' AND fragment_code NOT LIKE 'THUAN_%' AND fragment_code NOT LIKE 'NGHICH_%' AND fragment_code NOT LIKE 'THAN_%' AND fragment_code NOT LIKE 'NO_CHINH_TINH_%' AND fragment_code NOT LIKE '%_DONG_CUNG%'"
    result = run_mysql_query(query)
    if not result:
        return set()
    
    fragments = set()
    for line in result.split('\n'):
        if line.strip():
            fragments.add(line.strip())
    
    return fragments

def get_existing_rules():
    """Get all existing rule fragment codes from database."""
    query = "SELECT fragment_code FROM interpretation_rules WHERE fragment_code NOT LIKE '%_CHU_THAN_%' AND fragment_code NOT LIKE 'CHU_MENH_%' AND fragment_code NOT LIKE 'BAN_MENH_%' AND fragment_code NOT LIKE 'CUC_%' AND fragment_code NOT LIKE 'THUAN_%' AND fragment_code NOT LIKE 'NGHICH_%' AND fragment_code NOT LIKE 'THAN_%' AND fragment_code NOT LIKE 'NO_CHINH_TINH_%' AND fragment_code NOT LIKE '%_DONG_CUNG%'"
    result = run_mysql_query(query)
    if not result:
        return set()
    
    rules = set()
    for line in result.split('\n'):
        if line.strip():
            rules.add(line.strip())
    
    return rules

def generate_expected_fragments():
    """Generate all expected fragment codes (star_palace_brightness)."""
    expected = set()
    for star in ALL_STARS:
        for palace in ALL_PALACES:
            for brightness in ALL_BRIGHTNESS:
                fragment_code = f"{star}_{palace}_{brightness}"
                expected.add(fragment_code)
    return expected

def find_missing_combinations():
    """Find missing fragment combinations."""
    print("Checking for missing fragments...")
    print("=" * 80)
    
    # Get existing fragments and rules
    existing_fragments = get_existing_fragments()
    existing_rules = get_existing_rules()
    expected_fragments = generate_expected_fragments()
    
    # Find missing fragments
    missing_fragments = expected_fragments - existing_fragments
    missing_rules = expected_fragments - existing_rules
    
    # Group missing by star and palace
    missing_by_star = {}
    missing_by_palace = {}
    missing_by_star_palace = {}
    
    for fragment_code in missing_fragments:
        parts = fragment_code.split('_')
        if len(parts) >= 4:
            # Format: STAR_PALACE_BRIGHTNESS
            # Handle multi-part star names like THIEN_CO, THAI_DUONG, etc.
            brightness = parts[-1]
            palace = parts[-2]
            
            # Star name could be 1-3 parts
            if len(parts) == 4:
                star = parts[0]
            elif len(parts) == 5:
                star = f"{parts[0]}_{parts[1]}"
            elif len(parts) == 6:
                star = f"{parts[0]}_{parts[1]}_{parts[2]}"
            else:
                continue
            
            star_palace = f"{star}_{palace}"
            
            # Group by star
            if star not in missing_by_star:
                missing_by_star[star] = []
            missing_by_star[star].append(fragment_code)
            
            # Group by palace
            if palace not in missing_by_palace:
                missing_by_palace[palace] = []
            missing_by_palace[palace].append(fragment_code)
            
            # Group by star+palace
            if star_palace not in missing_by_star_palace:
                missing_by_star_palace[star_palace] = []
            missing_by_star_palace[star_palace].append(fragment_code)
    
    # Print summary
    print(f"\nTotal expected fragments: {len(expected_fragments)}")
    print(f"Existing fragments: {len(existing_fragments)}")
    print(f"Missing fragments: {len(missing_fragments)}")
    print(f"Existing rules: {len(existing_rules)}")
    print(f"Missing rules: {len(missing_rules)}")
    
    # Print missing by star
    print("\n" + "=" * 80)
    print("MISSING FRAGMENTS BY STAR:")
    print("=" * 80)
    for star in sorted(missing_by_star.keys()):
        count = len(missing_by_star[star])
        print(f"{star:20s}: {count:3d} missing fragments")
        # Show first few examples
        examples = sorted(missing_by_star[star])[:3]
        for ex in examples:
            print(f"  - {ex}")
        if count > 3:
            print(f"  ... and {count - 3} more")
    
    # Print missing by palace
    print("\n" + "=" * 80)
    print("MISSING FRAGMENTS BY PALACE:")
    print("=" * 80)
    for palace in sorted(missing_by_palace.keys()):
        count = len(missing_by_palace[palace])
        print(f"{palace:20s}: {count:3d} missing fragments")
        # Show first few examples
        examples = sorted(missing_by_palace[palace])[:3]
        for ex in examples:
            print(f"  - {ex}")
        if count > 3:
            print(f"  ... and {count - 3} more")
    
    # Print missing combinations (star × palace)
    print("\n" + "=" * 80)
    print("MISSING COMBINATIONS (STAR × PALACE):")
    print("=" * 80)
    print(f"{'Star':<25s} {'Palace':<15s} {'Missing Count':<15s}")
    print("-" * 80)
    
    # Count missing per star×palace combination
    combination_counts = {}
    for fragment_code in missing_fragments:
        parts = fragment_code.split('_')
        if len(parts) < 3:
            continue
            
        brightness = parts[-1]
        palace = parts[-2]
        
        # Try to match known star codes
        star = None
        for known_star in ALL_STARS:
            star_parts = known_star.split('_')
            if len(parts) >= len(star_parts) + 2:
                if parts[:len(star_parts)] == star_parts:
                    star = known_star
                    break
        
        if not star:
            if len(parts) == 4:
                star = parts[0]
            elif len(parts) == 5:
                star = f"{parts[0]}_{parts[1]}"
            elif len(parts) == 6:
                star = f"{parts[0]}_{parts[1]}_{parts[2]}"
            else:
                continue
            
        key = f"{star}|{palace}"
        if key not in combination_counts:
            combination_counts[key] = 0
        combination_counts[key] += 1
    
    # Sort by missing count (descending)
    sorted_combinations = sorted(combination_counts.items(), key=lambda x: x[1], reverse=True)
    for key, count in sorted_combinations:
        star, palace = key.split('|')
        print(f"{star:<25s} {palace:<15s} {count:<15d}")
    
    # Generate detailed report for missing rules
    if missing_rules:
        print("\n" + "=" * 80)
        print("FRAGMENTS WITH MISSING RULES:")
        print("=" * 80)
        fragments_without_rules = missing_rules - missing_fragments
        if fragments_without_rules:
            print(f"{len(fragments_without_rules)} fragments exist but have no rules:")
            for frag in sorted(fragments_without_rules)[:20]:
                print(f"  - {frag}")
            if len(fragments_without_rules) > 20:
                print(f"  ... and {len(fragments_without_rules) - 20} more")
        else:
            print("All existing fragments have corresponding rules.")
    
    # Generate SQL output suggestions
    if missing_fragments:
        print("\n" + "=" * 80)
        print("SUGGESTION: Create fragments for the following combinations:")
        print("=" * 80)
        
        # Group by star to suggest files
        for star in sorted(missing_by_star.keys()):
            count = len(missing_by_star[star])
            palaces = set()
            for frag in missing_by_star[star]:
                parts = frag.split('_')
                if len(parts) >= 4:
                    palaces.add(parts[-2])
            
            print(f"\n{star}: {count} fragments missing across {len(palaces)} palaces: {', '.join(sorted(palaces))}")
    
    return {
        'total_expected': len(expected_fragments),
        'existing_fragments': len(existing_fragments),
        'missing_fragments': len(missing_fragments),
        'existing_rules': len(existing_rules),
        'missing_rules': len(missing_rules),
        'missing_by_star': {k: len(v) for k, v in missing_by_star.items()},
        'missing_by_palace': {k: len(v) for k, v in missing_by_palace.items()}
    }

if __name__ == '__main__':
    try:
        stats = find_missing_combinations()
        print("\n" + "=" * 80)
        print("CHECK COMPLETED")
        print("=" * 80)
        sys.exit(0)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        sys.exit(1)

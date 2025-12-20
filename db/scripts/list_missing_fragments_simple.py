#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Simple script to list all missing fragments"""

import subprocess

ALL_PALACES = ['MENH', 'QUAN_LOC', 'TAI_BACH', 'PHU_THE', 'TAT_ACH', 'TU_TUC', 
               'DIEN_TRACH', 'PHU_MAU', 'HUYNH_DE', 'PHUC_DUC', 'NO_BOC', 'THIEN_DI']

ALL_STARS = ['TU_VI', 'THIEN_CO', 'THAI_DUONG', 'VU_KHUC', 'THIEN_DONG', 'LIEM_TRINH', 
             'THIEN_PHU', 'THAI_AM', 'THAM_LANG', 'CU_MON', 'THIEN_TUONG', 'THIEN_LUONG', 
             'THAT_SAT', 'PHA_QUAN']

ALL_BRIGHTNESS = ['MIEU', 'VUONG', 'DAC', 'BINH', 'HAM']

def run_query(query):
    result = subprocess.run(
        ['mysql', '-uroot', '-proot', 'lich_van_nien', '-N', '-e', query],
        capture_output=True, text=True, check=True
    )
    return set(line.strip() for line in result.stdout.strip().split('\n') if line.strip())

# Get existing fragments
query = """SELECT fragment_code FROM interpretation_fragments 
WHERE fragment_code NOT LIKE '%_CHU_THAN_%' 
  AND fragment_code NOT LIKE 'CHU_MENH_%' 
  AND fragment_code NOT LIKE 'BAN_MENH_%' 
  AND fragment_code NOT LIKE 'CUC_%' 
  AND fragment_code NOT LIKE 'THUAN_%' 
  AND fragment_code NOT LIKE 'NGHICH_%' 
  AND fragment_code NOT LIKE 'THAN_%' 
  AND fragment_code NOT LIKE 'NO_CHINH_TINH_%' 
  AND fragment_code NOT LIKE '%_DONG_CUNG%'"""

existing = run_query(query)

# Generate expected
expected = set()
for star in ALL_STARS:
    for palace in ALL_PALACES:
        for brightness in ALL_BRIGHTNESS:
            expected.add(f"{star}_{palace}_{brightness}")

# Find missing
missing = sorted(expected - existing)

print(f"Total expected: {len(expected)}")
print(f"Existing: {len(existing)}")
print(f"Missing: {len(missing)}")
print("\n" + "="*80)
print("MISSING FRAGMENTS:")
print("="*80)

# Group by star_palace
groups = {}
for frag in missing:
    parts = frag.rsplit('_', 2)  # Split from right: [star_palace, brightness]
    if len(parts) == 3:
        key = f"{parts[0]}_{parts[1]}"  # star_palace
        if key not in groups:
            groups[key] = []
        groups[key].append(frag)

for key in sorted(groups.keys()):
    frags = groups[key]
    print(f"\n{key}: {len(frags)} missing")
    for f in sorted(frags):
        print(f"  - {f}")

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Generate rules for remaining star fragments"""

STARS = ['THIEN_CO', 'THAI_AM', 'THAI_DUONG', 'VU_KHUC', 'THIEN_TUONG', 'THIEN_LUONG', 'LIEM_TRINH', 'CU_MON', 'THIEN_DONG']
PALACES = ['QUAN_LOC', 'TAI_BACH', 'PHU_THE', 'TAT_ACH', 'TU_TUC', 'PHUC_DUC', 'THIEN_DI', 'NO_BOC', 'DIEN_TRACH', 'PHU_MAU', 'HUYNH_DE']
BRIGHTNESS = ['MIEU', 'VUONG', 'DAC', 'BINH', 'HAM']

output = ["-- Rules for remaining star fragments\n"]
output.append("INSERT INTO interpretation_rules (fragment_code, conditions) VALUES\n")

rules = []
for star in STARS:
    for palace in PALACES:
        for bright in BRIGHTNESS:
            fragment_code = f"{star}_{palace}_{bright}"
            conditions = f'{{"type": "PALACE", "stars": ["{star}"], "palace": "{palace}", "brightness": ["{bright}"]}}'
            rules.append(f"('{fragment_code}', '{conditions}')")

output.append(',\n'.join(rules))
output.append(";")

with open('db/scripts/DETAILED_FRAGMENTS_REMAINING_STARS_RULES.sql', 'w', encoding='utf-8') as f:
    f.write('\n'.join(output))

print(f"Generated rules file with {len(rules)} rules")

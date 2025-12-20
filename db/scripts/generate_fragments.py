#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Generate interpretation fragments for remaining stars"""

STARS = {
    'THIEN_CO': {'name': 'Thiên Cơ', 'char': ['trí tuệ', 'linh hoạt', 'thông minh']},
    'THAI_AM': {'name': 'Thái Âm', 'char': ['nhân từ', 'hòa nhã', 'dịu dàng']},
    'THAI_DUONG': {'name': 'Thái Dương', 'char': ['nhiệt huyết', 'năng động', 'tích cực']},
    'VU_KHUC': {'name': 'Vũ Khúc', 'char': ['tài chính', 'kinh doanh', 'tổ chức']},
    'THIEN_TUONG': {'name': 'Thiên Tướng', 'char': ['ổn định', 'bảo vệ', 'trung thành']},
    'THIEN_LUONG': {'name': 'Thiên Lương', 'char': ['nhân từ', 'từ thiện', 'hòa nhã']},
    'LIEM_TRINH': {'name': 'Liêm Trinh', 'char': ['thanh cao', 'trung thực', 'chính trực']},
    'CU_MON': {'name': 'Cự Môn', 'char': ['giao tiếp', 'học hỏi', 'kiến thức']},
    'THIEN_DONG': {'name': 'Thiên Đồng', 'char': ['hài hòa', 'vui vẻ', 'lạc quan']}
}

PALACES = {
    'QUAN_LOC': {'name': 'Quan Lộc', 'focus': 'công việc, sự nghiệp, địa vị'},
    'TAI_BACH': {'name': 'Tài Bạch', 'focus': 'tiền bạc, tài chính, thu nhập'},
    'PHU_THE': {'name': 'Phu Thê', 'focus': 'hôn nhân, mối quan hệ vợ chồng'},
    'TAT_ACH': {'name': 'Tật Ách', 'focus': 'sức khỏe, thể trạng, bệnh tật'},
    'TU_TUC': {'name': 'Tử Tức', 'focus': 'con cái, giáo dục, tương lai'},
    'PHUC_DUC': {'name': 'Phúc Đức', 'focus': 'phúc đức, tâm linh, truyền thống'},
    'THIEN_DI': {'name': 'Thiên Di', 'focus': 'di chuyển, du lịch, thay đổi'},
    'NO_BOC': {'name': 'Nô Bộc', 'focus': 'bạn bè, đồng nghiệp, hợp tác'},
    'DIEN_TRACH': {'name': 'Điền Trạch', 'focus': 'nhà cửa, bất động sản, tài sản'},
    'PHU_MAU': {'name': 'Phụ Mẫu', 'focus': 'cha mẹ, học tập, giáo dục'},
    'HUYNH_DE': {'name': 'Huynh Đệ', 'focus': 'anh chị em, bạn bè, tương trợ'}
}

BRIGHTNESS = {
    'MIEU': {'name': 'miếu địa', 'tone': 'positive'},
    'VUONG': {'name': 'vượng địa', 'tone': 'positive'},
    'DAC': {'name': 'đắc địa', 'tone': 'positive'},
    'BINH': {'name': 'bình địa', 'tone': 'neutral'},
    'HAM': {'name': 'hãm địa', 'tone': 'negative'}
}

def gen_fragment(star_code, palace_code, bright_code):
    star = STARS[star_code]
    palace = PALACES[palace_code]
    bright = BRIGHTNESS[bright_code]
    
    if bright['tone'] == 'positive':
        content = f"{star['name']} {bright['name']} tại cung {palace['name']} thể hiện khả năng {star['char'][0]} và {star['char'][1]} trong lĩnh vực {palace['focus']}. Sao này chủ về {star['char'][0]}, khi ở {bright['name']} tại {palace['name']} tạo nên khả năng phát huy đầy đủ tính chất. Người có {star['name']} {bright['name']} tại {palace['name']} thường có khả năng {star['char'][2]}, dễ dàng đạt được thành tựu và phát triển. Tính cách {star['char'][0]} giúp họ vượt qua các thách thức và tạo dựng được vị thế. Trong thực tế, họ thường {palace['focus']} một cách hiệu quả, với khả năng {star['char'][1]} và {star['char'][2]}. {palace['name']} của họ có xu hướng phát triển tích cực, với nhiều cơ hội để thể hiện năng lực và đạt được mục tiêu thông qua {star['char'][0]}."
    elif bright['tone'] == 'neutral':
        content = f"{star['name']} {bright['name']} tại cung {palace['name']} thể hiện khả năng {palace['focus']} ở mức độ trung bình, không có sự nổi bật đặc biệt về {star['char'][0]}. {bright['name']} là trạng thái không có lợi cũng không có hại rõ rệt. Người có {star['name']} {bright['name']} tại {palace['name']} thường có khả năng ổn định, hoàn thành các nhiệm vụ cơ bản nhưng ít có thành tựu nổi bật. Trong thực tế, họ thể hiện tính cách trung hòa, có thể {palace['focus']} ở mức độ cơ bản nhưng khó đạt được thành tựu lớn. {palace['name']} của họ có xu hướng phát triển chậm, cần nhiều thời gian và nỗ lực để cải thiện. Họ cần chủ động phát triển {star['char'][0]} để nâng cao chất lượng."
    else:
        content = f"{star['name']} {bright['name']} tại cung {palace['name']} cho thấy khó khăn trong việc {palace['focus']}. {bright['name']} là trạng thái bất lợi, khiến {star['name']} không thể phát huy các mặt tích cực. Người có {star['name']} {bright['name']} tại {palace['name']} thường gặp khó khăn trong việc {star['char'][0]}, dễ bị rối loạn khi đối mặt với tình huống phức tạp. Họ có xu hướng thiếu tự tin, khó khăn trong việc đạt được mục tiêu và thường cần sự hỗ trợ. Trong thực tế, họ có thể gặp trở ngại, dễ bỏ lỡ cơ hội. {palace['name']} của họ có xu hướng phát triển chậm, cần nhiều nỗ lực để vượt qua khó khăn."
    
    return content.replace("'", "''")

output = ["-- Auto-generated fragments for remaining stars\n"]
for star_code in STARS.keys():
    output.append(f"-- {STARS[star_code]['name']}")
    for palace_code in PALACES.keys():
        output.append(f"-- {PALACES[palace_code]['name']}")
        output.append("INSERT INTO interpretation_fragments (fragment_code, content, tone, priority) VALUES")
        frags = []
        for bright_code in ['MIEU', 'VUONG', 'DAC', 'BINH', 'HAM']:
            code = f"{star_code}_{palace_code}_{bright_code}"
            content = gen_fragment(star_code, palace_code, bright_code)
            tone = BRIGHTNESS[bright_code]['tone']
            frags.append(f"('{code}', '{content}', '{tone}', 0)")
        output.append(',\n'.join(frags))
        output.append(";\n")

with open('db/scripts/DETAILED_FRAGMENTS_REMAINING_STARS.sql', 'w', encoding='utf-8') as f:
    f.write('\n'.join(output))

print("Generated SQL file")

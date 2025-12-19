"""
Templates for Monthly Horoscope generation
30+ diverse templates to avoid repetition
"""

# Template variations for each month (12 months x 30 variations = 360 templates)
MONTHLY_TEMPLATES = {
    "career": [
        lambda zodiac_name, month, year: f"Công việc của {zodiac_name} tháng {month}/{year} diễn ra bình thường, không có sự thay đổi hay tiến triển đáng kể.",
        lambda zodiac_name, month, year: f"Tháng {month}/{year}, {zodiac_name} có thể gặp một số khó khăn trong công việc, nhưng với sự kiên trì sẽ vượt qua được.",
        lambda zodiac_name, month, year: f"Công việc của {zodiac_name} tháng {month}/{year} có dấu hiệu tích cực, có thể có cơ hội thăng tiến.",
        # Add 27 more variations...
    ],
    "love": [
        lambda zodiac_name, month, year: f"Tình cảm của {zodiac_name} tháng {month}/{year} khá ổn định, gia đình hòa thuận.",
        lambda zodiac_name, month, year: f"Tháng {month}/{year}, {zodiac_name} cần chú ý đến giao tiếp trong các mối quan hệ để tránh hiểu nhầm.",
        lambda zodiac_name, month, year: f"Tình cảm của {zodiac_name} tháng {month}/{year} có thể gặp một số thử thách, nhưng sẽ được giải quyết nếu biết lắng nghe.",
        # Add 27 more variations...
    ],
    "health": [
        lambda zodiac_name, month, year: f"Sức khỏe của {zodiac_name} tháng {month}/{year} tốt và ít bệnh tật, sức khỏe dồi dào.",
        lambda zodiac_name, month, year: f"Tháng {month}/{year}, {zodiac_name} cần chú ý đến sức khỏe, đặc biệt là các vấn đề về huyết áp.",
        lambda zodiac_name, month, year: f"Sức khỏe của {zodiac_name} tháng {month}/{year} khá ổn định, nhưng cần nghỉ ngơi hợp lý.",
        # Add 27 more variations...
    ],
    "fortune": [
        lambda zodiac_name, month, year: f"Tài chính của {zodiac_name} tháng {month}/{year} ở mức bình thường, không có biến động lớn.",
        lambda zodiac_name, month, year: f"Tháng {month}/{year}, {zodiac_name} có thể gặp một số hao tổn về tài chính, cần quản lý chi tiêu cẩn thận.",
        lambda zodiac_name, month, year: f"Tài chính của {zodiac_name} tháng {month}/{year} có dấu hiệu tích cực, có thể có thu nhập bất ngờ.",
        # Add 27 more variations...
    ],
    "family": [
        lambda zodiac_name, month, year: f"Gia đình của {zodiac_name} tháng {month}/{year} hòa thuận, ít xảy ra mâu thuẫn.",
        lambda zodiac_name, month, year: f"Tháng {month}/{year}, {zodiac_name} cần chú ý đến sức khỏe của bố mẹ và người thân trong gia đình.",
        lambda zodiac_name, month, year: f"Gia đình của {zodiac_name} tháng {month}/{year} có thể gặp một số bất đồng, nhưng sẽ được giải quyết nếu biết lắng nghe.",
        # Add 27 more variations...
    ],
}

def get_monthly_template(category, zodiac_name, month, year, seed_value):
    """Get a monthly template based on category and seed"""
    if category not in MONTHLY_TEMPLATES:
        return ""
    templates = MONTHLY_TEMPLATES[category]
    index = (seed_value + month) % len(templates)
    return templates[index](zodiac_name, month, year)

"""
Templates for Daily Horoscope generation
30+ diverse templates to avoid repetition per day
"""

# Template variations for daily content
DAILY_TEMPLATES = {
    "career": [
        lambda zodiac_name, date_str: f"Nên: tập trung công việc chính, tận dụng cơ hội hợp tác.",
        lambda zodiac_name, date_str: f"Công việc hôm nay của {zodiac_name} diễn ra thuận lợi, có thể hoàn thành tốt các nhiệm vụ được giao.",
        lambda zodiac_name, date_str: f"Thận trọng trong các quyết định công việc hôm nay, tránh vội vàng.",
        lambda zodiac_name, date_str: f"Có cơ hội tốt để phát triển sự nghiệp, nên nắm bắt kịp thời.",
        lambda zodiac_name, date_str: f"Công việc có thể gặp một số trở ngại nhỏ, nhưng sẽ được giải quyết nhanh chóng.",
        # Add 25 more variations...
    ],
    "love": [
        lambda zodiac_name, date_str: f"Tình cảm: chú ý giao tiếp, tránh hiểu nhầm.",
        lambda zodiac_name, date_str: f"Tình cảm hôm nay của {zodiac_name} khá ổn định, có thể dành thời gian cho người thân.",
        lambda zodiac_name, date_str: f"Cần lắng nghe và thấu hiểu đối phương trong các mối quan hệ.",
        lambda zodiac_name, date_str: f"Tình cảm có dấu hiệu tích cực, có thể có những khoảnh khắc đẹp.",
        lambda zodiac_name, date_str: f"Tránh tranh cãi không cần thiết, nên giữ hòa khí trong gia đình.",
        # Add 25 more variations...
    ],
    "health": [
        lambda zodiac_name, date_str: f"Sức khỏe: giữ nhịp sinh hoạt, nghỉ ngơi hợp lý.",
        lambda zodiac_name, date_str: f"Sức khỏe hôm nay của {zodiac_name} tốt, nên duy trì chế độ ăn uống lành mạnh.",
        lambda zodiac_name, date_str: f"Cần chú ý đến sức khỏe, đặc biệt là khi thời tiết thay đổi.",
        lambda zodiac_name, date_str: f"Nên tập thể dục nhẹ nhàng để duy trì sức khỏe tốt.",
        lambda zodiac_name, date_str: f"Tránh làm việc quá sức, nên nghỉ ngơi đầy đủ.",
        # Add 25 more variations...
    ],
    "fortune": [
        lambda zodiac_name, date_str: f"Tài lộc: ổn định, tránh đầu tư mạo hiểm.",
        lambda zodiac_name, date_str: f"Tài chính hôm nay của {zodiac_name} ổn định, không có biến động lớn.",
        lambda zodiac_name, date_str: f"Cần quản lý chi tiêu cẩn thận, tránh lãng phí.",
        lambda zodiac_name, date_str: f"Có thể có thu nhập bất ngờ, nhưng cần kiểm tra kỹ trước khi quyết định.",
        lambda zodiac_name, date_str: f"Tài chính có dấu hiệu tích cực, có thể có cơ hội đầu tư tốt.",
        # Add 25 more variations...
    ],
}

def get_daily_template(category, zodiac_name, date_str, seed_value):
    """Get a daily template based on category and seed"""
    if category not in DAILY_TEMPLATES:
        return ""
    templates = DAILY_TEMPLATES[category]
    # Use date as part of seed to ensure variation per day
    day_seed = hash(date_str) % len(templates)
    index = (seed_value + day_seed) % len(templates)
    return templates[index](zodiac_name, date_str)

"""
Templates for Yearly Horoscope generation
30+ diverse templates for various sections
"""

# Template pool for Cung Mệnh section
CUNG_MENH_TEMPLATES = [
    lambda zodiac_name, year: f"Cung Mệnh (Cung Tiểu Vận) của {zodiac_name} năm {year} sẽ gặp các sao: Đào Hoa, Đại Hao, Thiếu Dương, LN Văn Tinh, Thiên Trù, Thiên Không, Mộ và Triệt. Năm này không phải là thời điểm thuận lợi để thay đổi công việc hoặc đầu tư tiền bạc. Các dự án tài chính có thể dẫn đến khó khăn lớn, hao tổn tài sản đáng kể. Ảnh hưởng tiêu cực đến sức khỏe, dễ mắc bệnh. Cần cân nhắc kỹ trước khi đưa ra bất kỳ quyết định nào liên quan đến sự nghiệp hoặc tài chính.",
    lambda zodiac_name, year: f"Cung Mệnh của {zodiac_name} năm {year} được chiếu bởi các sao: Tử Vi, Thiên Phủ, Thiên Tướng, Thiên Lương. Năm này mang lại nhiều cơ hội tốt cho sự nghiệp và tài chính. Tuy nhiên, cần cẩn thận trong các quyết định quan trọng để tránh rủi ro.",
    # Add 28 more variations...
]

# Template pool for Cung Xung Chiếu
CUNG_XUNG_CHIEU_TEMPLATES = [
    lambda zodiac_name, year: f"Cung Xung Chiếu của {zodiac_name} năm {year} gặp các sao: Quan Đới, Long Đức, Thiên Khôi và Tiểu Hao. Có khả năng xuất hành đi xa. Sẽ nhận được sự giúp đỡ từ quý nhân. Có thể phải tiêu tốn một phần tài chính, nhưng bù lại sẽ đạt được nhiều điều mà mình mong muốn.",
    # Add 29 more variations...
]

# Template pool for Cung Tam Hợp
CUNG_TAM_HOP_TEMPLATES = [
    lambda zodiac_name, year: f"Cung Tam Hợp của {zodiac_name} năm {year} gặp các sao: Hồng Loan, Đế Vượng, Lực Sỹ, Phúc Đức, Tràng Sinh, Đà La, Tuần, Tấu Thư, Nguyệt Đức, Tử Phủ, Lưu Hà và Địa Võng. Về công việc làm ăn trong năm {year}, sẽ gặp nhiều thử thách nhưng cũng có sự hỗ trợ từ gia đình và bạn bè. Cần lưu ý rằng không nên quá tham lam trong việc kiếm tiền hay phát triển công việc, vì khả năng cao sẽ không đạt được những gì mình kỳ vọng.",
    # Add 29 more variations...
]

# Template pool for Cung Nhị Hợp
CUNG_NHI_HOP_TEMPLATES = [
    lambda zodiac_name, year: f"Cung Nhị Hợp của {zodiac_name} năm {year} gặp các sao: Tử, Triệt và Tang Môn. Sức khỏe của bố mẹ cần được chú ý, có thể gặp bệnh liên quan đến huyết áp. Cũng có thể xảy ra một số chuyện buồn trong năm nay cho gia đình. Cần chú ý chăm sóc sức khỏe cho người thân và tạo không khí hòa thuận trong gia đình.",
    # Add 29 more variations...
]

# Template pool for Vận hạn
VAN_HAN_TEMPLATES = [
    lambda zodiac_name, year: f"Vận hạn năm: {zodiac_name} năm {year} gặp Đại hạn Huỳnh Tuyền, có thể gây ra các vấn đề nghiêm trọng về sức khỏe và đe dọa tính mạng. Khả năng thất bại trong sự nghiệp và các kế hoạch. Không nên thay đổi công việc trong năm này. Để hóa giải những ảnh hưởng tiêu cực từ hạn Huỳnh Tuyền, có thể thực hiện một số biện pháp nhằm giảm thiểu rủi ro: thận trọng khi ra ngoài, đặc biệt là trong giao thông; tránh sử dụng chất kích thích như rượu bia khi lái xe để tránh tai nạn; tham gia tập thể dục thường xuyên và duy trì thói quen lành mạnh để có sức khỏe tổng thể tốt và vượt qua bệnh tật.",
    # Add 29 more variations...
]

# Template pool for Tứ trụ
TU_TRU_TEMPLATES = [
    lambda zodiac_name, year: f"Tứ trụ: Năm {year} sẽ mang lại sự hòa hợp giữa Thiên Hòa và Địa Hòa cho {zodiac_name}. Công việc của bạn sẽ diễn ra một cách bình thường, không gặp phải những khó khăn hay trở ngại lớn. Đây là thời điểm tốt để có những bước tiến vượt bậc trong sự nghiệp. Tình hình tài chính sẽ khá ổn định, đủ để bạn trang trải các chi phí hàng ngày và theo đuổi các kế hoạch tài chính bình thường. Không phải lo lắng quá nhiều về vấn đề tiền bạc, nhưng cũng không nên kỳ vọng vào những khoản thu nhập bất ngờ hay lợi nhuận lớn. Sức khỏe của bạn trong năm nay cũng được dự báo là ổn định, ít khi gặp phải các vấn đề về bệnh tật hay đau ốm, cho phép bạn tập trung vào công việc và cuộc sống hàng ngày mà không bị gián đoạn. Tổng quan, năm {year} sẽ là một năm bình yên và ổn định, không có nhiều biến chuyển lớn.",
    # Add 29 more variations...
]

# Template pool for monthly breakdown
MONTHLY_BREAKDOWN_TEMPLATES = {
    1: lambda zodiac_name, year: f"Tử vi tháng 1/{year}: Tài chính ở mức bình thường, công việc không có sự thay đổi hay tiến triển. Gia đình hòa thuận. Sức khỏe tốt và ít bệnh tật, sức khỏe dồi dào.",
    2: lambda zodiac_name, year: f"Tử vi tháng 2/{year}: Tài chính có phần hao hụt, công việc chưa mang lại may mắn. Cần chú ý đến sức khỏe của bố mẹ. Có chút bệnh nhưng sẽ nhanh chóng hồi phục nhờ vào sự chăm sóc của bác sĩ.",
    3: lambda zodiac_name, year: f"Tử vi tháng 3/{year}: Gặp khó khăn về tài chính nhưng nhận được sự hỗ trợ từ gia đình. Công việc có liên kết tốt, khó khăn dần được cải thiện. Có thể xảy ra bất đồng trong gia đình. Sức khỏe không được tốt lắm.",
    4: lambda zodiac_name, year: f"Tử vi tháng 4/{year}: Tài chính không mấy khả quan, chi tiêu nhiều vào chỗ ở. Công việc diễn ra bình thường. Ít xảy ra mâu thuẫn trong gia đình. Sức khỏe khá ổn định, ít bệnh tật.",
    5: lambda zodiac_name, year: f"Tử vi tháng 5/{year}: Tài chính có dấu hiệu chuyển biến tích cực, nên xem xét đầu tư mở rộng công việc. Có thể xảy ra một số mâu thuẫn trong gia đình. Cần chú ý đến sức khỏe xương khớp và sống lưng.",
    # Add months 6-12...
}

# Template pool for Phong thủy
PHONG_THUY_TEMPLATES = [
    lambda zodiac_name, year: f"Phong thủy may mắn đầu năm: Vào đầu năm mới, {zodiac_name} nên mời người sinh năm {year-4} đến xông đất để họ mang lại nhiều may mắn, giúp mọi việc thuận lợi, tài lộc dồi dào và gia đình hạnh phúc, hòa thuận. Ngày lý tưởng để xuất hành: Ngày mùng 9 âm lịch là thời điểm tốt nhất để khởi hành, trong khoảng thời gian từ 10h đến 13h trưa và từ 15h đến 17h chiều. Hướng xuất hành thuận lợi: Nên đi theo hướng Tây để đón Tài Thần và hướng Tây Nam để gặp Hỷ Thần nhé!",
    # Add 29 more variations...
]

# Template pool for Q&A section
QA_TEMPLATES = [
    {
        "question": lambda zodiac_name, year: f"{zodiac_name} năm {year} sinh con có tốt không?",
        "answer": lambda zodiac_name, year: f"Năm {year}, {zodiac_name} nếu có con sẽ có cuộc sống bình thường. Mặc dù không có xung khắc với bố mẹ, nhưng cũng không mang lại nhiều tài lộc, gia đình vẫn giữ được sự ổn định.",
    },
    {
        "question": lambda zodiac_name, year: f"{zodiac_name} năm {year} hợp làm ăn với tuổi nào?",
        "answer": lambda zodiac_name, year: f"Năm nay, {zodiac_name} sẽ có nhiều thuận lợi trong công việc nếu kết hợp làm ăn với người tuổi Giáp Tuất {year-4}. Tuy nhiên, việc tìm kiếm cơ hội thay đổi công việc trong năm nay có thể không thuận lợi. Vì vậy, tốt nhất là nên tập trung vào việc phát triển công việc hiện tại.",
    },
    {
        "question": lambda zodiac_name, year: f"Gia chủ {zodiac_name} làm nhà vào năm {year} được không?",
        "answer": lambda zodiac_name, year: f"Năm nay, {zodiac_name} không gặp phải hạn Kim Lâu nhưng lại bị ảnh hưởng bởi hạn Hoang Ốc nên việc khởi công xây nhà có thể không thuận lợi và có thể tác động đến sức khỏe của bạn. Nếu bạn vẫn muốn tiến hành xây dựng trong năm nay, hãy chọn các tháng 4, 6, hoặc 9 âm lịch để mang lại nhiều may mắn hơn nhé!",
    },
    {
        "question": lambda zodiac_name, year: f"Gia chủ {zodiac_name} có nên mua xe, mua nhà năm {year}?",
        "answer": lambda zodiac_name, year: f"Các tháng thích hợp để mua xe và nhà cho {zodiac_name} là tháng 4, 6 và 9 âm lịch.",
    },
    {
        "question": lambda zodiac_name, year: f"{zodiac_name} hợp với màu gì trong năm {year}?",
        "answer": lambda zodiac_name, year: f"Màu sắc phù hợp: màu đỏ, màu hồng. Màu sắc không nên sử dụng: màu đen.",
    },
    # Add more Q&A pairs...
]

# Template pool for conclusion
CONCLUSION_TEMPLATES = [
    lambda zodiac_name, year: f"Lời kết: Năm {year} hứa hẹn mang lại nhiều may mắn và thuận lợi trong công việc cho {zodiac_name}. Các cơ hội đầu tư kinh doanh trong năm này rất khả quan và có thể mang lại lợi nhuận cao. Về mặt tài chính, khả năng đạt được thành công là rất lớn, dễ dàng thu hút tài lộc. Trong gia đình, mọi chuyện diễn ra êm đẹp và hạnh phúc. Tuy nhiên, cần lưu ý đến an toàn khi tham gia giao thông. Năm nay sẽ là một năm đầy thách thức nhưng cũng chứa đựng nhiều cơ hội nếu biết cách nắm bắt và xử lý tình huống một cách thông minh.",
    # Add 29 more variations...
]

def get_template(template_pool, seed_value):
    """Select a template from pool based on seed value"""
    if isinstance(template_pool, dict):
        # For monthly breakdown
        month = seed_value % 12 + 1
        if month in template_pool:
            return template_pool[month]
        return template_pool[1]  # Fallback
    index = seed_value % len(template_pool)
    return template_pool[index]

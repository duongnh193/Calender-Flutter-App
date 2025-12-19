"""
Templates for Lifetime Horoscope generation
30+ diverse templates to avoid repetition
"""

# Template pool for overview section (30 variations)
OVERVIEW_TEMPLATES = [
    lambda can_chi, gender: f"{can_chi} là số rất cao, số có kẻ đón người đưa rộn ràng. Thuận sinh tiếp đãi dạ thưa, hiển vinh một cách có thừa chẳng sai. Mùa Xuân lỗi số đắng cay, mồ hôi nước mắt chảy ngay ròng ròng. Anh em xung khắc chẳng xong, nói ra gây dữ, tự nhiên chẳng hòa.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách độc lập, mạnh mẽ. Cuộc đời họ trải qua nhiều thăng trầm nhưng luôn giữ được tinh thần kiên định. Về công danh, họ có thể đạt được thành tựu lớn nếu biết nắm bắt cơ hội đúng lúc.",
    lambda can_chi, gender: f"Vận mệnh của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được đánh giá là khá tốt. Họ có khả năng thích ứng cao với mọi hoàn cảnh, dù gặp khó khăn cũng không dễ dàng từ bỏ. Sự nghiệp phát triển ổn định, tài lộc dồi dào vào những năm trung niên.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt, cuộc đời nhiều biến động nhưng cũng nhiều cơ hội. Họ là người có tầm nhìn xa, biết cách lập kế hoạch cho tương lai. Tuy nhiên, cần cẩn thận trong các quyết định quan trọng để tránh rủi ro.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng thường có cuộc sống khá sung túc. Họ giỏi trong việc quản lý tài chính và biết cách đầu tư thông minh. Về tình cảm, họ là người chung thủy và luôn quan tâm đến gia đình.",
    lambda can_chi, gender: f"Vận số của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được xem là trung bình khá. Họ có thể đạt được thành công trong sự nghiệp nếu biết phấn đấu và nỗ lực. Tuy nhiên, cần lưu ý đến sức khỏe, đặc biệt là trong những năm tuổi trung niên.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách hòa đồng, dễ gần. Họ có khả năng giao tiếp tốt và thường được nhiều người yêu mến. Cuộc sống của họ khá ổn định, ít có biến động lớn.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt, cuộc đời nhiều thử thách nhưng cũng nhiều thành tựu. Họ là người có ý chí mạnh mẽ, không dễ dàng từ bỏ mục tiêu. Sự nghiệp phát triển tốt vào những năm sau 30 tuổi.",
    lambda can_chi, gender: f"Vận mệnh của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được đánh giá là tốt. Họ có khả năng học hỏi nhanh và thích ứng tốt với môi trường mới. Về tài chính, họ biết cách quản lý và tích lũy tài sản một cách hiệu quả.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách độc lập và quyết đoán. Họ không thích phụ thuộc vào người khác và luôn muốn tự mình quyết định cuộc sống. Tuy nhiên, đôi khi sự độc lập này có thể khiến họ cảm thấy cô đơn.",
    # Add 20 more variations...
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh khá đặc biệt. Cuộc đời họ trải qua nhiều giai đoạn khác nhau, mỗi giai đoạn đều có những thử thách và cơ hội riêng. Họ cần học cách thích ứng và nắm bắt cơ hội để đạt được thành công.",
    lambda can_chi, gender: f"Vận số của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được xem là tốt về mặt tài lộc. Họ có khả năng kiếm tiền tốt và biết cách đầu tư thông minh. Tuy nhiên, cần cẩn thận trong việc quản lý chi tiêu để tránh lãng phí.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách ôn hòa, dễ mến. Họ thường được nhiều người yêu quý và hỗ trợ trong cuộc sống. Về sự nghiệp, họ có thể đạt được thành công nhờ vào sự giúp đỡ của những người xung quanh.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về tình cảm. Họ là người chung thủy và luôn quan tâm đến người thân. Tuy nhiên, đôi khi họ quá nhạy cảm và dễ bị tổn thương trong các mối quan hệ.",
    lambda can_chi, gender: f"Vận mệnh của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được đánh giá là khá tốt về mặt sức khỏe. Họ thường có sức khỏe tốt và ít bệnh tật. Tuy nhiên, cần lưu ý đến chế độ ăn uống và nghỉ ngơi để duy trì sức khỏe tốt.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách mạnh mẽ và quyết đoán. Họ không ngại đối mặt với thử thách và luôn tìm cách vượt qua khó khăn. Sự nghiệp của họ phát triển tốt nhờ vào sự kiên trì và nỗ lực không ngừng.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về gia đình. Họ là người rất quan tâm đến gia đình và luôn muốn tạo ra một môi trường hạnh phúc cho những người thân yêu. Gia đình là nguồn động lực lớn nhất của họ.",
    lambda can_chi, gender: f"Vận số của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được xem là tốt về mặt học vấn. Họ có khả năng học hỏi nhanh và tiếp thu kiến thức tốt. Với sự chăm chỉ và nỗ lực, họ có thể đạt được nhiều thành tựu trong học tập và sự nghiệp.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách sáng tạo và đổi mới. Họ không thích làm theo lối mòn và luôn tìm cách cải tiến. Sự sáng tạo này giúp họ đạt được nhiều thành công trong công việc và cuộc sống.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về tài chính. Họ có khả năng kiếm tiền tốt và biết cách quản lý tài sản. Tuy nhiên, cần cẩn thận trong các quyết định đầu tư để tránh rủi ro.",
    lambda can_chi, gender: f"Vận mệnh của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được đánh giá là khá tốt về mặt xã hội. Họ có khả năng giao tiếp tốt và thường được nhiều người yêu mến. Những mối quan hệ xã hội tốt sẽ giúp họ đạt được nhiều thành công trong cuộc sống.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách kiên nhẫn và bền bỉ. Họ không dễ dàng từ bỏ mục tiêu và luôn tìm cách vượt qua khó khăn. Sự kiên nhẫn này giúp họ đạt được nhiều thành tựu trong cuộc sống.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về sức khỏe. Họ thường có sức khỏe tốt và ít bệnh tật. Tuy nhiên, cần lưu ý đến việc tập thể dục và nghỉ ngơi để duy trì sức khỏe tốt.",
    lambda can_chi, gender: f"Vận số của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được xem là tốt về mặt tình cảm. Họ là người chung thủy và luôn quan tâm đến người thân. Những mối quan hệ tình cảm của họ thường bền chặt và hạnh phúc.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách hòa đồng và dễ gần. Họ thường được nhiều người yêu quý và hỗ trợ trong cuộc sống. Sự hòa đồng này giúp họ đạt được nhiều thành công trong công việc và cuộc sống.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về sự nghiệp. Họ có khả năng lãnh đạo tốt và biết cách quản lý công việc. Với sự chăm chỉ và nỗ lực, họ có thể đạt được nhiều thành tựu trong sự nghiệp.",
    lambda can_chi, gender: f"Vận mệnh của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được đánh giá là khá tốt về mặt tài lộc. Họ có khả năng kiếm tiền tốt và biết cách đầu tư thông minh. Tuy nhiên, cần cẩn thận trong việc quản lý chi tiêu để tránh lãng phí.",
    lambda can_chi, gender: f"{can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có tính cách độc lập và quyết đoán. Họ không thích phụ thuộc vào người khác và luôn muốn tự mình quyết định cuộc sống. Sự độc lập này giúp họ đạt được nhiều thành công trong cuộc sống.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có số mệnh đặc biệt về gia đình. Họ là người rất quan tâm đến gia đình và luôn muốn tạo ra một môi trường hạnh phúc cho những người thân yêu. Gia đình là nguồn động lực lớn nhất của họ.",
    lambda can_chi, gender: f"Vận số của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng được xem là tốt về mặt học vấn. Họ có khả năng học hỏi nhanh và tiếp thu kiến thức tốt. Với sự chăm chỉ và nỗ lực, họ có thể đạt được nhiều thành tựu trong học tập và sự nghiệp.",
]

# Template pool for career section (30 variations)
CAREER_TEMPLATES = [
    lambda can_chi, gender: f"Về công danh sự nghiệp, {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có thể đạt được thành tựu lớn nếu biết nắm bắt cơ hội đúng lúc. Họ có khả năng lãnh đạo tốt và biết cách quản lý công việc. Tuy nhiên, cần cẩn thận trong các quyết định quan trọng để tránh rủi ro.",
    lambda can_chi, gender: f"Sự nghiệp của {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng phát triển ổn định. Họ có thể đạt được thành công nhờ vào sự chăm chỉ và nỗ lực không ngừng. Vào những năm trung niên, sự nghiệp của họ sẽ có bước tiến vượt bậc.",
    lambda can_chi, gender: f"Người tuổi {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng có khả năng làm việc tốt trong các lĩnh vực liên quan đến kinh doanh và tài chính. Họ biết cách quản lý tài sản và đầu tư thông minh. Tuy nhiên, cần cẩn thận trong các quyết định đầu tư để tránh rủi ro.",
    # Add 27 more variations...
]

# Template pool for love section with month groups
LOVE_TEMPLATES_GROUP1 = [  # Months 5, 6, 9
    lambda can_chi, gender: f"Vấn đề nhân duyên của {'nam' if gender == 'male' else 'nữ'} mạng {can_chi} được chia thành ba trường hợp sau: Những người sinh vào các tháng: 5, 6 và 9 Âm lịch có thể sẽ phải trải qua thay đổi đến ba lần trong chuyện tình duyên mới đến được với bến bờ hạnh phúc của riêng mình.",
]

LOVE_TEMPLATES_GROUP2 = [  # Months 1, 2, 7, 10, 11, 12
    lambda can_chi, gender: f"Những ai sinh vào các tháng: 1, 2, 7, 10, 11 và 12 Âm lịch thì tình duyên sẽ phải trải qua hai lần thay đổi mới tìm được bến đỗ cuối cùng của cuộc đời mình.",
]

LOVE_TEMPLATES_GROUP3 = [  # Months 3, 4, 8
    lambda can_chi, gender: f"Những người sinh vào các tháng: 3, 4 và 8 Âm lịch có tình duyên khá thuận lợi, ít gặp trở ngại. Họ có thể tìm được người bạn đời phù hợp và có cuộc sống hôn nhân hạnh phúc.",
]

# Template pool for compatible ages
COMPATIBLE_AGES_TEMPLATES = [
    lambda can_chi: f"Tuổi hợp làm ăn: {can_chi} nên chọn các tuổi hợp như {can_chi}, Bính Tuất, Kỷ Sửu và Đinh Sửu để làm ăn chung, sẽ đạt được sự thịnh vượng, thuận tiện, may mắn và thành công ngày càng tăng.",
]

# Template pool for difficult years
DIFFICULT_YEARS_TEMPLATES = [
    lambda can_chi: f"Năm khó khăn nhất: {can_chi} cần thận trọng vào các năm 26, 29, 33 và 40 tuổi. Trong những năm này, có thể gặp nhiều khó khăn trong tình cảm và gia đình, cần cẩn thận trong công việc, nên trì hoãn các ý tưởng mới hoặc kế hoạch lớn sang các năm khác để có kết quả tốt hơn.",
]

# Template pool for yearly progression
YEARLY_PROGRESSION_TEMPLATES = [
    lambda can_chi: f"Diễn biến từng năm: Từ năm 26 tuổi đến năm 29 tuổi, {can_chi} cần cực kỳ thận trọng trong công việc vào tháng 7 năm 27 tuổi. Có thể gặp ốm đau, bệnh tật hoặc hao tổn tài chính vào tháng 5 năm 28 tuổi, nhưng tổng thể công việc và tài lộc sẽ thuận lợi. Từ tháng Giêng đến tháng 6 năm 29 tuổi, sẽ gặp nhiều khó khăn và đau buồn. Từ tháng 7 trở đi, mọi việc sẽ thuận lợi và gia đình hạnh phúc.",
]

# Template pool for incompatible ages
INCOMPATIBLE_AGES_TEMPLATES = [
    lambda can_chi, gender: f"Tuổi đại kỵ: {can_chi} {'nam' if gender == 'male' else 'nữ'} mạng không nên kết hôn hay cộng tác trong công việc với những tuổi xung khắc với mình như: Quý Mùi, Giáp Thân, Canh Dần, Ất Mùi, Bính Thân, Mậu Dần và Nhâm Thân.",
]

# Template pool for ritual guidance
RITUAL_GUIDANCE_TEMPLATES = [
    lambda can_chi: f"Hàng tháng, vào ngày 25 âm lịch, nên tiến hành lễ cúng dâng sao giải hạn từ khoảng 19h đến 21h. Trong buổi lễ này, sử dụng bài vị giấy màu xanh với dòng chữ 'Đông Phương Giáp Ất Mộc Đức Tinh Quân' và thắp sáng 20 ngọn đèn cầy. Khi thực hiện nghi lễ, hãy quay mặt về hướng Đông và khấn nguyện đến Thiên đình Thánh Vân cung Đại Thánh, Trùng Quan Triều Nguyên Mộc Đức tinh quân, nhằm cầu xin sự phù hộ và bảo vệ.",
]

def get_template(template_pool, seed_value):
    """Select a template from pool based on seed value"""
    index = seed_value % len(template_pool)
    return template_pool[index]

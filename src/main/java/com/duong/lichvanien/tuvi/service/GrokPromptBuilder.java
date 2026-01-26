package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to build prompts for Grok AI.
 * Creates structured prompts for Tu Vi interpretation generation.
 */
@Slf4j
@UtilityClass
public class GrokPromptBuilder {

    private static final String SYSTEM_PROMPT_FULL = """
        Bạn là một chuyên gia Tử Vi Đẩu Số Bắc Phái hàng đầu Việt Nam với hơn 30 năm kinh nghiệm.
        Bạn có kiến thức sâu rộng về:
        - 14 Chính tinh và ý nghĩa của chúng
        - 12 cung trong lá số Tử Vi
        - Các Phụ tinh, Bàng tinh và ảnh hưởng của chúng
        - Ngũ Hành tương sinh tương khắc
        - Độ sáng của sao (Miếu, Vượng, Đắc, Bình, Hãm)
        - Đại vận, Tiểu vận và Lưu niên
        - Tuần, Triệt và ảnh hưởng của chúng
        
        Nhiệm vụ của bạn là phân tích lá số Tử Vi và đưa ra giải luận chi tiết, chính xác.
        
        **QUAN TRỌNG - GIỌNG VĂN VÀ PHONG CÁCH:**
        - Sử dụng giọng văn mềm mại, thân thiện, như đang tư vấn trực tiếp: "Dựa trên lá số Tử Vi của bạn", "Lá số bạn...", "Bạn có thể...", "Bạn nên..."
        - LUÔN dùng đại từ "bạn" khi nói về người xem, tránh dùng "người này", "chủ mệnh", "người sinh năm..."
        - Luôn nhấn mạnh mặt tích cực trước, sau đó mới đề cập thách thức một cách xây dựng
        - Giải thích rõ ràng, HẠN CHẾ TỐI ĐA thuật ngữ chuyên sâu. Nếu bắt buộc phải dùng thuật ngữ, PHẢI giải thích ngay sau đó bằng ngôn ngữ dễ hiểu
        - Ví dụ: Thay vì "Vô chính diệu" → dùng "Cung này không có sao chính tinh nào, nhưng..."
        - Ví dụ: Thay vì "Hóa Lộc" → dùng "Sao này được hóa thành Lộc (tài lộc, may mắn)..."
        - Ví dụ: Thay vì "Tam hợp" → dùng "Ba cung hợp nhau (tam hợp)..."
        - Độ dài: Mỗi phần trong overview tối thiểu 5-7 câu, mỗi cung tối thiểu 500-700 từ
        - Luôn kết thúc mỗi phần với lời khuyên tích cực, thực tế
        - Tránh dự đoán quá cụ thể về tương lai, thay vào đó đưa ra xu hướng và hướng dẫn ứng phó
        - Sử dụng ngôn ngữ tích cực: "có thể", "nên", "phù hợp", "cơ hội" thay vì "sẽ", "chắc chắn", "phải"
        
        **CẤU TRÚC GIẢI LUẬN:**
        - Mỗi cung: Tóm tắt ngắn → Giải thích ý nghĩa → Phân tích chi tiết → Lời khuyên tích cực
        - Luôn kết thúc với điểm tích cực hoặc cách hóa giải nếu có thách thức
        """;

    private static final String SYSTEM_PROMPT_CYCLES = """
        Bạn là một chuyên gia Tử Vi Đẩu Số Bắc Phái hàng đầu Việt Nam với hơn 30 năm kinh nghiệm.
        Bạn chuyên về phân tích Đại vận và Tiểu vận trong Tử Vi.
        
        Kiến thức chuyên môn:
        - Đại vận: chu kỳ 10 năm, mỗi cung ảnh hưởng một giai đoạn, phản ánh xu hướng dài hạn
        - Tiểu vận (Lưu niên): chu kỳ hàng năm, biến động ngắn hạn trong năm
        - Thuận/Nghịch: hướng di chuyển của Đại vận (thuận chiều kim đồng hồ hoặc ngược lại)
        - Ảnh hưởng của các sao trong từng cung theo thời gian
        - Tuần/Triệt và tác động trong từng giai đoạn
        - Tứ Hóa lưu niên (Hóa Lộc, Hóa Quyền, Hóa Khoa, Hóa Kỵ) thay đổi theo năm
        - Sao chiếu mệnh và các hạn (Thiên La, Địa Võng, Tam Tai, Kim Lâu, Hoang Ốc)
        
        **QUAN TRỌNG - GIỌNG VĂN VÀ PHONG CÁCH:**
        - Sử dụng giọng văn mềm mại, thân thiện: "Dựa trên lá số Tử Vi của bạn", "Lá số bạn...", "Bạn đang ở..."
        - Luôn nhấn mạnh mặt tích cực và cơ hội trước, sau đó mới đề cập thách thức một cách xây dựng
        - Giải thích rõ ràng, tránh thuật ngữ quá chuyên sâu. Ví dụ: "Đại vận phản ánh xu hướng dài hạn, tiểu vận là biến động ngắn hạn"
        - Nội dung đủ ý nhưng không lan man. Mỗi đại vận: 4-6 câu, mỗi tiểu vận: 3-5 câu
        - Luôn kết thúc với lời khuyên tích cực, thực tế, có thể áp dụng ngay
        - Với tiểu vận: Phân tích theo tháng, đưa ra lời khuyên cụ thể cho từng tháng
        - Có phần hóa giải nếu có sao xấu hoặc hạn, nhưng luôn tích cực: "Có thể hóa giải bằng cách..."
        
        **CẤU TRÚC GIẢI LUẬN:**
        - Đại vận: Tóm tắt → Phân tích cơ hội/thách thức → Chủ đề chính → Lời khuyên
        - Tiểu vận: Tổng quan → Phân tích theo lĩnh vực (Sự nghiệp, Tài lộc, Tình duyên, Sức khỏe) → Vận trình theo tháng → Cách hóa giải
        - Luôn kết thúc với điểm tích cực: "Tổng: Bình an, nền tảng tốt" hoặc "Tổng quát tốt, đạt đỉnh nếu kiên trì"
        """;

    /**
     * Build system prompt for full interpretation.
     */
    public static String buildFullSystemPrompt() {
        return SYSTEM_PROMPT_FULL;
    }

    /**
     * Build system prompt for cycle interpretation.
     */
    public static String buildCycleSystemPrompt() {
        return SYSTEM_PROMPT_CYCLES;
    }

    /**
     * Build user prompt for full interpretation.
     */
    public static String buildFullInterpretationPrompt(TuViChartResponse chart, String name, String gender) {
        CenterInfo center = chart.getCenter();
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("Hãy phân tích chi tiết lá số Tử Vi sau:\n\n");
        
        // Basic info
        prompt.append("## THÔNG TIN CƠ BẢN\n");
        prompt.append(String.format("- Tên: %s\n", name != null ? name : "Không rõ"));
        prompt.append(String.format("- Giới tính: %s\n", "male".equals(gender) ? "Nam" : "Nữ"));
        prompt.append(String.format("- Ngày sinh dương lịch: %s\n", center.getSolarDate()));
        prompt.append(String.format("- Giờ sinh: %s (%s)\n", center.getBirthHour() + ":" + center.getBirthMinute(), center.getBirthHourCanChi()));
        prompt.append(String.format("- Năm âm lịch: %s (%d)\n", center.getLunarYearCanChi(), center.getLunarYear()));
        prompt.append(String.format("- Tháng âm lịch: %d (%s)\n", center.getLunarMonth(), center.getLunarMonthCanChi()));
        prompt.append(String.format("- Ngày âm lịch: %d (%s)\n", center.getLunarDay(), center.getLunarDayCanChi()));
        prompt.append("\n");
        
        // Destiny info
        prompt.append("## BẢN MỆNH VÀ CỤC\n");
        prompt.append(String.format("- Bản mệnh (Nạp Âm): %s (%s)\n", center.getBanMenh(), center.getBanMenhNguHanh()));
        prompt.append(String.format("- Cục: %s (giá trị: %d, ngũ hành: %s)\n", center.getCuc(), center.getCucValue(), center.getCucNguHanh()));
        prompt.append(String.format("- Quan hệ Mệnh-Cục: %s\n", center.getMenhCucRelation()));
        prompt.append(String.format("- Âm/Dương: %s\n", center.getAmDuong()));
        prompt.append(String.format("- Thuận/Nghịch: %s\n", center.getThuanNghich()));
        prompt.append(String.format("- Chủ mệnh: %s\n", center.getChuMenh()));
        prompt.append(String.format("- Chủ thân: %s\n", center.getChuThan()));
        prompt.append(String.format("- Thân cư: %s\n", center.getThanCu()));
        prompt.append("\n");
        
        // Tuần/Triệt
        MarkerInfo markers = chart.getMarkers();
        if (markers != null) {
            prompt.append("## TUẦN TRIỆT\n");
            prompt.append(String.format("- Tuần: %s - %s\n", markers.getTuanStart(), markers.getTuanEnd()));
            prompt.append(String.format("- Triệt: %s - %s\n", markers.getTrietStart(), markers.getTrietEnd()));
            prompt.append("\n");
        }
        
        // Palaces
        prompt.append("## 12 CUNG\n");
        for (PalaceInfo palace : chart.getPalaces()) {
            prompt.append(String.format("\n### %s (%s)\n", palace.getName(), palace.getDiaChiCode()));
            prompt.append(String.format("- Can Chi: %s\n", palace.getCanChiPrefix()));
            prompt.append(String.format("- Trường Sinh: %s\n", palace.getTruongSinhStage()));
            
            if (palace.isHasTuan()) prompt.append("- Có TUẦN\n");
            if (palace.isHasTriet()) prompt.append("- Có TRIỆT\n");
            if (palace.isThanCu()) prompt.append("- THÂN CƯ tại đây\n");
            
            if (palace.getStars() != null && !palace.getStars().isEmpty()) {
                prompt.append("- Các sao: ");
                String stars = palace.getStars().stream()
                        .map(s -> String.format("%s(%s-%s)", s.getName(), s.getType(), s.getBrightness()))
                        .collect(Collectors.joining(", "));
                prompt.append(stars);
                prompt.append("\n");
            }
        }
        
        prompt.append("\n## YÊU CẦU\n");
        prompt.append("""
            Hãy đưa ra giải luận đầy đủ với giọng văn mềm mại, tích cực, dễ hiểu:
            
            1. **TỔNG QUAN** (overview):
               - Giới thiệu tổng quan về lá số (bắt đầu bằng "Dựa trên lá số Tử Vi của bạn...")
               - Phân tích Bản mệnh và Cục: Giải thích ý nghĩa, ảnh hưởng, luôn nhấn mạnh mặt tích cực
               - Phân tích Chủ mệnh và Chủ thân: Vai trò và ảnh hưởng trong cuộc đời
               - Phân tích Thân cư: Ý nghĩa và tác động
               - Phân tích Thuận/Nghịch: Giải thích rõ ràng, tránh thuật ngữ quá chuyên sâu
               - Tổng kết chung: Nhấn mạnh điểm mạnh, đưa ra hướng phát triển tích cực
            
            2. **PHÂN TÍCH 12 CUNG**: Với mỗi cung, đưa ra:
               - Tóm tắt ngắn (3-4 câu): Nêu đặc điểm chính, ưu tiên mặt tích cực
               - Giới thiệu ý nghĩa cung: Giải thích dễ hiểu, KHÔNG dùng thuật ngữ. Nếu phải dùng, giải thích ngay
               - Phân tích chi tiết dựa trên các sao (TỐI THIỂU 500-700 TỪ cho mỗi cung): 
                 * Phân tích từng sao chính, giải thích ảnh hưởng một cách dễ hiểu, dùng "bạn"
                 * Nhấn mạnh cơ hội và thuận lợi trước (3-4 câu)
                 * Sau đó đề cập thách thức một cách xây dựng, kèm cách hóa giải (2-3 câu)
                 * Giải thích cách các sao tương tác với nhau (2-3 câu)
                 * Đưa ra ví dụ cụ thể, thực tế (1-2 câu)
               - Ảnh hưởng của Tuần/Triệt (nếu có): Giải thích rõ bằng ngôn ngữ dễ hiểu, đưa ra cách ứng phó
               - Lời khuyên: Cụ thể, thực tế, có thể áp dụng ngay, luôn tích cực (2-3 câu)
               - Kết luận: Tóm tắt điểm chính, nhấn mạnh mặt tích cực (2-3 câu)
            
            **LƯU Ý QUAN TRỌNG:**
            - Giọng văn: Mềm mại, thân thiện, như đang tư vấn trực tiếp
            - Ngôn ngữ: Tích cực, tránh từ ngữ tiêu cực mạnh. Dùng "cần chú ý", "nên tránh" thay vì "tuyệt đối không", "rất xấu"
            - Độ dài: Mỗi phần 3-5 câu, đủ ý nhưng không lan man
            - Thuật ngữ: Nếu phải dùng, giải thích ngắn gọn trong ngoặc đơn
            - Luôn kết thúc với lời khuyên tích cực hoặc cách hóa giải
            
            Trả về dạng JSON với cấu trúc:
            {
              "overview": {
                "introduction": "...",
                "banMenhInterpretation": "...",
                "cucInterpretation": "...",
                "chuMenhInterpretation": "...",
                "chuThanInterpretation": "...",
                "thanCuInterpretation": "...",
                "thuanNghichInterpretation": "...",
                "overallSummary": "..."
              },
              "palaces": [
                {
                  "palaceCode": "MENH",
                  "summary": "...",
                  "introduction": "...",
                  "detailedAnalysis": "...",
                  "tuanTrietEffect": "...",
                  "adviceSection": "...",
                  "conclusion": "..."
                },
                ...
              ]
            }
            """);
        
        return prompt.toString();
    }

    /**
     * Build user prompt for cycle interpretation.
     */
    public static String buildCycleInterpretationPrompt(TuViChartResponse chart, String name, String gender) {
        CenterInfo center = chart.getCenter();
        CycleInfo cycles = chart.getCycles();
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("Hãy phân tích chi tiết Đại vận và Tiểu vận trong lá số Tử Vi sau:\n\n");
        
        // Basic info
        prompt.append("## THÔNG TIN CƠ BẢN\n");
        prompt.append(String.format("- Tên: %s\n", name != null ? name : "Không rõ"));
        prompt.append(String.format("- Giới tính: %s\n", "male".equals(gender) ? "Nam" : "Nữ"));
        prompt.append(String.format("- Năm âm lịch: %s\n", center.getLunarYearCanChi()));
        prompt.append(String.format("- Bản mệnh: %s (%s)\n", center.getBanMenh(), center.getBanMenhNguHanh()));
        prompt.append(String.format("- Cục: %s (giá trị: %d)\n", center.getCuc(), center.getCucValue()));
        prompt.append(String.format("- Chủ mệnh: %s\n", center.getChuMenh()));
        prompt.append("\n");
        
        // Cycle info
        prompt.append("## THÔNG TIN ĐẠI VẬN\n");
        prompt.append(String.format("- Hướng: %s (%s)\n", cycles.getDirection(), cycles.getDirectionText()));
        prompt.append(String.format("- Tuổi khởi đầu: %d\n", cycles.getDaiVanStartAge()));
        prompt.append(String.format("- Chu kỳ: %d năm\n", cycles.getCyclePeriod()));
        prompt.append("\n");
        
        // Đại vận list (limited to 8 cycles for realistic lifespan)
        prompt.append("## DANH SÁCH ĐẠI VẬN\n");
        prompt.append("Lưu ý: Chỉ phân tích 8 đại vận đầu tiên (từ tuổi thơ đến khoảng 80-85 tuổi), " +
                      "đây là phạm vi tuổi thọ thực tế của con người. Không gen đại vận đến 100+ tuổi vì không thực tế.\n");
        List<CycleInfo.DaiVanEntry> daiVanList = cycles.getDaiVanList();
        if (daiVanList != null) {
            // Limit to first 8 cycles only
            int maxCycles = Math.min(8, daiVanList.size());
            for (int i = 0; i < maxCycles; i++) {
                CycleInfo.DaiVanEntry entry = daiVanList.get(i);
                String ageRange = entry.getEndAge() >= 80 
                    ? String.format("%d+", entry.getStartAge()) 
                    : String.format("%d-%d", entry.getStartAge(), entry.getEndAge());
                prompt.append(String.format("- Tuổi %s: Cung %s\n", 
                        ageRange, entry.getPalaceName()));
            }
        }
        prompt.append("\n");
        
        // Palace info for reference
        prompt.append("## THÔNG TIN CÁC CUNG (để tham khảo)\n");
        for (PalaceInfo palace : chart.getPalaces()) {
            prompt.append(String.format("### %s (%s)\n", palace.getName(), palace.getDiaChiCode()));
            if (palace.isHasTuan()) prompt.append("  - Có TUẦN\n");
            if (palace.isHasTriet()) prompt.append("  - Có TRIỆT\n");
            if (palace.getStars() != null && !palace.getStars().isEmpty()) {
                String mainStars = palace.getStars().stream()
                        .filter(s -> "CHINH_TINH".equals(s.getType()))
                        .map(StarInfo::getName)
                        .collect(Collectors.joining(", "));
                if (!mainStars.isEmpty()) {
                    prompt.append(String.format("  - Chính tinh: %s\n", mainStars));
                }
            }
        }
        
        int currentYear = Year.now().getValue();
        
        prompt.append("\n## YÊU CẦU\n");
        prompt.append("Hãy phân tích chi tiết Đại vận và Tiểu vận với giọng văn mềm mại, tích cực, dễ hiểu.\n\n");
        prompt.append("**PHẦN 1: ĐẠI VẬN (Các giai đoạn 10 năm)**\n");
        prompt.append("QUAN TRỌNG: Chỉ phân tích 8 đại vận đầu tiên (từ tuổi thơ đến khoảng 80-85 tuổi). \n");
        prompt.append("Đây là phạm vi tuổi thọ thực tế của con người (80-100 tuổi). \n");
        prompt.append("KHÔNG được gen đại vận đến 100+ tuổi vì không thực tế và làm người đọc mất tin tưởng.\n\n");
        prompt.append("Với mỗi Đại vận (chỉ 8 đại vận đầu tiên), đưa ra:\n\n");
        prompt.append("1. **Tóm tắt**: 3-4 câu về đặc điểm chính, nhấn mạnh mặt tích cực trước\n");
        prompt.append("2. **Phân tích chi tiết** (TỐI THIỂU 10 CÂU, khoảng 150-200 từ): \n");
        prompt.append("   - Ảnh hưởng của cung và các sao trong cung (giải thích dễ hiểu, dùng \"bạn\", hạn chế thuật ngữ) - 3-4 câu\n");
        prompt.append("   - Cơ hội và thuận lợi (ưu tiên nêu trước, cụ thể, thực tế) - 2-3 câu\n");
        prompt.append("   - Thách thức và khó khăn (đề cập một cách xây dựng, kèm cách hóa giải cụ thể) - 2-3 câu\n");
        prompt.append("   - Ảnh hưởng của Tuần/Triệt (nếu có, giải thích dễ hiểu) - 1-2 câu\n");
        prompt.append("   - Kết thúc với tổng kết tích cực: \"Tổng: Bình an, nền tảng tốt\" hoặc tương tự - 1 câu\n");
        prompt.append("3. **Chủ đề chính**: Các lĩnh vực nổi bật trong giai đoạn (sự nghiệp, tài lộc, tình duyên, sức khỏe) - 2-3 câu\n");
        prompt.append("4. **Lời khuyên**: Hướng dẫn cụ thể, thực tế, có thể áp dụng ngay, luôn tích cực - 2-3 câu\n\n");
        prompt.append("Đại vận cuối cùng (thứ 8) có thể kết thúc ở tuổi 74+ hoặc 84+ tùy theo startAge, \n");
        prompt.append("đây là tuổi thọ hợp lý và thực tế.\n\n");
        prompt.append("**PHẦN 2: TIỂU VẬN (Lưu niên) - Năm hiện tại**\n");
        prompt.append(String.format("Phân tích tiểu vận cho năm hiện tại (năm %d dương lịch), bao gồm:\n\n", currentYear));
        prompt.append("1. **Tổng quan tiểu vận**: \n");
        prompt.append("   - Vị trí lưu niên tại cung nào, trong đại vận nào\n");
        prompt.append("   - Các sao chính và ảnh hưởng\n");
        prompt.append("   - Tứ Hóa lưu niên (Hóa Lộc, Hóa Quyền, Hóa Khoa, Hóa Kỵ) và tác động\n");
        prompt.append("   - Sao chiếu mệnh và các hạn (nếu có)\n");
        prompt.append("   - Tổng thể: Nhấn mạnh mặt tích cực, đưa ra hướng phát triển\n\n");
        prompt.append("2. **Phân tích theo lĩnh vực**:\n");
        prompt.append("   - **Sự nghiệp và Quan Lộc**: Cơ hội, thách thức, thời điểm tốt\n");
        prompt.append("   - **Tài lộc và Tài Bạch**: Xu hướng tài chính, thời điểm may mắn, lưu ý chi tiêu\n");
        prompt.append("   - **Tình duyên và Phu Thê**: Vận trình tình cảm, cơ hội, lưu ý\n");
        prompt.append("   - **Sức khỏe và Tật Ách**: Điểm cần chú ý, cách giữ gìn\n\n");
        prompt.append("3. **Vận trình theo tháng** (dương lịch):\n");
        prompt.append("   - Liệt kê từng tháng (1-12) với:\n");
        prompt.append("     * Vận trình chính của tháng\n");
        prompt.append("     * Lời khuyên cụ thể cho tháng đó\n");
        prompt.append("   - Format: \"Tháng X: [Vận trình] - [Lời khuyên]\"\n\n");
        prompt.append("4. **Cách hóa giải và lời khuyên**:\n");
        prompt.append("   - Nếu có sao xấu hoặc hạn: Đưa ra cách hóa giải cụ thể, thực tế\n");
        prompt.append("   - Lời khuyên chung: Tích cực, có thể áp dụng, nhấn mạnh tu tâm, làm thiện\n\n");
        prompt.append("**LƯU Ý QUAN TRỌNG:**\n");
        prompt.append("- Giọng văn: Mềm mại, thân thiện, như đang tư vấn: \"Dựa trên lá số Tử Vi của bạn...\", \"Lá số bạn...\", \"Bạn đang ở...\"\n");
        prompt.append("- Ngôn ngữ: Tích cực, tránh từ ngữ tiêu cực mạnh. Dùng \"cần chú ý\", \"nên tránh\" thay vì \"tuyệt đối không\", \"rất xấu\"\n");
        prompt.append("- Độ dài: Mỗi đại vận 4-6 câu, tiểu vận mỗi phần 3-5 câu, đủ ý nhưng không lan man\n");
        prompt.append("- Thuật ngữ: Nếu phải dùng, giải thích ngắn gọn. Ví dụ: \"Đại vận phản ánh xu hướng dài hạn, tiểu vận là biến động ngắn hạn\"\n");
        prompt.append("- Luôn kết thúc với lời khuyên tích cực hoặc cách hóa giải\n\n");
        prompt.append("Trả về dạng JSON với cấu trúc:\n");
        prompt.append("{\n");
        prompt.append("  \"introduction\": \"Giới thiệu về hệ thống Đại vận trong lá số này, bắt đầu bằng 'Dựa trên lá số Tử Vi của bạn...'\",\n");
        prompt.append("  \"overallCycleSummary\": \"Tổng quan về vận mệnh qua các giai đoạn, nhấn mạnh mặt tích cực...\",\n");
        prompt.append("  \"daiVanInterpretations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"startAge\": 5,\n");
        prompt.append("      \"endAge\": 14,\n");
        prompt.append("      \"palaceCode\": \"MENH\",\n");
        prompt.append("      \"palaceName\": \"Mệnh\",\n");
        prompt.append("      \"summary\": \"Tóm tắt 3-4 câu, nhấn mạnh mặt tích cực\",\n");
        prompt.append("      \"interpretation\": \"Phân tích chi tiết TỐI THIỂU 10 CÂU (khoảng 150-200 từ), dùng 'bạn', hạn chế thuật ngữ, kết thúc với 'Tổng: Bình an, nền tảng tốt' hoặc tương tự\",\n");
        prompt.append("      \"keyThemes\": \"Các lĩnh vực nổi bật: sự nghiệp, tài lộc, tình duyên, sức khỏe\",\n");
        prompt.append("      \"advice\": \"Lời khuyên cụ thể, thực tế, tích cực\"\n");
        prompt.append("    },\n");
        prompt.append("    ... (chỉ 8 đại vận đầu tiên, không gen đến 100+ tuổi)\n");
        prompt.append("  ],\n");
        prompt.append("  \"tieuVanInterpretations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"age\": 24,\n");
        prompt.append(String.format("      \"year\": %d,\n", currentYear));
        prompt.append("      \"palaceCode\": \"PHU_MAU\",\n");
        prompt.append("      \"palaceName\": \"Phụ Mẫu\",\n");
        prompt.append("      \"palaceChi\": \"Tý\",\n");
        prompt.append(String.format("      \"summary\": \"Tổng quan tiểu vận năm %d, nhấn mạnh mặt tích cực\",\n", currentYear));
        prompt.append("      \"interpretation\": \"Phân tích chi tiết theo lĩnh vực: Sự nghiệp, Tài lộc, Tình duyên, Sức khỏe. Vận trình theo tháng (1-12). Cách hóa giải và lời khuyên.\",\n");
        prompt.append("      \"keyEvents\": \"Các sự kiện hoặc chủ đề chính trong năm\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"generalAdvice\": \"Lời khuyên chung cho việc ứng dụng Đại vận, tích cực, thực tế...\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    /**
     * Extract JSON from AI response (handles markdown code blocks).
     */
    public static String extractJsonFromResponse(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }
        
        // Remove markdown code blocks if present
        String cleaned = response.trim();
        
        // Handle ```json ... ``` format
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        return cleaned.trim();
    }
}


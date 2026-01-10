package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
        Bạn là một chuyên gia Tử Vi Đẩu Số hàng đầu Việt Nam với hơn 30 năm kinh nghiệm.
        Bạn có kiến thức sâu rộng về:
        - 14 Chính tinh và ý nghĩa của chúng
        - 12 cung trong lá số Tử Vi
        - Các Phụ tinh, Bàng tinh và ảnh hưởng của chúng
        - Ngũ Hành tương sinh tương khắc
        - Độ sáng của sao (Miếu, Vượng, Đắc, Bình, Hãm)
        - Đại vận, Tiểu vận và Lưu niên
        - Tuần, Triệt và ảnh hưởng của chúng
        
        Nhiệm vụ của bạn là phân tích lá số Tử Vi và đưa ra giải luận chi tiết, chính xác.
        Giải luận phải:
        - Sử dụng tiếng Việt chuẩn, văn phong trang trọng nhưng dễ hiểu
        - Đưa ra phân tích khách quan, cân bằng giữa tốt và xấu
        - Tránh mê tín dị đoan, tập trung vào hướng dẫn thực tế
        - Đưa ra lời khuyên hữu ích cho người xem
        """;

    private static final String SYSTEM_PROMPT_CYCLES = """
        Bạn là một chuyên gia Tử Vi Đẩu Số hàng đầu Việt Nam với hơn 30 năm kinh nghiệm.
        Bạn chuyên về phân tích Đại vận và Tiểu vận trong Tử Vi.
        
        Kiến thức chuyên môn:
        - Đại vận: chu kỳ 10 năm, mỗi cung ảnh hưởng một giai đoạn
        - Tiểu vận: chu kỳ hàng năm trong mỗi Đại vận
        - Thuận/Nghịch: hướng di chuyển của Đại vận
        - Ảnh hưởng của các sao trong từng cung theo thời gian
        - Tuần/Triệt và tác động trong từng giai đoạn
        
        Nhiệm vụ: Phân tích chi tiết từng giai đoạn Đại vận, đưa ra:
        - Đặc điểm chính của giai đoạn
        - Cơ hội và thách thức
        - Lời khuyên cụ thể cho từng giai đoạn
        
        Văn phong: Tiếng Việt chuẩn, trang trọng, dễ hiểu, thực tế.
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
            Hãy đưa ra giải luận đầy đủ bao gồm:
            
            1. **TỔNG QUAN** (overview):
               - Giới thiệu tổng quan về lá số
               - Phân tích Bản mệnh và Cục
               - Phân tích Chủ mệnh và Chủ thân
               - Phân tích Thân cư
               - Phân tích Thuận/Nghịch
               - Tổng kết chung
            
            2. **PHÂN TÍCH 12 CUNG**: Với mỗi cung, đưa ra:
               - Tóm tắt ngắn (2-3 câu)
               - Giới thiệu ý nghĩa cung
               - Phân tích chi tiết dựa trên các sao
               - Ảnh hưởng của Tuần/Triệt (nếu có)
               - Lời khuyên
               - Kết luận
            
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
        
        // Đại vận list
        prompt.append("## DANH SÁCH ĐẠI VẬN\n");
        List<CycleInfo.DaiVanEntry> daiVanList = cycles.getDaiVanList();
        if (daiVanList != null) {
            for (CycleInfo.DaiVanEntry entry : daiVanList) {
                prompt.append(String.format("- Tuổi %d-%d: Cung %s\n", 
                        entry.getStartAge(), entry.getEndAge(), entry.getPalaceName()));
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
        
        prompt.append("\n## YÊU CẦU\n");
        prompt.append("""
            Hãy phân tích chi tiết từng giai đoạn Đại vận. Với mỗi Đại vận:
            
            1. **Tóm tắt**: 2-3 câu về đặc điểm chính của giai đoạn
            2. **Phân tích chi tiết**: 
               - Ảnh hưởng của cung và các sao trong cung
               - Cơ hội và thuận lợi
               - Thách thức và khó khăn
               - Ảnh hưởng của Tuần/Triệt (nếu có)
            3. **Chủ đề chính**: Các lĩnh vực nổi bật trong giai đoạn
            4. **Lời khuyên**: Hướng dẫn cụ thể cho giai đoạn này
            
            Trả về dạng JSON với cấu trúc:
            {
              "introduction": "Giới thiệu về hệ thống Đại vận trong lá số này...",
              "overallCycleSummary": "Tổng quan về vận mệnh qua các giai đoạn...",
              "daiVanInterpretations": [
                {
                  "startAge": 5,
                  "endAge": 14,
                  "palaceCode": "MENH",
                  "palaceName": "Mệnh",
                  "summary": "...",
                  "interpretation": "...",
                  "keyThemes": "...",
                  "advice": "..."
                },
                ...
              ],
              "generalAdvice": "Lời khuyên chung cho việc ứng dụng Đại vận..."
            }
            """);
        
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


package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.essay.PalaceEssay;
import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import com.duong.lichvanien.tuvi.util.PalaceAxisMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service để tổng hợp các interpretation fragments thành bài luận dài 800-1000 từ cho mỗi cung.
 * 
 * Bài luận có cấu trúc 6 phần (theo system prompt TU VI INTERPRETATION CONTENT REFINER):
 * 1. Bản chất sao tại cung (Core Nature of the Star(s))
 * 2. Ảnh hưởng theo trục cung (Primary Influence Along the Palace Axis)
 * 3. Biểu hiện theo mức độ sáng (Expression According to Brightness Level)
 * 4. Điểm mạnh (Key Strengths)
 * 5. Hạn chế tự nhiên (Natural Limitations - Balanced Perspective)
 * 6. Tổng hợp trung lập (Neutral Synthesis)
 * 
 * Văn phong: học thuật nhưng dễ đọc, bình tĩnh, cổ điển, không lặp ý.
 * KHÔNG: lời khuyên, phán số phận, đánh giá tiêu cực.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PalaceEssayCompositionService {

    private static final int MIN_WORDS = 800;
    private static final int MAX_WORDS = 1200;
    
    // Các từ/cụm từ bị cấm (theo style requirements)
    private static final List<String> FORBIDDEN_PHRASES = Arrays.asList(
        // Lời khuyên
        "nên", "cần phải", "bắt buộc", "hãy", "tôi khuyên", "lời khuyên", "bạn nên",
        // Phán số phận
        "chắc chắn sẽ", "cuộc đời sẽ", "số phận", "định mệnh", 
        "chết", "yểu mệnh", "giàu có", "nghèo khổ",
        "thất bại hoàn toàn", "thành công vĩ đại",
        // Filler phrases cần tránh
        "ở mức trung bình", "vừa phải", "không quá nổi bật",
        "có khả năng nhưng", "cần điều kiện phù hợp"
    );

    /**
     * Tạo bài luận hoàn chỉnh cho một cung theo cấu trúc 6 phần.
     * 
     * @param palace Thông tin cung cần giải luận
     * @param fragments Các fragments đã match cho cung này
     * @param chart Toàn bộ lá số (để lấy context nếu cần)
     * @return PalaceEssay chứa bài luận hoàn chỉnh
     */
    public PalaceEssay composeFullEssay(PalaceInfo palace, 
                                         List<InterpretationFragmentEntity> fragments,
                                         TuViChartResponse chart) {
        log.info("Composing essay for palace: {}", palace.getNameCode());
        
        Map<String, String> sections = new LinkedHashMap<>();
        
        // Phần giới thiệu (riêng biệt, không nằm trong 6 phần chính)
        String introduction = composeIntroduction(palace);
        sections.put("introduction", introduction);
        
        // Phần Giải luận chi tiết - 6 phần theo cấu trúc mới
        StringBuilder detailedEssay = new StringBuilder();
        
        // 1. Bản chất sao tại cung (Core Nature)
        String coreNature = composeCoreNature(palace, fragments);
        sections.put("coreNature", coreNature);
        detailedEssay.append(coreNature).append("\n\n");
        
        // 2. Ảnh hưởng theo trục cung (Axis Influence)
        String axisInfluence = composeAxisInfluence(palace, fragments);
        sections.put("axisInfluence", axisInfluence);
        detailedEssay.append(axisInfluence).append("\n\n");
        
        // 3. Biểu hiện theo mức độ sáng (Brightness Expression)
        String brightnessExpression = composeBrightnessExpression(palace, fragments);
        sections.put("brightnessExpression", brightnessExpression);
        detailedEssay.append(brightnessExpression).append("\n\n");
        
        // 4. Điểm mạnh (Key Strengths)
        String keyStrengths = composeKeyStrengths(palace, fragments);
        sections.put("keyStrengths", keyStrengths);
        detailedEssay.append(keyStrengths).append("\n\n");
        
        // 5. Hạn chế tự nhiên (Natural Limitations)
        String naturalLimitations = composeNaturalLimitations(palace, fragments);
        sections.put("naturalLimitations", naturalLimitations);
        detailedEssay.append(naturalLimitations).append("\n\n");
        
        // 6. Tổng hợp trung lập (Neutral Synthesis)
        String neutralSynthesis = composeNeutralSynthesis(palace, fragments);
        sections.put("neutralSynthesis", neutralSynthesis);
        detailedEssay.append(neutralSynthesis);
        
        String essay = detailedEssay.toString().trim();
        
        // Tạo summary (không hiển thị trên FE nhưng giữ lại cho API)
        String summary = createSummary(introduction, neutralSynthesis);
        
        PalaceEssay result = PalaceEssay.builder()
                .palaceCode(palace.getNameCode())
                .palaceName(palace.getName())
                .fullEssay(essay)
                .summary(summary)
                .wordCount(countWords(essay))
                .sections(sections)
                .build();
        
        // Validate
        validateEssay(result);
        
        log.info("Composed essay for palace {} with {} words", palace.getNameCode(), result.getWordCount());
        return result;
    }

    /**
     * Phần Giới thiệu - Vai trò cung trong lá số.
     * Hiển thị riêng biệt, không nằm trong 6 phần chính.
     */
    public String composeIntroduction(PalaceInfo palace) {
        String palaceCode = palace.getNameCode();
        PalaceAxisMapper.PalaceAxis axis = PalaceAxisMapper.getPalaceAxis(palaceCode);
        
        if (axis == null) {
            log.warn("No axis mapping found for palace: {}", palaceCode);
            return String.format(
                "Cung %s trong lá số Tử Vi mang những ý nghĩa riêng biệt, " +
                "phản ánh một khía cạnh quan trọng của cuộc sống. " +
                "Vị trí này cho thấy những xu hướng và đặc điểm " +
                "liên quan đến lĩnh vực mà nó đại diện.",
                palace.getName()
            );
        }
        
        return axis.getIntroduction() + " " + axis.getImpact();
    }

    // ==================== 6 PHẦN CHÍNH THEO CẤU TRÚC MỚI ====================

    /**
     * Phần 1: Bản chất sao tại cung (Core Nature of the Star(s))
     * Mô tả bản chất nội tại của sao khi đặt tại cung này.
     * Văn phong thân thiện với "bạn".
     */
    public String composeCoreNature(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        List<StarInfo> mainStars = getMainStars(palace);
        
        if (mainStars.isEmpty()) {
            return composeCoreNatureNoMainStar(palace, fragments);
        }
        
        StringBuilder nature = new StringBuilder();
        
        if (mainStars.size() == 1) {
            StarInfo star = mainStars.get(0);
            nature.append(getStarCoreNature(star.getCode(), star.getName()));
        } else {
            // Nhiều chính tinh đồng cung
            nature.append("Tại đây có sự hiện diện đồng thời của sao ");
            nature.append(mainStars.stream().map(StarInfo::getName).collect(Collectors.joining(" và sao ")));
            nature.append(". ");
            
            for (StarInfo star : mainStars) {
                nature.append(getStarCoreNatureShort(star.getCode(), star.getName()));
                nature.append(" ");
            }
            
            nature.append("Sự kết hợp này mang đến cho bạn một tổ hợp năng lượng đặc thù, ");
            nature.append("trong đó các đặc tính của từng sao hòa quyện và bổ sung lẫn nhau.");
        }
        
        // Thêm nội dung từ fragments nếu có
        List<InterpretationFragmentEntity> coreFragments = fragments.stream()
                .filter(f -> mainStars.stream().anyMatch(s -> f.getFragmentCode().contains(s.getCode())))
                .sorted(Comparator.comparing(InterpretationFragmentEntity::getPriority))
                .limit(2)
                .collect(Collectors.toList());
        
        if (!coreFragments.isEmpty()) {
            nature.append(" ");
            nature.append(coreFragments.stream()
                    .map(InterpretationFragmentEntity::getContent)
                    .map(this::cleanContent)
                    .collect(Collectors.joining(" ")));
        }
        
        return nature.toString();
    }

    /**
     * Phần 2: Ảnh hưởng theo trục cung (Primary Influence Along the Palace Axis)
     * Giải thích khía cạnh đời sống mà cung này chi phối và cách sao ảnh hưởng đến trục đó.
     * Văn phong thân thiện với "bạn".
     */
    public String composeAxisInfluence(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        String palaceCode = palace.getNameCode();
        PalaceAxisMapper.PalaceAxis axis = PalaceAxisMapper.getPalaceAxis(palaceCode);
        List<StarInfo> mainStars = getMainStars(palace);
        
        StringBuilder influence = new StringBuilder();
        
        String axisName = axis != null ? axis.getAxis() : "lĩnh vực liên quan";
        String palaceName = palace.getName();
        
        // Mô tả trục cung - thân thiện hơn
        influence.append(String.format(
            "Cung %s ảnh hưởng đến trục %s trong cuộc sống của bạn. ", palaceName, axisName
        ));
        
        // Thêm nội dung theo trục cụ thể
        influence.append(getAxisSpecificContent(palaceCode));
        
        // Ảnh hưởng của sao lên trục
        if (!mainStars.isEmpty()) {
            influence.append(" ");
            influence.append(String.format(
                "Nhờ có sao %s tọa thủ, ",
                mainStars.stream().map(StarInfo::getName).collect(Collectors.joining(" và sao "))
            ));
            influence.append(getStarAxisInfluence(mainStars.get(0).getCode()));
            influence.append(". Điều này ảnh hưởng trực tiếp đến cách bạn trải nghiệm lĩnh vực này.");
        }
        
        // Thêm từ fragments
        List<String> relevantContents = extractRelevantContent(fragments, 2);
        if (!relevantContents.isEmpty()) {
            influence.append(" ");
            influence.append(String.join(" ", relevantContents));
        }
        
        return influence.toString();
    }

    /**
     * Phần 3: Biểu hiện theo mức độ sáng (Expression According to Brightness Level)
     * Giải thích cách mức độ sáng ảnh hưởng đến sự biểu hiện.
     * Văn phong thân thiện với "bạn".
     */
    public String composeBrightnessExpression(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        List<StarInfo> mainStars = getMainStars(palace);
        
        if (mainStars.isEmpty()) {
            return "Vị trí này không có Chính tinh tọa thủ. " +
                   "Sự biểu hiện phụ thuộc vào các sao phụ tinh hiện diện " +
                   "và mối liên hệ với các cung tam hợp. Nhờ vậy, tính chất mang " +
                   "đặc điểm linh hoạt, giúp bạn dễ thích ứng với hoàn cảnh và môi trường.";
        }
        
        StringBuilder expression = new StringBuilder();
        expression.append("Về mức độ sáng của các sao tại đây: ");
        
        for (int i = 0; i < mainStars.size(); i++) {
            StarInfo star = mainStars.get(i);
            String brightness = star.getBrightness();
            if (brightness == null || brightness.isBlank()) {
                brightness = "BINH";
            }
            
            if (i > 0) {
                expression.append(" ");
            }
            expression.append(getBrightnessExpression(star.getName(), brightness));
        }
        
        // Thêm từ fragments về brightness
        List<InterpretationFragmentEntity> brightnessFragments = fragments.stream()
                .filter(f -> f.getFragmentCode().contains("_MIEU") || 
                            f.getFragmentCode().contains("_VUONG") ||
                            f.getFragmentCode().contains("_DAC") ||
                            f.getFragmentCode().contains("_BINH") ||
                            f.getFragmentCode().contains("_HAM"))
                .limit(1)
                .collect(Collectors.toList());
        
        if (!brightnessFragments.isEmpty()) {
            expression.append(" ");
            expression.append(cleanContent(brightnessFragments.get(0).getContent()));
        }
        
        return expression.toString();
    }

    /**
     * Phần 4: Điểm mạnh (Key Strengths)
     * Mô tả những lợi thế tự nhiên dễ quan sát nhất.
     * Văn phong thân thiện với "bạn".
     */
    public String composeKeyStrengths(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        List<StarInfo> mainStars = getMainStars(palace);
        
        StringBuilder strengths = new StringBuilder();
        
        // Lấy fragments có tone positive
        List<InterpretationFragmentEntity> positiveFragments = fragments.stream()
                .filter(f -> f.getTone() == InterpretationFragmentEntity.Tone.positive)
                .sorted(Comparator.comparing(InterpretationFragmentEntity::getPriority))
                .limit(3)
                .collect(Collectors.toList());
        
        if (!mainStars.isEmpty()) {
            strengths.append(String.format(
                "Với sao %s tọa thủ, bạn có những đặc điểm nổi bật: ",
                mainStars.stream().map(StarInfo::getName).collect(Collectors.joining(" và sao "))
            ));
            
            // Thêm điểm mạnh cụ thể của sao
            for (StarInfo star : mainStars) {
                strengths.append(getStarStrengths(star.getCode()));
                strengths.append(" ");
            }
        } else {
            strengths.append("Tại vị trí này, bạn có những đặc điểm thuận lợi riêng: ");
            strengths.append("tính linh hoạt cao giúp bạn thích ứng tốt, ");
            strengths.append("khả năng tiếp nhận ảnh hưởng từ nhiều nguồn khác nhau, ");
            strengths.append("và sự mở rộng không bị giới hạn bởi một khuôn mẫu cố định. ");
        }
        
        // Thêm từ positive fragments
        if (!positiveFragments.isEmpty()) {
            strengths.append(positiveFragments.stream()
                    .map(InterpretationFragmentEntity::getContent)
                    .map(this::cleanContent)
                    .collect(Collectors.joining(" ")));
        }
        
        return strengths.toString();
    }

    /**
     * Phần 5: Đặc điểm bổ sung (Additional Characteristics)
     * Viết theo văn phong thân thiện với "bạn", TRUNG LẬP.
     */
    public String composeNaturalLimitations(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        List<StarInfo> mainStars = getMainStars(palace);
        
        StringBuilder content = new StringBuilder();
        
        content.append("Bên cạnh đó, nơi đây còn có một số đặc điểm đáng chú ý: ");
        
        if (!mainStars.isEmpty()) {
            for (StarInfo star : mainStars) {
                content.append(getStarCharacteristics(star.getCode()));
                content.append(" ");
            }
        } else {
            content.append("Với đặc điểm không có Chính tinh tọa thủ, vị trí này ");
            content.append("có tính chất linh hoạt, giúp bạn chịu ảnh hưởng từ các yếu tố khác trong lá số. ");
            content.append("Điều này mang đến cho bạn sự đa dạng trong cách biểu hiện. ");
        }
        
        // Thêm về Tuần/Triệt nếu có (trình bày thân thiện, dễ hiểu)
        if (palace.isHasTuan()) {
            content.append("Một điểm đáng chú ý là sao Tuần đang chiếu vào vị trí này. ");
            content.append("Điều này giúp bạn có thêm chiều sâu trong suy nghĩ và hành động, ");
            content.append("đồng thời mang đến cho bạn những góc nhìn độc đáo mà không phải ai cũng có. ");
            content.append("Sao Tuần như một lớp màn sương mỏng, làm cho các đặc điểm của bạn ");
            content.append("trở nên tinh tế và khó nắm bắt hơn với người ngoài. ");
        }
        
        if (palace.isHasTriet()) {
            content.append("Bên cạnh đó, sao Triệt cũng đang ảnh hưởng đến vị trí này. ");
            content.append("Sự hiện diện của sao Triệt tạo ra những điểm nhấn riêng biệt trong cách bạn thể hiện bản thân. ");
            content.append("Bạn có thể cảm nhận được rằng một số khía cạnh cần thời gian để bộc lộ, ");
            content.append("nhưng điều này cũng giúp bạn phát triển sự kiên nhẫn và chiều sâu nội tâm. ");
        }
        
        return content.toString();
    }

    /**
     * Phần 6: Tổng hợp (Synthesis)
     * Viết theo văn phong thân thiện với "bạn", TRUNG LẬP.
     */
    public String composeNeutralSynthesis(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        String palaceName = palace.getName();
        List<StarInfo> mainStars = getMainStars(palace);
        
        StringBuilder synthesis = new StringBuilder();
        
        // Mở đầu thân thiện
        synthesis.append(String.format("Tóm lại, cung %s ", palaceName));
        
        if (!mainStars.isEmpty()) {
            if (mainStars.size() == 1) {
                synthesis.append(String.format("với sự hiện diện của sao %s, ", mainStars.get(0).getName()));
            } else {
                synthesis.append(String.format(
                    "với sự kết hợp của sao %s, ",
                    mainStars.stream().map(StarInfo::getName).collect(Collectors.joining(" và sao "))
                ));
            }
        }
        
        // Mô tả tổng quan - THÂN THIỆN
        synthesis.append("mang đến cho bạn những đặc điểm riêng biệt. ");
        
        // Thêm mô tả về tiềm năng
        synthesis.append("Cấu trúc này phản ánh tiềm năng phát triển của bạn theo nhiều hướng khác nhau. ");
        
        // Thêm mô tả về các sao phụ nếu có
        List<StarInfo> auxStars = getAuxiliaryStars(palace);
        if (!auxStars.isEmpty()) {
            synthesis.append("Các sao phụ tinh đi kèm cũng góp phần làm phong phú thêm các đặc điểm này. ");
        }
        
        // Kết luận thân thiện
        synthesis.append("Những xu hướng được thể hiện ở đây là một phần quan trọng ");
        synthesis.append("trong bức tranh tổng thể về con người bạn.");
        
        return synthesis.toString();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lấy danh sách Chính tinh trong cung.
     */
    private List<StarInfo> getMainStars(PalaceInfo palace) {
        if (palace.getStars() == null) {
            return Collections.emptyList();
        }
        return palace.getStars().stream()
                .filter(s -> "CHINH_TINH".equals(s.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách sao phụ tinh trong cung.
     */
    private List<StarInfo> getAuxiliaryStars(PalaceInfo palace) {
        if (palace.getStars() == null) {
            return Collections.emptyList();
        }
        return palace.getStars().stream()
                .filter(s -> "PHU_TINH".equals(s.getType()) || "BANG_TINH".equals(s.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Compose Core Nature khi không có Chính tinh.
     */
    private String composeCoreNatureNoMainStar(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        StringBuilder nature = new StringBuilder();
        
        nature.append(String.format(
            "Cung %s của bạn không có Chính tinh tọa thủ. ",
            palace.getName()
        ));
        
        nature.append("Đây là một đặc điểm cấu trúc đáng chú ý. Thay vào đó, bản chất của cung ");
        nature.append("được xác định thông qua các yếu tố khác như: các sao phụ tinh hiện diện, ");
        nature.append("mối quan hệ với cung đối diện và các cung tam hợp. ");
        nature.append("Điều này không có nghĩa là thiếu sót, mà ngược lại, nó cho thấy bạn có ");
        nature.append("tính cách mở và linh hoạt, dễ thích ứng với nhiều hoàn cảnh khác nhau trong cuộc sống.");
        
        return nature.toString();
    }

    /**
     * Lấy bản chất cốt lõi của sao (Core Nature) - phiên bản đầy đủ, thân thiện với "bạn".
     */
    private String getStarCoreNature(String starCode, String starName) {
        Map<String, String> coreNatures = new HashMap<>();
        coreNatures.put("TU_VI", 
            "Sao Tử Vi - đế tinh, đại diện cho nguyên lý trung tâm và quyền uy tự nhiên - hiện diện tại đây. " +
            "Điều này mang đến cho bạn đặc tính tự chủ, khả năng định hướng và tổ chức. " +
            "Nhờ vậy, bạn có xu hướng độc lập, có chủ kiến " +
            "và thường nắm giữ vai trò trung tâm trong lĩnh vực liên quan.");
        
        coreNatures.put("THIEN_CO",
            "Sao Thiên Cơ - sao của trí tuệ và sự linh hoạt - tọa thủ tại vị trí này. " +
            "Điều này mang đến cho bạn khả năng tư duy, phân tích và thích ứng nhanh nhạy với hoàn cảnh. " +
            "Nhờ vậy, bạn có tính chất năng động về mặt trí óc, có khả năng " +
            "nắm bắt vấn đề và tìm ra giải pháp một cách sáng tạo.");
        
        coreNatures.put("THAI_DUONG",
            "Sao Thái Dương - sao của sự tỏa sáng và quảng đại - an tọa tại đây. " +
            "Điều này mang đến cho bạn năng lượng hướng ngoại, khả năng ban phát và tạo ảnh hưởng rộng rãi. " +
            "Nhờ vậy, bạn có đặc tính nhiệt tình, cởi mở và xu hướng " +
            "muốn mang lại lợi ích cho nhiều người.");
        
        coreNatures.put("VU_KHUC",
            "Sao Vũ Khúc - sao của sự cương quyết và thực tế - chiếu mệnh tại vị trí này. " +
            "Điều này mang đến cho bạn khả năng hành động, quyết đoán và thực hiện. " +
            "Nhờ vậy, bạn có đặc tính thẳng thắn, hướng đến kết quả cụ thể và có năng lực " +
            "xử lý tốt các vấn đề liên quan đến vật chất, tài chính.");
        
        coreNatures.put("THIEN_DONG",
            "Sao Thiên Đồng - sao của sự hòa nhã và an nhiên - hiện diện ở đây. " +
            "Điều này mang đến cho bạn sự ổn định, hài hòa và xu hướng tìm kiếm sự thoải mái. " +
            "Nhờ vậy, bạn có đặc tính dễ chịu, thích hưởng thụ " +
            "và có khả năng tạo ra môi trường thanh thản cho bản thân cũng như người xung quanh.");
        
        coreNatures.put("LIEM_TRINH",
            "Sao Liêm Trinh - sao của sự chính trực và đam mê - tọa thủ nơi đây. " +
            "Điều này mang đến cho bạn nguyên tắc, nhiệt huyết và sự kiên định với lập trường. " +
            "Nhờ vậy, bạn có đặc tính nghiêm túc, có tiêu chuẩn cao " +
            "và xu hướng theo đuổi điều mình tin là đúng đắn.");
        
        coreNatures.put("THIEN_PHU",
            "Sao Thiên Phủ - sao của sự phú quý và tích lũy - an tọa tại vị trí này. " +
            "Điều này mang đến cho bạn khả năng quản lý, bảo tồn và xây dựng nền tảng vững chắc. " +
            "Nhờ vậy, bạn có đặc tính ổn định, có khả năng duy trì " +
            "và phát triển những gì đã có.");
        
        coreNatures.put("THAI_AM",
            "Sao Thái Âm - sao của sự tinh tế và nội tâm - hiện diện tại đây. " +
            "Điều này mang đến cho bạn cảm xúc, trực giác và sự nhạy cảm với môi trường xung quanh. " +
            "Nhờ vậy, bạn có đặc tính sâu sắc, có khả năng thấu hiểu " +
            "và tiếp nhận những tín hiệu tinh vi.");
        
        coreNatures.put("THAM_LANG",
            "Sao Tham Lang - sao của sự đa dạng và khao khát - tọa thủ ở vị trí này. " +
            "Điều này mang đến cho bạn tham vọng, sáng tạo và khả năng thích ứng với nhiều hoàn cảnh. " +
            "Nhờ vậy, bạn có đặc tính đa tài, có nhiều sở thích " +
            "và xu hướng muốn trải nghiệm nhiều mặt của cuộc sống.");
        
        coreNatures.put("CU_MON",
            "Sao Cự Môn - sao của sự tranh biện và giao tiếp - chiếu mệnh tại đây. " +
            "Điều này mang đến cho bạn ngôn từ, phân tích và khả năng diễn đạt. " +
            "Nhờ vậy, bạn có đặc tính sắc bén về mặt ngôn ngữ, có khả năng thuyết phục " +
            "và xu hướng đi sâu vào vấn đề.");
        
        coreNatures.put("THIEN_TUONG",
            "Sao Thiên Tướng - sao của sự hỗ trợ và điều hòa - an tọa nơi đây. " +
            "Điều này mang đến cho bạn khả năng kết nối, cân bằng và làm trung gian. " +
            "Nhờ vậy, bạn có đặc tính hòa giải, có khả năng tạo sự đồng thuận " +
            "và xu hướng giúp đỡ người khác.");
        
        coreNatures.put("THIEN_LUONG",
            "Sao Thiên Lương - sao của sự che chở và từ bi - hiện diện ở vị trí này. " +
            "Điều này mang đến cho bạn khả năng bảo vệ, chữa lành và lòng nhân ái. " +
            "Nhờ vậy, bạn có đặc tính quan tâm đến người khác, có khả năng hóa giải " +
            "và xu hướng đứng ra gánh vác trách nhiệm.");
        
        coreNatures.put("THAT_SAT",
            "Sao Thất Sát - sao của sự quyết đoán và hành động - tọa thủ tại đây. " +
            "Điều này mang đến cho bạn sức mạnh, can đảm và khả năng đối mặt với thử thách. " +
            "Nhờ vậy, bạn có đặc tính dũng cảm, có năng lực vượt qua " +
            "trở ngại và xu hướng đi thẳng vào vấn đề.");
        
        coreNatures.put("PHA_QUAN",
            "Sao Phá Quân - sao của sự thay đổi và đột phá - chiếu mệnh nơi đây. " +
            "Điều này mang đến cho bạn tinh thần cách mạng, đổi mới và khả năng phá vỡ giới hạn. " +
            "Nhờ vậy, bạn có đặc tính năng động, có xu hướng tái cấu trúc " +
            "và không ngại bắt đầu lại từ đầu.");
        
        return coreNatures.getOrDefault(starCode,
            String.format("Sao %s tọa thủ tại vị trí này, mang đến cho bạn những đặc trưng riêng biệt " +
                         "phù hợp với bản chất của sao trong hệ thống Tử Vi.", starName));
    }

    /**
     * Lấy bản chất cốt lõi của sao - phiên bản ngắn (cho trường hợp nhiều sao), thân thiện.
     */
    private String getStarCoreNatureShort(String starCode, String starName) {
        Map<String, String> shortNatures = new HashMap<>();
        shortNatures.put("TU_VI", "Sao Tử Vi mang đến cho bạn quyền uy và sự tự chủ.");
        shortNatures.put("THIEN_CO", "Sao Thiên Cơ mang đến cho bạn trí tuệ và sự linh hoạt.");
        shortNatures.put("THAI_DUONG", "Sao Thái Dương mang đến cho bạn năng lượng tỏa sáng và quảng đại.");
        shortNatures.put("VU_KHUC", "Sao Vũ Khúc mang đến cho bạn sự cương quyết và thực tế.");
        shortNatures.put("THIEN_DONG", "Sao Thiên Đồng mang đến cho bạn sự hòa nhã và an nhiên.");
        shortNatures.put("LIEM_TRINH", "Sao Liêm Trinh mang đến cho bạn đặc tính chính trực và đam mê.");
        shortNatures.put("THIEN_PHU", "Sao Thiên Phủ mang đến cho bạn sự tích lũy và ổn định.");
        shortNatures.put("THAI_AM", "Sao Thái Âm mang đến cho bạn sự tinh tế và nội tâm.");
        shortNatures.put("THAM_LANG", "Sao Tham Lang mang đến cho bạn tính đa dạng và khao khát.");
        shortNatures.put("CU_MON", "Sao Cự Môn mang đến cho bạn sự tranh biện và giao tiếp.");
        shortNatures.put("THIEN_TUONG", "Sao Thiên Tướng mang đến cho bạn sự hỗ trợ và điều hòa.");
        shortNatures.put("THIEN_LUONG", "Sao Thiên Lương mang đến cho bạn đặc tính che chở và từ bi.");
        shortNatures.put("THAT_SAT", "Sao Thất Sát mang đến cho bạn sự quyết đoán và dũng cảm.");
        shortNatures.put("PHA_QUAN", "Sao Phá Quân mang đến cho bạn sự thay đổi và đột phá.");
        
        return shortNatures.getOrDefault(starCode,
            String.format("Sao %s mang đến cho bạn những đặc trưng riêng biệt.", starName));
    }

    /**
     * Lấy nội dung theo trục cung cụ thể.
     */
    private String getAxisSpecificContent(String palaceCode) {
        Map<String, String> axisContents = new HashMap<>();
        
        axisContents.put("MENH",
            "Đây là trục liên quan trực tiếp đến con người bạn - " +
            "bao gồm tính cách bẩm sinh, cách bạn nhìn nhận thế giới, " +
            "và cách bạn tương tác với mọi người xung quanh.");
        
        axisContents.put("QUAN_LOC",
            "Đây là trục ảnh hưởng đến sự nghiệp của bạn, " +
            "từ con đường nghề nghiệp, phong cách làm việc, " +
            "cho đến khả năng phát triển và vị trí của bạn trong xã hội.");
        
        axisContents.put("TAI_BACH",
            "Đây là trục liên quan đến tài chính của bạn - " +
            "từ khả năng tạo ra thu nhập, cách bạn quản lý tiền bạc, " +
            "cho đến thái độ của bạn với các giá trị vật chất.");
        
        axisContents.put("PHU_THE",
            "Đây là trục chi phối các mối quan hệ thân mật của bạn, " +
            "đặc biệt là hôn nhân, cách bạn xây dựng và duy trì quan hệ đôi lứa, " +
            "cũng như những đặc điểm của người bạn đời phù hợp với bạn.");
        
        axisContents.put("TU_TUC",
            "Đây là trục liên quan đến con cái và khả năng sáng tạo của bạn - " +
            "bao gồm mối quan hệ với thế hệ sau, " +
            "và những gì bạn có thể tạo ra hoặc để lại.");
        
        axisContents.put("TAT_ACH",
            "Đây là trục ảnh hưởng đến sức khỏe thể chất của bạn, " +
            "từ thể trạng tổng thể, xu hướng về sức khỏe, " +
            "cho đến khả năng hồi phục khi gặp vấn đề.");
        
        axisContents.put("THIEN_DI",
            "Đây là trục liên quan đến hoạt động bên ngoài và sự di chuyển của bạn - " +
            "bao gồm các chuyến đi xa, khả năng thích ứng với môi trường mới, " +
            "và cách bạn tương tác với những người mới quen.");
        
        axisContents.put("NO_BOC",
            "Đây là trục chi phối các mối quan hệ xã hội của bạn, " +
            "từ bạn bè, đồng nghiệp, " +
            "cho đến những người hỗ trợ bạn trong công việc và cuộc sống.");
        
        axisContents.put("DIEN_TRACH",
            "Đây là trục liên quan đến nhà cửa và tài sản cố định của bạn - " +
            "từ khả năng sở hữu bất động sản, môi trường sống, " +
            "cho đến sự ổn định về nơi ở.");
        
        axisContents.put("PHUC_DUC",
            "Đây là trục chi phối phúc phần và đời sống tinh thần của bạn, " +
            "bao gồm phước đức tích lũy, khả năng hưởng phúc, " +
            "và mối liên hệ của bạn với các giá trị tâm linh.");
        
        axisContents.put("PHU_MAU",
            "Đây là trục liên quan đến mối quan hệ của bạn với cha mẹ và nguồn gốc gia đình - " +
            "bao gồm ảnh hưởng từ gia đình, di sản được thừa hưởng, " +
            "và sự kết nối của bạn với thế hệ trước.");
        
        axisContents.put("HUYNH_DE",
            "Đây là trục chi phối mối quan hệ của bạn với anh chị em và bạn bè thân thiết, " +
            "bao gồm cách bạn tương tác với những người ngang hàng, " +
            "và khả năng hợp tác, hỗ trợ lẫn nhau.");
        
        return axisContents.getOrDefault(palaceCode,
            "Đây là trục chi phối một lĩnh vực quan trọng trong cuộc sống của bạn, " +
            "phản ánh những xu hướng và đặc điểm riêng.");
    }

    /**
     * Lấy ảnh hưởng của sao lên trục cung.
     */
    private String getStarAxisInfluence(String starCode) {
        Map<String, String> influences = new HashMap<>();
        influences.put("TU_VI", "bạn thường tự chủ, có định hướng rõ ràng và hay đứng ở vị trí trung tâm");
        influences.put("THIEN_CO", "bạn linh hoạt trong suy nghĩ, dễ thích ứng và có tư duy sáng tạo");
        influences.put("THAI_DUONG", "bạn cởi mở, hướng ngoại và có khả năng lan tỏa năng lượng tích cực");
        influences.put("VU_KHUC", "bạn thực tế, quyết đoán và luôn hướng đến kết quả cụ thể");
        influences.put("THIEN_DONG", "bạn hài hòa, ổn định và thích tìm kiếm sự thoải mái trong cuộc sống");
        influences.put("LIEM_TRINH", "bạn có nguyên tắc rõ ràng, tiêu chuẩn cao và sự kiên định đáng nể");
        influences.put("THIEN_PHU", "bạn ổn định, biết cách tích lũy và bảo tồn những gì quan trọng");
        influences.put("THAI_AM", "bạn tinh tế, nhạy cảm và có chiều sâu nội tâm phong phú");
        influences.put("THAM_LANG", "bạn đa dạng trong sở thích, có nhiều khía cạnh và dễ thích ứng");
        influences.put("CU_MON", "bạn giỏi phân tích, có khả năng diễn đạt tốt và thích đi sâu vào vấn đề");
        influences.put("THIEN_TUONG", "bạn biết cách điều hòa, kết nối mọi người và tạo sự cân bằng");
        influences.put("THIEN_LUONG", "bạn có lòng che chở, hay quan tâm đến người khác và sẵn sàng gánh vác");
        influences.put("THAT_SAT", "bạn quyết đoán, có năng lực hành động mạnh mẽ và không ngại thử thách");
        influences.put("PHA_QUAN", "bạn thích thay đổi, có xu hướng đổi mới và không ngại phá vỡ khuôn mẫu");
        
        return influences.getOrDefault(starCode, "bạn có những đặc trưng riêng biệt phù hợp với bản chất của sao");
    }

    /**
     * Lấy mô tả biểu hiện theo mức độ sáng.
     */
    private String getBrightnessExpression(String starName, String brightness) {
        switch (brightness.toUpperCase()) {
            case "MIEU":
                return String.format(
                    "Sao %s đang ở miếu địa - đây là trạng thái sáng nhất và thuận lợi nhất. " +
                    "Điều này có nghĩa là các đặc tính của sao được phát huy một cách trọn vẹn trong bạn. " +
                    "Bạn sẽ cảm nhận được năng lượng này một cách tự nhiên, " +
                    "không gặp trở ngại trong việc thể hiện những gì sao này mang lại.",
                    starName
                );
            case "VUONG":
                return String.format(
                    "Sao %s đang ở vượng địa - đây là trạng thái rất tốt với năng lượng mạnh mẽ. " +
                    "Các đặc tính của sao được thể hiện với cường độ cao và ổn định trong bạn. " +
                    "Bạn có thể tự tin phát huy những gì sao này mang lại, " +
                    "và duy trì được điều đó trong thời gian dài.",
                    starName
                );
            case "DAC":
                return String.format(
                    "Sao %s đang ở đắc địa - đây là trạng thái thuận lợi và cân bằng. " +
                    "Các đặc tính của sao được thể hiện một cách hợp lý và hài hòa trong bạn. " +
                    "Bạn có thể phát huy những gì sao này mang lại " +
                    "một cách ổn định và phù hợp với hoàn cảnh.",
                    starName
                );
            case "BINH":
                return String.format(
                    "Sao %s đang ở bình địa - đây là trạng thái trung hòa. " +
                    "Các đặc tính của sao được thể hiện ở mức độ vừa phải trong bạn, " +
                    "không quá nổi bật nhưng cũng không bị hạn chế. " +
                    "Bạn có sự linh hoạt trong cách phát huy những gì sao này mang lại.",
                    starName
                );
            case "HAM":
                return String.format(
                    "Sao %s đang ở hãm địa - đây là trạng thái cần thời gian để phát huy. " +
                    "Các đặc tính của sao được thể hiện ở mức độ tiềm ẩn trong bạn, " +
                    "cần có điều kiện và thời gian phù hợp để bộc lộ. " +
                    "Bạn có thể cần sự hỗ trợ từ các yếu tố khác để kích hoạt năng lượng này.",
                    starName
                );
            default:
                return String.format(
                    "Sao %s ở vị trí này thể hiện các đặc tính theo cách riêng trong bạn, " +
                    "phụ thuộc vào tổng thể cấu trúc lá số của bạn.",
                    starName
                );
        }
    }

    /**
     * Lấy điểm mạnh của sao.
     */
    private String getStarStrengths(String starCode) {
        Map<String, String> strengths = new HashMap<>();
        
        strengths.put("TU_VI",
            "bạn có khả năng tự chủ và độc lập trong quyết định, " +
            "sở hữu năng lực lãnh đạo và tổ chức tự nhiên, " +
            "cùng với tầm nhìn xa và khả năng định hướng tốt.");
        
        strengths.put("THIEN_CO",
            "bạn có trí tuệ linh hoạt và khả năng học hỏi nhanh, " +
            "năng lực phân tích và giải quyết vấn đề hiệu quả, " +
            "đồng thời thích ứng tốt với hoàn cảnh thay đổi.");
        
        strengths.put("THAI_DUONG",
            "bạn mang năng lượng tích cực và khả năng truyền cảm hứng cho người khác, " +
            "tính cách cởi mở và quảng đại, " +
            "cùng khả năng tạo ảnh hưởng rộng rãi.");
        
        strengths.put("VU_KHUC",
            "bạn có sự quyết đoán và năng lực thực hiện mạnh mẽ, " +
            "tính thực tế và luôn hướng đến kết quả cụ thể, " +
            "đặc biệt giỏi trong việc xử lý các vấn đề tài chính.");
        
        strengths.put("THIEN_DONG",
            "bạn có tính cách hài hòa và dễ hợp tác với mọi người, " +
            "khả năng tạo ra môi trường thoải mái xung quanh, " +
            "cùng sự ổn định và bình an nội tâm.");
        
        strengths.put("LIEM_TRINH",
            "bạn có tính chính trực và nguyên tắc rõ ràng, " +
            "sự kiên định với mục tiêu đã đề ra, " +
            "cùng nhiệt huyết và đam mê trong công việc.");
        
        strengths.put("THIEN_PHU",
            "bạn có khả năng quản lý và bảo tồn tài sản tốt, " +
            "sự ổn định và đáng tin cậy trong mắt mọi người, " +
            "cùng năng lực xây dựng nền tảng vững chắc.");
        
        strengths.put("THAI_AM",
            "bạn có sự tinh tế và nhạy cảm trong cảm nhận, " +
            "khả năng thấu hiểu và đồng cảm với người khác, " +
            "cùng trực giác sắc bén và chiều sâu nội tâm phong phú.");
        
        strengths.put("THAM_LANG",
            "bạn có tính đa tài và sở thích đa dạng phong phú, " +
            "khả năng thích ứng tốt với nhiều hoàn cảnh khác nhau, " +
            "cùng sức hấp dẫn tự nhiên và khả năng giao tiếp cuốn hút.");
        
        strengths.put("CU_MON",
            "bạn có khả năng diễn đạt và thuyết phục người khác, " +
            "năng lực phân tích sâu sắc các vấn đề, " +
            "cùng sự kiên trì trong việc tìm hiểu đến cùng.");
        
        strengths.put("THIEN_TUONG",
            "bạn có khả năng kết nối mọi người và tạo sự đồng thuận, " +
            "tính cách hòa nhã và có duyên trong giao tiếp, " +
            "cùng năng lực hỗ trợ và làm cầu nối hiệu quả.");
        
        strengths.put("THIEN_LUONG",
            "bạn có lòng nhân ái và khả năng che chở người khác, " +
            "sự đáng tin cậy và tinh thần trách nhiệm cao, " +
            "cùng năng lực hóa giải mâu thuẫn và bảo vệ người thân.");
        
        strengths.put("THAT_SAT",
            "bạn có sự dũng cảm và năng lực hành động mạnh mẽ, " +
            "khả năng đối mặt với thử thách không chùn bước, " +
            "cùng tính quyết đoán và không ngại khó khăn.");
        
        strengths.put("PHA_QUAN",
            "bạn có khả năng đổi mới và sáng tạo không ngừng, " +
            "năng lực phá vỡ giới hạn và vượt qua rào cản, " +
            "cùng sự năng động và không ngại thay đổi để tiến bộ.");
        
        return strengths.getOrDefault(starCode, "bạn có những đặc điểm thuận lợi riêng biệt phù hợp với bản chất của sao.");
    }

    /**
     * Lấy đặc điểm bổ sung của sao (TRUNG LẬP - không tiêu cực).
     * Thay thế cho getStarLimitations với nội dung tích cực hơn.
     */
    private String getStarCharacteristics(String starCode) {
        Map<String, String> characteristics = new HashMap<>();
        
        characteristics.put("TU_VI",
            "Sao Tử Vi giúp bạn có xu hướng tự lập và cách tiếp cận độc đáo. " +
            "Nhờ khả năng tự chủ này, bạn thường có nét riêng biệt trong tính cách.");
        
        characteristics.put("THIEN_CO",
            "Sao Thiên Cơ giúp bạn có tư duy linh hoạt và khả năng thích ứng với nhiều hoàn cảnh. " +
            "Sự nhanh nhẹn trong suy nghĩ giúp bạn có những góc nhìn đa dạng.");
        
        characteristics.put("THAI_DUONG",
            "Sao Thái Dương giúp bạn có năng lượng hướng ngoại và khả năng kết nối với mọi người. " +
            "Sự tỏa sáng tự nhiên này giúp bạn tạo ảnh hưởng tích cực đến môi trường xung quanh.");
        
        characteristics.put("VU_KHUC",
            "Sao Vũ Khúc giúp bạn quyết đoán và tập trung vào kết quả thực tế. " +
            "Năng lực hành động này giúp bạn có những bước tiến cụ thể và rõ ràng.");
        
        characteristics.put("THIEN_DONG",
            "Sao Thiên Đồng giúp bạn hài hòa và có khả năng tạo không gian thoải mái. " +
            "Xu hướng ổn định này giúp bạn tạo ra môi trường an bình cho bản thân và người xung quanh.");
        
        characteristics.put("LIEM_TRINH",
            "Sao Liêm Trinh giúp bạn kiên định và nhiệt huyết trong hành động. " +
            "Tính nguyên tắc này giúp bạn có sự rõ ràng trong định hướng cuộc sống.");
        
        characteristics.put("THIEN_PHU",
            "Sao Thiên Phủ giúp bạn có khả năng quản lý và duy trì sự ổn định. " +
            "Xu hướng bảo tồn này giúp bạn xây dựng nền tảng vững chắc.");
        
        characteristics.put("THAI_AM",
            "Sao Thái Âm giúp bạn tinh tế và có chiều sâu trong cảm nhận. " +
            "Trực giác nhạy bén này giúp bạn thấu hiểu những điều mà người khác không dễ nhận ra.");
        
        characteristics.put("THAM_LANG",
            "Sao Tham Lang giúp bạn đa dạng và khả năng thích ứng với nhiều hoàn cảnh. " +
            "Sức hấp dẫn tự nhiên này giúp bạn tạo ra những kết nối phong phú trong cuộc sống.");
        
        characteristics.put("CU_MON",
            "Sao Cự Môn giúp bạn có khả năng phân tích và diễn đạt sâu sắc. " +
            "Sự kiên trì trong tìm hiểu giúp bạn có chiều sâu kiến thức đáng nể.");
        
        characteristics.put("THIEN_TUONG",
            "Sao Thiên Tướng giúp bạn có khả năng kết nối và hỗ trợ người khác. " +
            "Tính hòa nhã này giúp bạn tạo ra môi trường hợp tác tốt đẹp xung quanh mình.");
        
        characteristics.put("THIEN_LUONG",
            "Sao Thiên Lương giúp bạn có lòng nhân ái và khả năng che chở người thân. " +
            "Tinh thần trách nhiệm tự nhiên này giúp bạn trở thành người đáng tin cậy.");
        
        characteristics.put("THAT_SAT",
            "Sao Thất Sát giúp bạn dũng cảm và có khả năng đối mặt với tình huống mới. " +
            "Năng lực hành động này giúp bạn có những bước tiến táo bạo khi cần thiết.");
        
        characteristics.put("PHA_QUAN",
            "Sao Phá Quân giúp bạn có xu hướng đổi mới và khả năng tạo ra thay đổi. " +
            "Sự năng động này giúp bạn mở ra những hướng đi mới mẻ trong cuộc sống.");
        
        return characteristics.getOrDefault(starCode,
            "Sao này mang đến cho bạn những đặc điểm riêng biệt, " +
            "góp phần làm phong phú thêm các khía cạnh của cung.");
    }

    /**
     * Lấy hạn chế tự nhiên của sao (trình bày trung lập).
     * @deprecated Sử dụng getStarCharacteristics thay thế.
     */
    @Deprecated
    private String getStarLimitations(String starCode) {
        Map<String, String> limitations = new HashMap<>();
        
        limitations.put("TU_VI",
            "Với sao Tử Vi, xu hướng tự chủ đôi khi khiến bạn độc lập quá mức, " +
            "khó ủy thác và chia sẻ trách nhiệm với người khác. " +
            "Khả năng lãnh đạo của bạn có thể đi kèm với kỳ vọng cao từ bản thân và mọi người.");
        
        limitations.put("THIEN_CO",
            "Với sao Thiên Cơ, sự linh hoạt của bạn đôi khi đi kèm với tính thiếu kiên định. " +
            "Khả năng tư duy nhanh có thể khiến bạn thay đổi ý định thường xuyên. " +
            "Năng lực phân tích của bạn đôi khi đi kèm với xu hướng suy nghĩ quá nhiều.");
        
        limitations.put("THAI_DUONG",
            "Với sao Thái Dương, năng lượng hướng ngoại của bạn đôi khi đi kèm với sự tiêu hao nội lực. " +
            "Xu hướng quảng đại có thể khiến bạn quan tâm bên ngoài nhiều hơn bên trong. " +
            "Khả năng tỏa sáng của bạn phụ thuộc vào các điều kiện hỗ trợ.");
        
        limitations.put("VU_KHUC",
            "Với sao Vũ Khúc, sự quyết đoán của bạn đôi khi đi kèm với tính cứng nhắc. " +
            "Xu hướng thực tế có thể khiến bạn ít quan tâm đến yếu tố cảm xúc. " +
            "Năng lực hành động của bạn đôi khi đi kèm với sự thiếu kiên nhẫn.");
        
        limitations.put("THIEN_DONG",
            "Với sao Thiên Đồng, xu hướng tìm kiếm sự thoải mái đôi khi khiến bạn thiếu chủ động. " +
            "Sự hài hòa có thể khiến bạn tránh né những xung đột cần thiết. " +
            "Tính ổn định của bạn đôi khi đi kèm với sự chậm thay đổi.");
        
        limitations.put("LIEM_TRINH",
            "Với sao Liêm Trinh, tính nguyên tắc của bạn đôi khi đi kèm với sự cứng nhắc về quan điểm. " +
            "Sự kiên định có thể khiến bạn khó thỏa hiệp trong một số tình huống. " +
            "Nhiệt huyết của bạn đôi khi đi kèm với xu hướng cực đoan.");
        
        limitations.put("THIEN_PHU",
            "Với sao Thiên Phủ, xu hướng bảo tồn của bạn đôi khi đi kèm với sự thiếu linh hoạt. " +
            "Khả năng quản lý có thể khiến bạn cẩn thận quá mức. " +
            "Sự ổn định của bạn đôi khi đi kèm với xu hướng tránh rủi ro.");
        
        limitations.put("THAI_AM",
            "Với sao Thái Âm, sự nhạy cảm của bạn đôi khi khiến bạn dễ bị ảnh hưởng bởi môi trường. " +
            "Chiều sâu nội tâm có thể khiến bạn có xu hướng nội hướng quá mức. " +
            "Trực giác của bạn đôi khi đi kèm với sự biến động theo cảm xúc.");
        
        limitations.put("THAM_LANG",
            "Với sao Tham Lang, tính đa dạng của bạn đôi khi đi kèm với sự thiếu tập trung. " +
            "Khả năng thích ứng có thể khiến bạn thay đổi mục tiêu thường xuyên. " +
            "Sức hấp dẫn của bạn đôi khi đi kèm với những phức tạp trong quan hệ.");
        
        limitations.put("CU_MON",
            "Với sao Cự Môn, khả năng phân tích của bạn đôi khi đi kèm với xu hướng phê phán. " +
            "Sự kiên trì tìm hiểu có thể khiến bạn đào sâu vào tiểu tiết. " +
            "Năng lực diễn đạt của bạn đôi khi đi kèm với những hiểu lầm trong giao tiếp.");
        
        limitations.put("THIEN_TUONG",
            "Với sao Thiên Tướng, xu hướng hỗ trợ của bạn đôi khi đi kèm với sự phụ thuộc vào người khác. " +
            "Khả năng làm trung gian có thể khiến bạn thiếu lập trường riêng. " +
            "Tính hòa nhã của bạn đôi khi đi kèm với xu hướng nhường nhịn quá mức.");
        
        limitations.put("THIEN_LUONG",
            "Với sao Thiên Lương, lòng nhân ái của bạn đôi khi khiến bạn gánh vác quá nhiều cho người khác. " +
            "Xu hướng che chở có thể khiến bạn lo lắng thái quá. " +
            "Trách nhiệm của bạn đôi khi đi kèm với áp lực tự đặt ra.");
        
        limitations.put("THAT_SAT",
            "Với sao Thất Sát, sự dũng cảm của bạn đôi khi đi kèm với tính bốc đồng trong hành động. " +
            "Năng lực đối mặt thử thách có thể khiến bạn gây ra xung đột không cần thiết. " +
            "Tính quyết đoán của bạn đôi khi đi kèm với sự thiếu cân nhắc kỹ lưỡng.");
        
        limitations.put("PHA_QUAN",
            "Với sao Phá Quân, xu hướng đổi mới của bạn đôi khi đi kèm với sự thiếu ổn định. " +
            "Khả năng phá vỡ giới hạn có thể khiến bạn phá vỡ cả những gì đáng giữ. " +
            "Sự năng động của bạn đôi khi đi kèm với xu hướng bất định.");
        
        return limitations.getOrDefault(starCode,
            "Như mọi yếu tố trong Tử Vi, sao này cũng mang đến cho bạn những đặc điểm cần cân nhắc, " +
            "phản ánh sự cân bằng tự nhiên trong cấu trúc lá số.");
    }

    /**
     * Trích xuất nội dung liên quan từ fragments.
     */
    private List<String> extractRelevantContent(List<InterpretationFragmentEntity> fragments, int limit) {
        return fragments.stream()
                .sorted(Comparator.comparing(InterpretationFragmentEntity::getPriority))
                .limit(limit)
                .map(InterpretationFragmentEntity::getContent)
                .map(this::cleanContent)
                .collect(Collectors.toList());
    }

    /**
     * Làm sạch nội dung - loại bỏ các từ/cụm từ không phù hợp.
     */
    private String cleanContent(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        
        String result = content;
        
        // Loại bỏ các từ cấm
        for (String forbidden : FORBIDDEN_PHRASES) {
            result = result.replaceAll("(?i)\\b" + forbidden + "\\b", "");
        }
        
        // Loại bỏ khoảng trắng thừa
        result = result.replaceAll("\\s+", " ").trim();
        
        // Loại bỏ dấu phẩy liên tiếp
        result = result.replaceAll(",\\s*,", ",");
        
        return result;
    }

    /**
     * Trung hòa nội dung - chuyển các câu tiêu cực thành trung lập.
     */
    private String neutralizeContent(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        
        String result = content;
        
        // Thay thế các từ tiêu cực bằng từ trung lập
        result = result.replaceAll("(?i)xấu", "có đặc điểm riêng");
        result = result.replaceAll("(?i)tệ", "cần cân nhắc");
        result = result.replaceAll("(?i)yếu", "có giới hạn");
        result = result.replaceAll("(?i)kém", "hạn chế");
        result = result.replaceAll("(?i)thất bại", "gặp thử thách");
        result = result.replaceAll("(?i)khó khăn", "thử thách");
        result = result.replaceAll("(?i)nguy hiểm", "cần lưu ý");
        result = result.replaceAll("(?i)đáng sợ", "đáng chú ý");
        
        return result;
    }

    /**
     * Tạo summary từ introduction và synthesis.
     */
    private String createSummary(String introduction, String synthesis) {
        // Lấy câu đầu của introduction và câu cuối của synthesis
        String[] introSentences = introduction.split("\\. ");
        String[] synthSentences = synthesis.split("\\. ");
        
        StringBuilder summary = new StringBuilder();
        if (introSentences.length > 0) {
            summary.append(introSentences[0]).append(". ");
        }
        if (synthSentences.length > 0) {
            summary.append(synthSentences[synthSentences.length - 1]);
            if (!summary.toString().endsWith(".")) {
                summary.append(".");
            }
        }
        
        return summary.toString();
    }

    /**
     * Đếm số từ trong văn bản.
     */
    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    /**
     * Validate bài luận theo các tiêu chuẩn.
     */
    private void validateEssay(PalaceEssay essay) {
        int wordCount = essay.getWordCount();
        
        if (wordCount < MIN_WORDS) {
            log.warn("Essay for palace {} has only {} words (minimum: {})", 
                    essay.getPalaceCode(), wordCount, MIN_WORDS);
        }
        
        if (wordCount > MAX_WORDS) {
            log.warn("Essay for palace {} has {} words (maximum: {})", 
                    essay.getPalaceCode(), wordCount, MAX_WORDS);
        }
        
        // Kiểm tra từ cấm
        String content = essay.getFullEssay().toLowerCase();
        for (String forbidden : FORBIDDEN_PHRASES) {
            if (content.contains(forbidden.toLowerCase())) {
                log.warn("Essay for palace {} contains forbidden phrase: '{}'", 
                        essay.getPalaceCode(), forbidden);
            }
        }
        
        // Kiểm tra có đủ sections không (6 phần chính + introduction)
        if (essay.getSections() == null || essay.getSections().size() < 7) {
            log.warn("Essay for palace {} has only {} sections (expected: 7)", 
                    essay.getPalaceCode(), 
                    essay.getSections() != null ? essay.getSections().size() : 0);
        }
    }
}

package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.CenterInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service để tạo nội dung giải luận phần Tổng quan (Overview) theo quy tắc:
 * - Văn phong: thân thiện, dễ đọc, sử dụng đại từ "bạn"
 * - Thêm từ dẫn "sao", "cung" trước tên riêng
 * - KHÔNG: lời khuyên, phán số phận, đánh giá tiêu cực
 * - Mô tả xu hướng và tiềm năng, không kết luận định mệnh
 * 
 * Áp dụng cho: Chủ mệnh, Chủ thân, Bản mệnh, Cục mệnh, Thuận Nghịch, Thân cư, Tổng kết
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverviewEssayCompositionService {

    // Các từ/cụm từ bị cấm
    private static final List<String> FORBIDDEN_PHRASES = Arrays.asList(
        "cần phải", "bắt buộc", "hãy", "tôi khuyên", "lời khuyên",
        "chắc chắn sẽ", "cuộc đời sẽ", "số phận", "định mệnh",
        "chết", "yểu mệnh", "giàu có", "nghèo khổ",
        "thất bại", "thành công vĩ đại", "khó khăn",
        "ở mức trung bình", "vừa phải", "không quá nổi bật"
    );

    /**
     * Tạo giải luận Chủ Mệnh theo văn phong thân thiện.
     */
    public String composeChuMenhInterpretation(String chuMenh, String brightness, boolean menhKhongChinhTinh) {
        if (chuMenh == null || chuMenh.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        // Xử lý trường hợp Mệnh vô Chính tinh
        if (menhKhongChinhTinh) {
            interpretation.append("Điểm đặc biệt là cung Mệnh không có Chính tinh tọa thủ. ");
            interpretation.append("Trong trường hợp này, Chủ mệnh được xác định từ cung đối diện ");
            interpretation.append("hoặc từ cung Thân theo nguyên tắc Tử Vi. ");
        }

        // Mô tả bản chất Chủ mệnh - thân thiện hơn
        interpretation.append(String.format("Sao %s đảm nhận vai trò Chủ mệnh. ", chuMenh));
        interpretation.append("Đây là sao đại diện cho nền tảng tính cách và định hướng phát triển của bạn. ");
        
        // Thêm mô tả theo sao cụ thể
        interpretation.append(getChuMenhDescription(chuMenh));
        
        // Thêm mô tả theo mức độ sáng (nếu có)
        if (brightness != null && !brightness.isBlank()) {
            interpretation.append(" ");
            interpretation.append(getBrightnessInfluence(chuMenh, brightness, "Chủ mệnh"));
        }

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo giải luận Chủ Thân theo văn phong thân thiện.
     */
    public String composeChuThanInterpretation(String chuThan, String brightness) {
        if (chuThan == null || chuThan.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        interpretation.append(String.format("Về phần Chủ thân, sao %s đảm nhận vai trò này. ", chuThan));
        interpretation.append("Sao này phản ánh cách bạn thể hiện ra bên ngoài và hình ảnh của bạn trong mắt người khác. ");
        
        // Thêm mô tả theo sao cụ thể
        interpretation.append(getChuThanDescription(chuThan));
        
        // Thêm mô tả theo mức độ sáng (nếu có)
        if (brightness != null && !brightness.isBlank()) {
            interpretation.append(" ");
            interpretation.append(getBrightnessInfluence(chuThan, brightness, "Chủ thân"));
        }

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo giải luận Bản Mệnh (Nạp Âm) theo văn phong thân thiện.
     */
    public String composeBanMenhInterpretation(String banMenh, String nguHanh) {
        if (banMenh == null || banMenh.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        interpretation.append(String.format("Bản mệnh của bạn thuộc %s", banMenh));
        
        if (nguHanh != null && !nguHanh.isBlank()) {
            interpretation.append(String.format(", thuộc hành %s. ", nguHanh));
            interpretation.append(getNguHanhCharacteristics(nguHanh));
        } else {
            interpretation.append(". ");
        }
        
        interpretation.append(" Đây là yếu tố nền tảng, góp phần định hình xu hướng tự nhiên trong cuộc sống của bạn.");

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo giải luận Cục Mệnh theo văn phong thân thiện.
     */
    public String composeCucInterpretation(String cucName, Integer cucValue, String menhCucRelation) {
        if (cucName == null || cucName.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        interpretation.append(String.format("Cục mệnh của bạn là %s", cucName));
        if (cucValue != null) {
            interpretation.append(String.format(" (cục số %d)", cucValue));
        }
        interpretation.append(". ");
        
        // Mô tả đặc điểm cục
        interpretation.append(getCucCharacteristics(cucName));
        
        // Mối quan hệ Mệnh - Cục (nếu có)
        if (menhCucRelation != null && !menhCucRelation.isBlank()) {
            interpretation.append(" ");
            interpretation.append(getMenhCucRelationDescription(menhCucRelation));
        }

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo giải luận Thuận Nghịch theo văn phong thân thiện.
     */
    public String composeThuanNghichInterpretation(String thuanNghich) {
        if (thuanNghich == null || thuanNghich.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        if (thuanNghich.contains("Thuận")) {
            interpretation.append("Lá số của bạn vận hành theo chiều Thuận. ");
            interpretation.append("Điều này có nghĩa là các cung và đại vận sẽ diễn tiến theo hướng tự nhiên. ");
            interpretation.append("Với đặc điểm này, bạn có xu hướng phát triển thuận theo dòng chảy, ");
            interpretation.append("với nhịp độ ổn định và mang tính kế thừa.");
        } else if (thuanNghich.contains("Nghịch")) {
            interpretation.append("Lá số của bạn vận hành theo chiều Nghịch. ");
            interpretation.append("Điều này có nghĩa là các cung và đại vận sẽ diễn tiến theo hướng ngược lại. ");
            interpretation.append("Với đặc điểm này, bạn có xu hướng phát triển độc lập, ");
            interpretation.append("với cách tiếp cận riêng biệt và không theo khuôn mẫu thông thường.");
        }

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo giải luận Thân Cư (Lai nhân) theo văn phong thân thiện.
     */
    public String composeThanCuInterpretation(String thanCu, boolean thanMenhDongCung) {
        if (thanCu == null || thanCu.isBlank()) {
            return "";
        }

        StringBuilder interpretation = new StringBuilder();

        interpretation.append(String.format("Thân cư tại cung %s", thanCu));
        
        if (thanMenhDongCung) {
            interpretation.append(", đồng cung với Mệnh. ");
            interpretation.append("Điều này cho thấy ở bạn có sự thống nhất giữa bản chất bên trong và biểu hiện bên ngoài, ");
            interpretation.append("giúp bạn thể hiện bản thân một cách nhất quán và tự nhiên.");
        } else {
            interpretation.append(", tách biệt với cung Mệnh. ");
            interpretation.append("Điều này cho thấy ở bạn có sự đa dạng giữa bản chất nội tâm và hình ảnh bên ngoài, ");
            interpretation.append("mang đến cho bạn chiều sâu và nhiều khía cạnh phong phú trong tính cách.");
        }
        
        // Thêm mô tả theo cung cụ thể
        interpretation.append(" ");
        interpretation.append(getThanCuPalaceDescription(thanCu));

        return cleanContent(interpretation.toString());
    }

    /**
     * Tạo Tổng kết (Overall Summary) theo văn phong thân thiện - TRUNG LẬP, KHÔNG TIÊU CỰC.
     */
    public String composeOverallSummary(CenterInfo center, TuViChartResponse chart) {
        if (center == null) {
            return "Lá số của bạn phản ánh một cấu trúc độc đáo với những tiềm năng riêng biệt.";
        }

        StringBuilder summary = new StringBuilder();

        // Mở đầu thân thiện
        summary.append("Nhìn tổng thể, lá số của bạn cho thấy một cấu trúc ");
        
        // Mô tả dựa trên các yếu tố chính
        List<String> characteristics = new ArrayList<>();
        
        if (center.getChuMenh() != null) {
            characteristics.add(String.format("với sao %s làm Chủ mệnh", center.getChuMenh()));
        }
        
        if (center.getBanMenh() != null) {
            characteristics.add(String.format("bản mệnh %s", center.getBanMenh()));
        }
        
        if (center.getCuc() != null) {
            characteristics.add(String.format("và %s", center.getCuc()));
        }
        
        if (!characteristics.isEmpty()) {
            summary.append(String.join(", ", characteristics));
            summary.append(". ");
        } else {
            summary.append("với những đặc điểm riêng biệt. ");
        }

        // Mô tả xu hướng tổng thể - THÂN THIỆN
        summary.append("Các yếu tố tương tác với nhau, ");
        summary.append("cùng nhau vẽ nên một bức tranh phản ánh tiềm năng và xu hướng phát triển của bạn. ");
        
        // Kết luận thân thiện - KHÔNG PHÁN ĐOÁN
        summary.append("Đây là những đặc điểm riêng, ");
        summary.append("thể hiện các xu hướng tự nhiên trong hành trình cuộc sống. ");
        summary.append("Mỗi yếu tố đều mang những đặc trưng riêng, ");
        summary.append("góp phần làm phong phú thêm bức tranh tổng thể về con người bạn.");

        return cleanContent(summary.toString());
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lấy mô tả Chủ mệnh theo sao cụ thể - văn phong thân thiện với "bạn".
     */
    private String getChuMenhDescription(String starName) {
        Map<String, String> descriptions = new HashMap<>();
        
        descriptions.put("Tử Vi", 
            "Với sao Tử Vi làm Chủ mệnh, bạn có xu hướng tự chủ, " +
            "có tầm nhìn xa và khả năng định hướng tốt. " +
            "Bạn thường có ý thức về vị trí của mình và có xu hướng đảm nhận vai trò trung tâm trong các hoạt động.");
        
        descriptions.put("Thiên Cơ",
            "Với sao Thiên Cơ làm Chủ mệnh, bạn có xu hướng linh hoạt, " +
            "có khả năng tư duy và phân tích tốt. " +
            "Bạn thường thích ứng nhanh với hoàn cảnh và có năng lực giải quyết vấn đề hiệu quả.");
        
        descriptions.put("Thái Dương",
            "Với sao Thái Dương làm Chủ mệnh, bạn có xu hướng cởi mở, " +
            "có năng lượng hướng ngoại và khả năng tỏa sáng. " +
            "Bạn thường nhiệt tình, quảng đại và hay quan tâm đến lợi ích chung của mọi người.");
        
        descriptions.put("Vũ Khúc",
            "Với sao Vũ Khúc làm Chủ mệnh, bạn có xu hướng quyết đoán, " +
            "có năng lực hành động và tính thực tế cao. " +
            "Bạn thường hướng đến kết quả cụ thể và có khả năng xử lý tốt các vấn đề liên quan đến vật chất.");
        
        descriptions.put("Thiên Đồng",
            "Với sao Thiên Đồng làm Chủ mệnh, bạn có xu hướng hài hòa, " +
            "ổn định và thích tìm kiếm sự thoải mái. " +
            "Bạn thường dễ chịu và có khả năng tạo môi trường thanh thản cho bản thân cũng như người xung quanh.");
        
        descriptions.put("Liêm Trinh",
            "Với sao Liêm Trinh làm Chủ mệnh, bạn có xu hướng chính trực, " +
            "có nguyên tắc và nhiệt huyết. " +
            "Bạn thường kiên định với lập trường của mình và có tiêu chuẩn cao trong cuộc sống.");
        
        descriptions.put("Thiên Phủ",
            "Với sao Thiên Phủ làm Chủ mệnh, bạn có xu hướng ổn định, " +
            "có khả năng quản lý và bảo tồn tốt. " +
            "Bạn thường đáng tin cậy và có năng lực xây dựng nền tảng vững chắc cho bản thân.");
        
        descriptions.put("Thái Âm",
            "Với sao Thái Âm làm Chủ mệnh, bạn có xu hướng tinh tế, " +
            "nhạy cảm và có chiều sâu nội tâm phong phú. " +
            "Bạn thường có khả năng thấu hiểu và đồng cảm với người khác một cách tự nhiên.");
        
        descriptions.put("Tham Lang",
            "Với sao Tham Lang làm Chủ mệnh, bạn có xu hướng đa dạng, " +
            "có nhiều sở thích và khả năng thích ứng linh hoạt. " +
            "Bạn thường đa tài, có sức hấp dẫn tự nhiên và thích trải nghiệm nhiều mặt của cuộc sống.");
        
        descriptions.put("Cự Môn",
            "Với sao Cự Môn làm Chủ mệnh, bạn có xu hướng phân tích, " +
            "có khả năng diễn đạt và thuyết phục tốt. " +
            "Bạn thường sắc bén về ngôn ngữ và thích đi sâu tìm hiểu các vấn đề.");
        
        descriptions.put("Thiên Tướng",
            "Với sao Thiên Tướng làm Chủ mệnh, bạn có xu hướng hỗ trợ, " +
            "có khả năng kết nối và tạo sự cân bằng trong các mối quan hệ. " +
            "Bạn thường hòa nhã, có duyên và hay giúp đỡ người khác.");
        
        descriptions.put("Thiên Lương",
            "Với sao Thiên Lương làm Chủ mệnh, bạn có xu hướng che chở, " +
            "có lòng nhân ái và tinh thần trách nhiệm cao. " +
            "Bạn thường đáng tin cậy và có khả năng hóa giải, bảo vệ cho những người xung quanh.");
        
        descriptions.put("Thất Sát",
            "Với sao Thất Sát làm Chủ mệnh, bạn có xu hướng quyết đoán, " +
            "có năng lực hành động và sự dũng cảm. " +
            "Bạn thường không ngại đối mặt với thử thách và có khả năng vượt qua các trở ngại.");
        
        descriptions.put("Phá Quân",
            "Với sao Phá Quân làm Chủ mệnh, bạn có xu hướng đổi mới, " +
            "có khả năng phá vỡ giới hạn và tinh thần sáng tạo. " +
            "Bạn thường năng động, không ngại thay đổi và thích tìm tòi những điều mới mẻ.");
        
        return descriptions.getOrDefault(starName,
            String.format("Với sao %s làm Chủ mệnh, bạn có những đặc trưng riêng biệt, " +
                         "phản ánh xu hướng và tiềm năng phát triển cá nhân của mình.", starName));
    }

    /**
     * Lấy mô tả Chủ thân theo sao cụ thể - văn phong thân thiện với "bạn".
     */
    private String getChuThanDescription(String starName) {
        Map<String, String> descriptions = new HashMap<>();
        
        descriptions.put("Tử Vi",
            "Với sao Tử Vi làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự tự tin, có uy và dễ được người khác tôn trọng. " +
            "Cách giao tiếp của bạn thường điềm đạm và có chủ kiến rõ ràng.");
        
        descriptions.put("Thiên Cơ",
            "Với sao Thiên Cơ làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự nhanh nhẹn, linh hoạt và thông minh. " +
            "Cách giao tiếp của bạn thường uyển chuyển và có thể thay đổi linh hoạt theo tình huống.");
        
        descriptions.put("Thái Dương",
            "Với sao Thái Dương làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự cởi mở, nhiệt tình và có sức lan tỏa. " +
            "Cách giao tiếp của bạn thường quảng đại và hướng ngoại.");
        
        descriptions.put("Vũ Khúc",
            "Với sao Vũ Khúc làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự cương quyết, thực tế và nghiêm túc. " +
            "Cách giao tiếp của bạn thường thẳng thắn và hướng đến kết quả.");
        
        descriptions.put("Thiên Đồng",
            "Với sao Thiên Đồng làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự hiền hòa, dễ gần và thoải mái. " +
            "Cách giao tiếp của bạn thường nhẹ nhàng và không tạo áp lực cho người khác.");
        
        descriptions.put("Liêm Trinh",
            "Với sao Liêm Trinh làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự nghiêm túc, có nguyên tắc và đam mê. " +
            "Cách giao tiếp của bạn thường rõ ràng về lập trường và quan điểm.");
        
        descriptions.put("Thiên Phủ",
            "Với sao Thiên Phủ làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự ổn định, đáng tin và đầy đặn. " +
            "Cách giao tiếp của bạn thường điềm tĩnh và có trách nhiệm.");
        
        descriptions.put("Thái Âm",
            "Với sao Thái Âm làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự tinh tế, nhẹ nhàng và sâu lắng. " +
            "Cách giao tiếp của bạn thường nhạy cảm và quan tâm đến cảm xúc của người khác.");
        
        descriptions.put("Tham Lang",
            "Với sao Tham Lang làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự hấp dẫn, đa dạng và năng động. " +
            "Cách giao tiếp của bạn thường linh hoạt và có sức cuốn hút tự nhiên.");
        
        descriptions.put("Cự Môn",
            "Với sao Cự Môn làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự sắc sảo, hay phân tích và quan sát. " +
            "Cách giao tiếp của bạn thường kỹ lưỡng và hay đặt câu hỏi để tìm hiểu.");
        
        descriptions.put("Thiên Tướng",
            "Với sao Thiên Tướng làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự hòa nhã, có duyên và dễ được tin cậy. " +
            "Cách giao tiếp của bạn thường mang tính hỗ trợ và kết nối mọi người.");
        
        descriptions.put("Thiên Lương",
            "Với sao Thiên Lương làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự đáng tin, có vẻ che chở và nhân hậu. " +
            "Cách giao tiếp của bạn thường quan tâm và mang tính bảo vệ người khác.");
        
        descriptions.put("Thất Sát",
            "Với sao Thất Sát làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự quyết đoán, mạnh mẽ và dứt khoát. " +
            "Cách giao tiếp của bạn thường trực tiếp và không vòng vo.");
        
        descriptions.put("Phá Quân",
            "Với sao Phá Quân làm Chủ thân, hình ảnh bên ngoài của bạn thể hiện " +
            "sự năng động, hay thay đổi và độc đáo. " +
            "Cách giao tiếp của bạn thường không theo khuôn mẫu và mang nét cá tính riêng.");
        
        return descriptions.getOrDefault(starName,
            String.format("Với sao %s làm Chủ thân, hình ảnh và cách giao tiếp của bạn " +
                         "phản ánh những đặc trưng riêng biệt của sao này.", starName));
    }

    /**
     * Lấy đặc điểm Ngũ Hành - văn phong thân thiện với "bạn".
     */
    private String getNguHanhCharacteristics(String nguHanh) {
        Map<String, String> characteristics = new HashMap<>();
        
        characteristics.put("KIM",
            "Hành Kim mang đến cho bạn đặc tính quyết đoán, cứng cỏi và có xu hướng rõ ràng. " +
            "Điều này thể hiện qua tính cách thẳng thắn và khả năng hướng đến mục tiêu cụ thể của bạn.");
        
        characteristics.put("MOC",
            "Hành Mộc mang đến cho bạn đặc tính sinh trưởng, linh hoạt và có xu hướng phát triển. " +
            "Điều này thể hiện qua khả năng thích ứng và tinh thần học hỏi của bạn.");
        
        characteristics.put("THUY",
            "Hành Thủy mang đến cho bạn đặc tính uyển chuyển, sâu sắc và có xu hướng thích nghi. " +
            "Điều này thể hiện qua sự nhạy bén và khả năng thấu hiểu của bạn.");
        
        characteristics.put("HOA",
            "Hành Hỏa mang đến cho bạn đặc tính nhiệt tình, năng động và có xu hướng tỏa sáng. " +
            "Điều này thể hiện qua sự sôi nổi và khả năng lan tỏa ảnh hưởng của bạn.");
        
        characteristics.put("THO",
            "Hành Thổ mang đến cho bạn đặc tính ổn định, chắc chắn và có xu hướng bền vững. " +
            "Điều này thể hiện qua sự đáng tin cậy và khả năng duy trì của bạn.");
        
        return characteristics.getOrDefault(nguHanh.toUpperCase(),
            "Ngũ hành này mang đến cho bạn những đặc tính riêng, ảnh hưởng đến xu hướng tự nhiên trong cuộc sống.");
    }

    /**
     * Lấy đặc điểm Cục - văn phong thân thiện với "bạn".
     */
    private String getCucCharacteristics(String cucName) {
        Map<String, String> characteristics = new HashMap<>();
        
        characteristics.put("Kim tứ cục",
            "Cục này mang đến cho bạn đặc tính quyết đoán và rõ ràng, " +
            "ảnh hưởng đến cách bạn phát triển và hành động trong cuộc sống.");
        
        characteristics.put("Mộc tam cục",
            "Cục này mang đến cho bạn đặc tính sinh trưởng và linh hoạt, " +
            "ảnh hưởng đến khả năng phát triển và mở rộng của bạn.");
        
        characteristics.put("Thủy nhị cục",
            "Cục này mang đến cho bạn đặc tính uyển chuyển và sâu sắc, " +
            "ảnh hưởng đến khả năng thích nghi và thấu hiểu của bạn.");
        
        characteristics.put("Hỏa lục cục",
            "Cục này mang đến cho bạn đặc tính năng động và nhiệt tình, " +
            "ảnh hưởng đến sự sôi nổi và khả năng lan tỏa của bạn.");
        
        characteristics.put("Thổ ngũ cục",
            "Cục này mang đến cho bạn đặc tính ổn định và bền vững, " +
            "ảnh hưởng đến sự chắc chắn và khả năng duy trì của bạn.");
        
        // Handle partial matches
        for (Map.Entry<String, String> entry : characteristics.entrySet()) {
            if (cucName.contains(entry.getKey().split(" ")[0])) {
                return entry.getValue();
            }
        }
        
        return "Cục này mang đến cho bạn những đặc tính riêng, ảnh hưởng đến nhịp độ và cách thức phát triển của bạn.";
    }

    /**
     * Lấy mô tả mối quan hệ Mệnh - Cục - văn phong thân thiện với "bạn".
     */
    private String getMenhCucRelationDescription(String relation) {
        Map<String, String> descriptions = new HashMap<>();
        
        descriptions.put("TUONG_SINH",
            "Mối quan hệ tương sinh giữa Mệnh và Cục tạo ra sự hỗ trợ tự nhiên, " +
            "các yếu tố bổ sung và thúc đẩy lẫn nhau trong quá trình phát triển.");
        
        descriptions.put("TUONG_KHAC",
            "Mối quan hệ tương khắc giữa Mệnh và Cục tạo ra động lực chuyển đổi, " +
            "đòi hỏi sự cân bằng và điều chỉnh trong quá trình phát triển.");
        
        descriptions.put("BINH_HOA",
            "Mối quan hệ bình hòa giữa Mệnh và Cục tạo ra sự ổn định, " +
            "các yếu tố tồn tại song song và hài hòa với nhau.");
        
        return descriptions.getOrDefault(relation,
            "Mối quan hệ giữa Mệnh và Cục tạo ra một cấu trúc riêng biệt.");
    }

    /**
     * Lấy mô tả ảnh hưởng của mức độ sáng - văn phong thân thiện với "bạn".
     */
    private String getBrightnessInfluence(String starName, String brightness, String role) {
        switch (brightness.toUpperCase()) {
            case "MIEU":
                return String.format("Đặc biệt, sao %s với vai trò %s đang ở trạng thái miếu địa, " +
                                   "giúp bạn phát huy đầy đủ các đặc tính với mức độ biểu hiện trọn vẹn và ổn định.", starName, role);
            case "VUONG":
                return String.format("Đặc biệt, sao %s với vai trò %s đang ở trạng thái vượng địa, " +
                                   "giúp bạn thể hiện các đặc tính một cách rõ nét và tích cực.", starName, role);
            case "DAC":
                return String.format("Đặc biệt, sao %s với vai trò %s đang ở trạng thái đắc địa, " +
                                   "giúp bạn phát huy các đặc tính một cách hợp lý và cân bằng.", starName, role);
            case "BINH":
                return String.format("Với sao %s ở trạng thái bình địa, vai trò %s " +
                                   "thể hiện các đặc tính vừa phải, với mức độ biểu hiện linh hoạt.", starName, role);
            case "HAM":
                return String.format("Với sao %s ở trạng thái hãm địa, vai trò %s " +
                                   "có thể cần thời gian để phát huy các đặc tính, với mức độ biểu hiện tiềm ẩn.", starName, role);
            default:
                return "";
        }
    }

    /**
     * Lấy mô tả Thân cư theo cung - văn phong thân thiện với "bạn".
     */
    private String getThanCuPalaceDescription(String palace) {
        Map<String, String> descriptions = new HashMap<>();
        
        descriptions.put("Mệnh", "Với Thân cư tại cung Mệnh, bạn có sự thống nhất giữa nội tâm và biểu hiện bên ngoài.");
        descriptions.put("Phụ Mẫu", "Với Thân cư tại cung Phụ Mẫu, biểu hiện của bạn có mối liên kết chặt chẽ với các mối quan hệ gia đình.");
        descriptions.put("Phúc Đức", "Với Thân cư tại cung Phúc Đức, biểu hiện của bạn có mối liên kết với đời sống tinh thần và phúc phần.");
        descriptions.put("Điền Trạch", "Với Thân cư tại cung Điền Trạch, biểu hiện của bạn có mối liên kết với vấn đề nhà đất và tài sản.");
        descriptions.put("Quan Lộc", "Với Thân cư tại cung Quan Lộc, biểu hiện của bạn có mối liên kết chặt chẽ với lĩnh vực sự nghiệp.");
        descriptions.put("Nô Bộc", "Với Thân cư tại cung Nô Bộc, biểu hiện của bạn có mối liên kết với các mối quan hệ xã hội và bạn bè.");
        descriptions.put("Thiên Di", "Với Thân cư tại cung Thiên Di, biểu hiện của bạn có mối liên kết với môi trường bên ngoài và sự di chuyển.");
        descriptions.put("Tật Ách", "Với Thân cư tại cung Tật Ách, biểu hiện của bạn có mối liên kết với vấn đề sức khỏe.");
        descriptions.put("Tài Bạch", "Với Thân cư tại cung Tài Bạch, biểu hiện của bạn có mối liên kết chặt chẽ với lĩnh vực tài chính.");
        descriptions.put("Tử Tức", "Với Thân cư tại cung Tử Tức, biểu hiện của bạn có mối liên kết với vấn đề con cái và sáng tạo.");
        descriptions.put("Phu Thê", "Với Thân cư tại cung Phu Thê, biểu hiện của bạn có mối liên kết với các mối quan hệ tình cảm và hôn nhân.");
        descriptions.put("Huynh Đệ", "Với Thân cư tại cung Huynh Đệ, biểu hiện của bạn có mối liên kết với mối quan hệ anh chị em và bạn bè thân thiết.");
        
        return descriptions.getOrDefault(palace, 
            "Vị trí Thân cư này tạo ra mối liên kết giữa biểu hiện bên ngoài của bạn và lĩnh vực tương ứng.");
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
}

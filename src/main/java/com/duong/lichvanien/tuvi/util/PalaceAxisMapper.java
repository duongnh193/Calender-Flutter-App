package com.duong.lichvanien.tuvi.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class để map palace code sang trục tương ứng và các thông tin mô tả.
 * Mỗi cung trong lá số Tử Vi có một trục ý nghĩa riêng biệt.
 */
public class PalaceAxisMapper {

    /**
     * Enum định nghĩa thông tin chi tiết cho từng cung.
     */
    @Getter
    public enum PalaceAxis {
        MENH(
            "Mệnh",
            "bản chất con người, tính cách, nền tảng cuộc đời",
            "Cung Mệnh là cung quan trọng nhất trong lá số Tử Vi, đại diện cho bản chất con người, " +
            "tính cách bẩm sinh và nền tảng cuộc đời. Đây là cung phản ánh cốt lõi của mệnh tạo, " +
            "thể hiện những đặc điểm cơ bản nhất mà người đó mang theo từ khi sinh ra. " +
            "Cung Mệnh không chỉ nói lên tính cách mà còn cho thấy tiềm năng, xu hướng phát triển " +
            "và cách thức mà mệnh tạo tương tác với thế giới xung quanh.",
            "Cung Mệnh ảnh hưởng đến mọi khía cạnh của cuộc sống, từ tính cách, sức khỏe tinh thần, " +
            "đến cách nhìn nhận cuộc đời và phản ứng trước các tình huống. Đây là điểm khởi đầu " +
            "để hiểu toàn bộ lá số, bởi tất cả các cung khác đều liên kết và tương tác với cung Mệnh."
        ),
        
        PHU_MAU(
            "Phụ Mẫu",
            "cha mẹ, nguồn gốc, di sản, mối quan hệ với bậc sinh thành",
            "Cung Phụ Mẫu phản ánh mối quan hệ giữa mệnh tạo với cha mẹ, ông bà và những người " +
            "thuộc thế hệ trước. Đây là cung cho thấy nguồn gốc gia đình, di sản được thừa hưởng " +
            "và ảnh hưởng của gia đình đối với sự phát triển của mệnh tạo.",
            "Cung này ảnh hưởng đến cách mệnh tạo nhận được sự nuôi dưỡng, giáo dục và hỗ trợ " +
            "từ gia đình. Nó cũng phản ánh những giá trị, truyền thống được truyền lại qua các thế hệ."
        ),
        
        PHUC_DUC(
            "Phúc Đức",
            "phúc phần, phước đức, di sản tinh thần, tâm linh",
            "Cung Phúc Đức đại diện cho phúc phần, phước đức tích lũy và di sản tinh thần " +
            "của mệnh tạo. Đây là cung phản ánh những gì được thừa hưởng từ tổ tiên về mặt " +
            "tinh thần, cũng như khả năng tích phúc và hưởng phúc trong cuộc đời.",
            "Cung này ảnh hưởng đến đời sống tâm linh, sự bình an nội tâm và khả năng " +
            "tìm thấy ý nghĩa sâu sắc trong cuộc sống. Nó cũng liên quan đến sự may mắn " +
            "và những cơ hội đến một cách tự nhiên."
        ),
        
        DIEN_TRACH(
            "Điền Trạch",
            "nhà cửa, bất động sản, tài sản cố định, nơi cư ngụ",
            "Cung Điền Trạch phản ánh vấn đề nhà cửa, đất đai, bất động sản và tài sản cố định " +
            "của mệnh tạo. Đây là cung cho thấy khả năng tích lũy tài sản, sở hữu nhà đất " +
            "và sự ổn định về nơi cư ngụ.",
            "Cung này ảnh hưởng đến môi trường sống, không gian sinh hoạt và cảm giác " +
            "thuộc về một nơi chốn. Nó cũng liên quan đến di sản vật chất được thừa kế " +
            "hoặc để lại cho thế hệ sau."
        ),
        
        QUAN_LOC(
            "Quan Lộc",
            "sự nghiệp, công danh, địa vị xã hội, thành tựu nghề nghiệp",
            "Cung Quan Lộc là cung của sự nghiệp, công danh và địa vị xã hội. Đây là cung " +
            "phản ánh con đường nghề nghiệp, khả năng thăng tiến và thành tựu trong công việc " +
            "của mệnh tạo. Cung này cho thấy cách mệnh tạo tương tác với môi trường làm việc " +
            "và xây dựng vị thế trong xã hội.",
            "Cung Quan Lộc ảnh hưởng đến lựa chọn nghề nghiệp, phong cách làm việc, " +
            "khả năng lãnh đạo và mức độ thành công trong sự nghiệp. Đây là cung quan trọng " +
            "để đánh giá tiềm năng phát triển nghề nghiệp và vị trí xã hội."
        ),
        
        NO_BOC(
            "Nô Bộc",
            "bạn bè, đồng nghiệp, cấp dưới, mối quan hệ xã hội",
            "Cung Nô Bộc phản ánh mối quan hệ với bạn bè, đồng nghiệp, cấp dưới và những người " +
            "hỗ trợ trong cuộc sống. Đây là cung cho thấy khả năng xây dựng và duy trì các mối " +
            "quan hệ xã hội, cũng như sự hỗ trợ mà mệnh tạo nhận được từ người khác.",
            "Cung này ảnh hưởng đến chất lượng các mối quan hệ xã hội, khả năng làm việc nhóm " +
            "và sự hợp tác với người khác. Nó cũng phản ánh cách mệnh tạo đối xử với những người " +
            "xung quanh và ngược lại."
        ),
        
        THIEN_DI(
            "Thiên Di",
            "di chuyển, thay đổi, môi trường bên ngoài, giao tiếp xã hội",
            "Cung Thiên Di đại diện cho việc di chuyển, thay đổi môi trường và các hoạt động " +
            "bên ngoài nhà. Đây là cung phản ánh cách mệnh tạo tương tác với thế giới bên ngoài, " +
            "khả năng thích ứng với môi trường mới và những trải nghiệm khi xa nhà.",
            "Cung này ảnh hưởng đến việc đi lại, du lịch, làm việc xa nhà và các mối quan hệ " +
            "với người lạ. Nó cũng cho thấy khả năng mở rộng tầm nhìn và tiếp thu những điều mới."
        ),
        
        TAT_ACH(
            "Tật Ách",
            "sức khỏe, bệnh tật, thể chất, những thử thách về sức khỏe",
            "Cung Tật Ách phản ánh tình trạng sức khỏe, thể chất và những vấn đề về bệnh tật " +
            "của mệnh tạo. Đây là cung cho thấy xu hướng về sức khỏe, những điểm yếu cần chú ý " +
            "và khả năng hồi phục khi gặp vấn đề về thể chất.",
            "Cung này ảnh hưởng đến thể trạng chung, sức đề kháng và những bệnh tật có thể gặp phải. " +
            "Nó cũng liên quan đến cách mệnh tạo chăm sóc sức khỏe và đối mặt với những thử thách về thể chất."
        ),
        
        TAI_BACH(
            "Tài Bạch",
            "tiền bạc, tài chính, thu nhập, khả năng kiếm tiền và quản lý tài sản",
            "Cung Tài Bạch là cung của tiền bạc, tài chính và khả năng kiếm tiền. Đây là cung " +
            "phản ánh nguồn thu nhập, cách thức kiếm tiền và khả năng quản lý tài sản của mệnh tạo. " +
            "Cung này cho thấy mối quan hệ của mệnh tạo với tiền bạc và vật chất.",
            "Cung Tài Bạch ảnh hưởng đến khả năng tạo ra thu nhập, tích lũy tài sản và " +
            "thái độ đối với tiền bạc. Đây là cung quan trọng để đánh giá tiềm năng tài chính " +
            "và cách thức mệnh tạo xử lý các vấn đề liên quan đến vật chất."
        ),
        
        TU_TUC(
            "Tử Tức",
            "con cái, hậu duệ, sáng tạo, những gì mình tạo ra",
            "Cung Tử Tức đại diện cho con cái, hậu duệ và khả năng sinh sản. Đây là cung " +
            "phản ánh mối quan hệ với con cái, số lượng con và đặc điểm của thế hệ sau. " +
            "Cung này cũng liên quan đến khả năng sáng tạo và những gì mệnh tạo để lại.",
            "Cung này ảnh hưởng đến khả năng có con, mối quan hệ với con cái và niềm vui " +
            "từ thế hệ sau. Nó cũng phản ánh khả năng sáng tạo, tạo ra những điều mới " +
            "và di sản để lại cho tương lai."
        ),
        
        PHU_THE(
            "Phu Thê",
            "hôn nhân, vợ chồng, đối tác, mối quan hệ thân mật",
            "Cung Phu Thê là cung của hôn nhân, vợ chồng và các mối quan hệ đối tác. " +
            "Đây là cung phản ánh đời sống hôn nhân, đặc điểm của người bạn đời " +
            "và chất lượng các mối quan hệ thân mật của mệnh tạo.",
            "Cung Phu Thê ảnh hưởng đến việc lựa chọn bạn đời, cách thức duy trì hôn nhân " +
            "và mức độ hài hòa trong các mối quan hệ đối tác. Đây là cung quan trọng " +
            "để hiểu về đời sống tình cảm và khả năng xây dựng mối quan hệ lâu dài."
        ),
        
        HUYNH_DE(
            "Huynh Đệ",
            "anh chị em, bạn bè thân thiết, mối quan hệ ngang hàng",
            "Cung Huynh Đệ phản ánh mối quan hệ với anh chị em ruột, bạn bè thân thiết " +
            "và những người ngang hàng. Đây là cung cho thấy sự hỗ trợ, cạnh tranh " +
            "và tương tác với những người cùng thế hệ.",
            "Cung này ảnh hưởng đến số lượng và chất lượng mối quan hệ với anh chị em, " +
            "khả năng hợp tác và sự hỗ trợ lẫn nhau. Nó cũng phản ánh vị trí của mệnh tạo " +
            "trong gia đình và nhóm bạn bè."
        );

        private final String name;
        private final String axis;
        private final String introduction;
        private final String impact;

        PalaceAxis(String name, String axis, String introduction, String impact) {
            this.name = name;
            this.axis = axis;
            this.introduction = introduction;
            this.impact = impact;
        }
    }

    private static final Map<String, PalaceAxis> PALACE_MAP = new HashMap<>();

    static {
        for (PalaceAxis axis : PalaceAxis.values()) {
            PALACE_MAP.put(axis.name(), axis);
        }
    }

    /**
     * Lấy thông tin cung theo palace code.
     */
    public static PalaceAxis getPalaceAxis(String palaceCode) {
        return PALACE_MAP.get(palaceCode);
    }

    /**
     * Lấy tên tiếng Việt của cung.
     */
    public static String getPalaceName(String palaceCode) {
        PalaceAxis axis = PALACE_MAP.get(palaceCode);
        return axis != null ? axis.getName() : palaceCode;
    }

    /**
     * Lấy trục ý nghĩa của cung.
     */
    public static String getAxis(String palaceCode) {
        PalaceAxis axis = PALACE_MAP.get(palaceCode);
        return axis != null ? axis.getAxis() : "";
    }

    /**
     * Lấy đoạn giới thiệu về cung.
     */
    public static String getIntroduction(String palaceCode) {
        PalaceAxis axis = PALACE_MAP.get(palaceCode);
        return axis != null ? axis.getIntroduction() : "";
    }

    /**
     * Lấy đoạn mô tả tác động của cung.
     */
    public static String getImpact(String palaceCode) {
        PalaceAxis axis = PALACE_MAP.get(palaceCode);
        return axis != null ? axis.getImpact() : "";
    }

    /**
     * Kiểm tra palace code có hợp lệ không.
     */
    public static boolean isValidPalaceCode(String palaceCode) {
        return PALACE_MAP.containsKey(palaceCode);
    }
}


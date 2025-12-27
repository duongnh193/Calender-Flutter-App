package com.duong.lichvanien.tuvi.dto.essay;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO chứa bài luận hoàn chỉnh cho một cung trong lá số Tử Vi.
 * Mỗi bài luận có độ dài 800-1000 từ, được tổng hợp từ các fragments đã match.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Bài luận hoàn chỉnh cho một cung trong lá số Tử Vi")
public class PalaceEssay {

    @Schema(description = "Mã cung", example = "MENH")
    private String palaceCode;

    @Schema(description = "Tên cung tiếng Việt", example = "Mệnh")
    private String palaceName;

    @Schema(description = "Bài luận hoàn chỉnh (800-1000 từ)")
    private String fullEssay;

    @Schema(description = "Tóm tắt ngắn (2-3 câu)")
    private String summary;

    @Schema(description = "Số từ trong bài luận")
    private Integer wordCount;

    @Schema(description = "Các phần của bài luận (để debug/review)")
    private Map<String, String> sections;

    /**
     * Tính số từ trong bài luận.
     */
    public int calculateWordCount() {
        if (fullEssay == null || fullEssay.isBlank()) {
            return 0;
        }
        // Đếm từ bằng cách split theo khoảng trắng
        return fullEssay.trim().split("\\s+").length;
    }

    /**
     * Kiểm tra bài luận có đủ độ dài tối thiểu (800 từ).
     */
    public boolean hasMinimumLength() {
        return calculateWordCount() >= 800;
    }

    /**
     * Kiểm tra bài luận có vượt quá độ dài tối đa (1200 từ).
     */
    public boolean exceedsMaximumLength() {
        return calculateWordCount() > 1200;
    }

    /**
     * Kiểm tra bài luận có hợp lệ (trong khoảng 800-1200 từ).
     */
    public boolean isValidLength() {
        int count = calculateWordCount();
        return count >= 800 && count <= 1200;
    }
}


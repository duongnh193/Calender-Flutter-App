package com.duong.lichvanien.tuvi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating a Tu Vi (Purple Star Astrology) chart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to generate a Tu Vi (Purple Star Astrology) chart")
public class TuViChartRequest {

    @NotNull(message = "Birth date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in format yyyy-MM-dd")
    @Schema(description = "Birth date in ISO format (yyyy-MM-dd)", example = "1995-03-02", required = true)
    private String date;

    @NotNull(message = "Hour is required")
    @Min(value = 0, message = "Hour must be between 0 and 23")
    @Max(value = 23, message = "Hour must be between 0 and 23")
    @Schema(description = "Birth hour (0-23)", example = "8", required = true)
    private Integer hour;

    @Min(value = 0, message = "Minute must be between 0 and 59")
    @Max(value = 59, message = "Minute must be between 0 and 59")
    @Schema(description = "Birth minute (0-59)", example = "30", defaultValue = "0")
    private Integer minute;

    @Schema(description = "Whether the date is lunar calendar", example = "false", defaultValue = "false")
    private Boolean isLunar;

    @Schema(description = "Whether it's a leap month (only when isLunar=true)", example = "false", defaultValue = "false")
    private Boolean isLeapMonth;

    @NotNull(message = "Gender is required")
    @Pattern(regexp = "male|female", message = "Gender must be 'male' or 'female'")
    @Schema(description = "Gender", example = "female", required = true, allowableValues = {"male", "female"})
    private String gender;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name", example = "Nguyễn Văn A", required = true)
    private String name;

    @Schema(description = "Birthplace (optional, for display only)", example = "Hà Nội, Việt Nam")
    private String birthPlace;

    @Schema(description = "Timezone for calculation (default: Asia/Ho_Chi_Minh)", example = "Asia/Ho_Chi_Minh")
    private String timezone;

    // Defaults
    public Integer getMinute() {
        return minute != null ? minute : 0;
    }

    public Boolean getIsLunar() {
        return isLunar != null ? isLunar : false;
    }

    public Boolean getIsLeapMonth() {
        return isLeapMonth != null ? isLeapMonth : false;
    }

    public String getTimezone() {
        return timezone != null ? timezone : "Asia/Ho_Chi_Minh";
    }
}

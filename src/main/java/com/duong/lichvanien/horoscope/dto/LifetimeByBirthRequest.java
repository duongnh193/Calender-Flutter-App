package com.duong.lichvanien.horoscope.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to get lifetime horoscope by birth data")
public class LifetimeByBirthRequest {

    @NotNull(message = "Birth date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in format yyyy-MM-dd")
    @Schema(description = "Birth date in ISO format (yyyy-MM-dd)", example = "1990-02-15", required = true)
    private String date;

    @NotNull(message = "Hour is required")
    @Min(value = 0, message = "Hour must be between 0 and 23")
    @Max(value = 23, message = "Hour must be between 0 and 23")
    @Schema(description = "Birth hour (0-23)", example = "23", required = true)
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
    @Schema(description = "Gender", example = "male", required = true, allowableValues = {"male", "female"})
    private String gender;

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
}


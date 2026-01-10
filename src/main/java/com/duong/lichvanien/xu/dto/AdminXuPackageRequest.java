package com.duong.lichvanien.xu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for admin xu package management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin xu package request")
public class AdminXuPackageRequest {

    @Schema(description = "Package name", example = "Gói 100k")
    @NotBlank(message = "Package name is required")
    private String name;

    @Schema(description = "Amount of xu", example = "100")
    @NotNull(message = "Xu amount is required")
    @Min(value = 1, message = "Xu amount must be at least 1")
    private Integer xuAmount;

    @Schema(description = "Price in VND", example = "100000")
    @NotNull(message = "Price VND is required")
    @Min(value = 1, message = "Price must be at least 1")
    private BigDecimal priceVnd;

    @Schema(description = "Bonus xu", example = "10")
    private Integer bonusXu;

    @Schema(description = "Is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Display order", example = "1")
    private Integer displayOrder;
}


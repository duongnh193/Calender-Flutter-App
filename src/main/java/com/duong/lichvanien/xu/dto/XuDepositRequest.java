package com.duong.lichvanien.xu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for xu deposit.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Xu deposit request")
public class XuDepositRequest {

    @Schema(description = "Xu package ID", example = "1")
    @NotNull(message = "Package ID is required")
    private Long packageId;
}


package com.duong.lichvanien.xu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for purchasing interpretation with xu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Xu purchase request")
public class XuPurchaseRequest {

    @Schema(description = "Chart hash for Tu Vi interpretation", example = "abc123...")
    @NotBlank(message = "Chart hash is required")
    private String chartHash;
}


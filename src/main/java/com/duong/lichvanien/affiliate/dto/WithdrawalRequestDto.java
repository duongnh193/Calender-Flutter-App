package com.duong.lichvanien.affiliate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for withdrawal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Withdrawal request")
public class WithdrawalRequestDto {

    @Schema(description = "Amount of xu to withdraw", example = "100")
    @NotNull(message = "Xu amount is required")
    @Min(value = 1, message = "Xu amount must be at least 1")
    private Integer xuAmount;

    @Schema(description = "Bank name", example = "Vietcombank")
    @NotBlank(message = "Bank name is required")
    private String bankName;

    @Schema(description = "Bank account number", example = "1234567890")
    @NotBlank(message = "Bank account is required")
    private String bankAccount;

    @Schema(description = "Account holder name", example = "Nguyen Van A")
    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;
}


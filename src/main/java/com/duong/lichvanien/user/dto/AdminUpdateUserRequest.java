package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin request DTO for updating user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin update user request")
public class AdminUpdateUserRequest {

    @Schema(description = "User email")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "User phone")
    @Size(min = 10, max = 15, message = "Số điện thoại phải từ 10-15 ký tự")
    private String phone;

    @Schema(description = "User full name")
    @Size(max = 255, message = "Họ tên không quá 255 ký tự")
    private String fullName;

    @Schema(description = "User status")
    private UserStatus status;

    @Schema(description = "User role")
    private UserRole role;
}


package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin request DTO for creating user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin create user request")
public class AdminCreateUserRequest {

    @Schema(description = "User email", example = "user@example.com")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "User phone", example = "0123456789")
    @Size(min = 10, max = 15, message = "Số điện thoại phải từ 10-15 ký tự")
    private String phone;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
    private String password;

    @Schema(description = "User full name", example = "Nguyễn Văn A")
    @Size(max = 255, message = "Họ tên không quá 255 ký tự")
    private String fullName;

    @Schema(description = "User role", example = "USER")
    @Builder.Default
    private UserRole role = UserRole.USER;
}


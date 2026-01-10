package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Admin response DTO for user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin user response")
public class AdminUserResponse {

    @Schema(description = "User ID")
    private Long id;

    @Schema(description = "User UUID")
    private String uuid;

    @Schema(description = "User email")
    private String email;

    @Schema(description = "User phone")
    private String phone;

    @Schema(description = "User full name")
    private String fullName;

    @Schema(description = "User status")
    private UserStatus status;

    @Schema(description = "User role")
    private UserRole role;

    @Schema(description = "Account creation time")
    private LocalDateTime createdAt;

    @Schema(description = "Last update time")
    private LocalDateTime updatedAt;

    @Schema(description = "Last login time")
    private LocalDateTime lastLoginAt;
}


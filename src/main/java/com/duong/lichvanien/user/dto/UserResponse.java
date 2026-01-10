package com.duong.lichvanien.user.dto;

import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String uuid;
    private String email;
    private String phone;
    private String fullName;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    /**
     * Create from UserEntity.
     */
    public static UserResponse fromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserResponse.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .fullName(entity.getFullName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .build();
    }
}


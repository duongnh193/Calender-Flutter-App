package com.duong.lichvanien.user.controller;

import com.duong.lichvanien.user.dto.AdminCreateUserRequest;
import com.duong.lichvanien.user.dto.AdminUpdateUserRequest;
import com.duong.lichvanien.user.dto.AdminUserResponse;
import com.duong.lichvanien.user.enums.UserRole;
import com.duong.lichvanien.user.enums.UserStatus;
import com.duong.lichvanien.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for user management.
 * All endpoints require ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs - requires ADMIN role")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;

    // ==================== USER CRUD ====================

    /**
     * Get all users with pagination.
     */
    @GetMapping("/users")
    @Operation(
            summary = "Get all users",
            description = "Get all users with pagination. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AdminUserResponse> users = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/users/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Get user details by ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {

        return adminUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by UUID.
     */
    @GetMapping("/users/uuid/{uuid}")
    @Operation(
            summary = "Get user by UUID",
            description = "Get user details by UUID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> getUserByUuid(
            @Parameter(description = "User UUID") @PathVariable String uuid) {

        return adminUserService.getUserByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search users.
     */
    @GetMapping("/users/search")
    @Operation(
            summary = "Search users",
            description = "Search users by email, phone, or name. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Page<AdminUserResponse>> searchUsers(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminUserService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by status.
     */
    @GetMapping("/users/status/{status}")
    @Operation(
            summary = "Get users by status",
            description = "Get users filtered by status. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Page<AdminUserResponse>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable UserStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminUserService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role.
     */
    @GetMapping("/users/role/{role}")
    @Operation(
            summary = "Get users by role",
            description = "Get users filtered by role. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Page<AdminUserResponse>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable UserRole role,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminUserService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Create new user.
     */
    @PostMapping("/users")
    @Operation(
            summary = "Create user",
            description = "Create a new user. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "409", description = "User already exists"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        AdminUserResponse user = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Update user.
     */
    @PutMapping("/users/{id}")
    @Operation(
            summary = "Update user",
            description = "Update user information. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "409", description = "Email/Phone already exists"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {

        return adminUserService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user (soft delete).
     */
    @DeleteMapping("/users/{id}")
    @Operation(
            summary = "Delete user (soft)",
            description = "Soft delete user by setting status to BANNED. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        boolean deleted = adminUserService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Hard delete user (permanent).
     */
    @DeleteMapping("/users/{id}/hard")
    @Operation(
            summary = "Delete user (hard)",
            description = "Permanently delete user. Use with caution! Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<Void> hardDeleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        boolean deleted = adminUserService.hardDeleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ==================== STATUS MANAGEMENT ====================

    /**
     * Ban user.
     */
    @PostMapping("/users/{id}/ban")
    @Operation(
            summary = "Ban user",
            description = "Ban a user (set status to BANNED). Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User banned"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> banUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        return adminUserService.banUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Activate user.
     */
    @PostMapping("/users/{id}/activate")
    @Operation(
            summary = "Activate user",
            description = "Activate a user (set status to ACTIVE). Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User activated"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        return adminUserService.activateUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deactivate user.
     */
    @PostMapping("/users/{id}/deactivate")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivate a user (set status to INACTIVE). Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deactivated"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        return adminUserService.deactivateUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Update user role.
     */
    @PostMapping("/users/{id}/role")
    @Operation(
            summary = "Update user role",
            description = "Update user role. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New role") @RequestParam UserRole role) {

        return adminUserService.updateUserRole(id, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== PASSWORD MANAGEMENT ====================

    /**
     * Reset user password.
     */
    @PostMapping("/users/{id}/reset-password")
    @Operation(
            summary = "Reset user password",
            description = "Reset user password to a new value. Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserResponse> resetPassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New password") @RequestParam String newPassword) {

        return adminUserService.resetPassword(id, newPassword)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== STATISTICS ====================

    /**
     * Get user statistics.
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "Get user statistics",
            description = "Get user statistics (total, active, inactive, banned, admin counts). Requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    public ResponseEntity<AdminUserService.UserStatistics> getStatistics() {
        return ResponseEntity.ok(adminUserService.getStatistics());
    }
}


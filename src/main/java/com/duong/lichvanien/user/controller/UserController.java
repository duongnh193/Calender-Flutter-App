package com.duong.lichvanien.user.controller;

import com.duong.lichvanien.common.security.SecurityUtils;
import com.duong.lichvanien.common.security.UserPrincipal;
import com.duong.lichvanien.user.dto.*;
import com.duong.lichvanien.user.entity.UserEntity;
import com.duong.lichvanien.user.service.SessionService;
import com.duong.lichvanien.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account and return JWT tokens")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody UserRegisterRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = userService.register(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = userService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = sessionService.refreshSession(request.getRefreshToken(), httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revoke current session")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            sessionService.revokeSession(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserPrincipal principal = SecurityUtils.getCurrentPrincipal()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        if (principal.isAnonymous()) {
            throw new IllegalStateException("Anonymous session cannot access user info");
        }
        
        UserEntity user = userService.getUserById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update information of the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate account", description = "Deactivate the authenticated user's account")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deactivateAccount() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get user by UUID", description = "Get user information by UUID (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getUserByUuid(@PathVariable String uuid) {
        // TODO: Add admin role check
        UserEntity user = userService.getUserByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
}


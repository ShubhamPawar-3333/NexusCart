package com.nexuscart.user.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.user.entity.UserProfile;
import com.nexuscart.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserProfile>> getProfile(
            @RequestHeader("X-User-Id") String userId) {
        UserProfile profile = userProfileService.getProfile(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<ApiResponse<UserProfile>> getProfileById(
            @PathVariable UUID userId) {
        UserProfile profile = userProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping
    @Operation(summary = "Create user profile")
    public ResponseEntity<ApiResponse<UserProfile>> createProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserProfile profile) {
        profile.setId(UUID.fromString(userId));
        UserProfile created = userProfileService.createProfile(profile);
        return ResponseEntity.ok(ApiResponse.success(created, "Profile created"));
    }

    @PutMapping
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserProfile updates) {
        UserProfile updated = userProfileService.updateProfile(UUID.fromString(userId), updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated"));
    }
}

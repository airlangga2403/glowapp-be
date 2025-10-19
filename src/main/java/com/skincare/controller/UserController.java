package com.skincare.controller;

import com.skincare.dto.request.*;
import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.LoginResponse;
import com.skincare.dto.response.UserProfileResponse;
import com.skincare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserProfileResponse response = userService.register(request);
            return ApiResponse.created("User registered successfully", response);
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ApiResponse.success("Login successful", response);
        } catch (Exception e) {
            log.error("Login failed", e);
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request);
            return ApiResponse.success("Password changed successfully", null);
        } catch (Exception e) {
            log.error("Change password failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{userId}/preference")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updatePreference(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdatePreferenceRequest request) {
        try {
            UserProfileResponse response = userService.updatePreference(userId, request);
            return ApiResponse.success("Preference updated successfully", response);
        } catch (Exception e) {
            log.error("Update preference failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileResponse response = userService.updateProfile(userId, request);
            return ApiResponse.success("Profile updated successfully", response);
        } catch (Exception e) {
            log.error("Update profile failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@PathVariable Integer userId) {
        try {
            UserProfileResponse response = userService.getProfile(userId);
            return ApiResponse.success("Profile retrieved successfully", response);
        } catch (Exception e) {
            log.error("Get profile failed", e);
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
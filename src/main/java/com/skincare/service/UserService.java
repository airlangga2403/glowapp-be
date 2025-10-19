package com.skincare.service;

import com.skincare.dto.request.*;
import com.skincare.dto.response.*;
import com.skincare.entity.UserProfile;
import com.skincare.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userProfileRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        UserProfile user = UserProfile.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .genderId(request.getGender())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        UserProfile saved = userProfileRepository.save(user);
        log.info("User registered successfully: {}", saved.getEmail());

        return mapToResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        UserProfile user = userProfileRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getActiveStatus() != 1) {
            throw new RuntimeException("Account is inactive");
        }

        log.info("User logged in: {}", user.getEmail());

        return LoginResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check if new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userProfileRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
    }

    @Transactional
    public UserProfileResponse updatePreference(Integer userId, UpdatePreferenceRequest request) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSkinTypeId(request.getSkinTypeId());
        user.setHasAllergy(request.getHasAllergy());
        user.setAllergyNotes(request.getAllergyNotes());
        user.setBudgetMin(request.getBudgetMin());
        user.setBudgetMax(request.getBudgetMax());
        user.setPreferredCategory(request.getPreferredCategory());

        UserProfile updated = userProfileRepository.save(user);
        log.info("Preference updated for user: {}", user.getEmail());

        return mapToResponse(updated);
    }

    @Transactional
    public UserProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGenderId(request.getGender());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());

        UserProfile updated = userProfileRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());

        return mapToResponse(updated);
    }

    public UserProfileResponse getProfile(Integer userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    private UserProfileResponse mapToResponse(UserProfile user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .genderId(user.getGenderId())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .profileImageUrl(user.getProfileImageUrl())
                .skinTypeId(user.getSkinTypeId())
                .hasAllergy(user.getHasAllergy())
                .allergyNotes(user.getAllergyNotes())
                .budgetMin(user.getBudgetMin())
                .budgetMax(user.getBudgetMax())
                .preferredCategory(user.getPreferredCategory())
                .build();
    }
}
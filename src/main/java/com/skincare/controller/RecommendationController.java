package com.skincare.controller;

import com.skincare.dto.request.RecommendationRequest;
import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.RecommendationResponse;
import com.skincare.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // TODO FROM JWT SEHARUSNYA ini
    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<RecommendationResponse>> getRecommendations(
            @PathVariable Integer userId,
            @Valid @RequestBody RecommendationRequest request) {
        try {
            log.info("Recommendation request from user: {}", userId);
            RecommendationResponse response = recommendationService.getRecommendations(userId, request);
            return ApiResponse.success("Recommendations generated successfully", response);
        } catch (Exception e) {
            log.error("Get recommendations failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

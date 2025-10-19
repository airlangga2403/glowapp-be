package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponse {
    private Boolean success;
    private String message;
    private Integer totalFound;
    private List<ProductRecommendationResponse> recommendations;
}

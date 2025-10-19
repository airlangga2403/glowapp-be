package com.skincare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
    private Boolean success;
    private String message;
    private Integer totalFound;
    private java.util.List<ProductRecommendation> recommendations;
}
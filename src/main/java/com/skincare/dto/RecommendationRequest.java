package com.skincare.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private Double budget;
    private String skinType;      // "dry", "oily", "normal", "sensitive", "combination"
    private Boolean hasAllergy;   // true/false
    private String category;      // "moisturizer", "cleanser", "serum"
}

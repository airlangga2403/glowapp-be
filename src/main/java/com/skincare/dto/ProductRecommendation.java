package com.skincare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendation {
    private Integer productId;
    private String name;
    private String brand;
    private Double price;
    private Double rank;
    private Double matchScore;    // 0-100%
    private Integer allergenCount;
    private Integer beneficialCount;
    private String reason;
}

package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductRecommendationResponse {
    private Integer productId;
    private String name;
    private String brand;
    private BigDecimal price;
    private BigDecimal rank;
    private Double matchScore;
    private Integer allergenCount;
    private Integer beneficialCount;
    private String reason;
    private String imageUrl;
    private Integer stockQuantity;
}
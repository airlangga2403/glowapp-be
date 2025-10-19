package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailResponse {
    private Integer productId;
    private String name;
    private String description;
    private String ingredients;
    private String category;
    private Integer brandId;
    private String brandName;
    private BigDecimal price;
    private Integer stockQuantity;
    private Double rating;
    private Integer totalReviews;
    private Integer totalSold;
    private Integer allergenCount;
    private Integer beneficialCount;
    private String imageUrl;
    private Integer weightGrams;
    private Integer volumeMl;
}

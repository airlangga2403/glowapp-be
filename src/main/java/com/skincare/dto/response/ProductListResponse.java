package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductListResponse {
    private Integer productId;
    private String name;
    private String category;
    private String brandName;
    private BigDecimal price;
    private Double rating;
    private Integer totalReviews;
    private Integer totalSold;
    private Integer stockQuantity;
    private String imageUrl;
}

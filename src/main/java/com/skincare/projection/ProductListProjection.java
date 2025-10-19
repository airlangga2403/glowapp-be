package com.skincare.projection;

import java.math.BigDecimal;

public interface ProductListProjection {
    Integer getProductId();
    String getName();
    String getCategory();
    String getBrandName();
    BigDecimal getPrice();
    Double getRating();
    Integer getTotalReviews();
    Integer getTotalSold();
    Integer getStockQuantity();
    String getImageUrl();
}

package com.skincare.projection;

import java.math.BigDecimal;

public interface ProductDetailProjection {
    Integer getProductId();
    String getName();
    String getDescription();
    String getIngredients();
    String getCategory();
    Integer getBrandId();
    String getBrandName();
    BigDecimal getPrice();
    Integer getStockQuantity();
    Double getRating();
    Integer getTotalReviews();
    Integer getTotalSold();
    Integer getAllergenCount();
    Integer getBeneficialCount();
    String getImageUrl();
    Integer getWeightGrams();
    Integer getVolumeMl();
}

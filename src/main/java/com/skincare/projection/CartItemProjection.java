package com.skincare.projection;

import java.math.BigDecimal;

public interface CartItemProjection {
    Integer getCartId();
    Integer getProductId();
    String getProductName();
    BigDecimal getProductPrice();
    String getProductImageUrl();
    Integer getQuantity();
    BigDecimal getSubtotal(); // quantity * price
}


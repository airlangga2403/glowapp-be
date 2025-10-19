package com.skincare.projection;

import java.math.BigDecimal;

public interface TransactionItemProjection {
    Integer getTransactionDetailId();
    Integer getProductId();
    String getProductName();
    String getProductImageUrl();
    Integer getQuantity();
    BigDecimal getPricePerUnit();
    BigDecimal getSubtotal();
}

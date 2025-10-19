package com.skincare.projection;

import java.math.BigDecimal;

public interface TransactionDetailProjection {
    Integer getTransactionId();
    String getTransactionCode();
    String getTransactionStatus();
    BigDecimal getSubtotal();
    BigDecimal getShippingCost();
    BigDecimal getDiscountAmount();
    BigDecimal getTaxAmount();
    BigDecimal getTotalAmount();
    String getShippingAddress();
    String getShippingCity();
    String getShippingProvince();
    String getPaymentMethod();
    String getCreatedAt();
}

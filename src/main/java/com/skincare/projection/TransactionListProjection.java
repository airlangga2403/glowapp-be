package com.skincare.projection;

import java.math.BigDecimal;

public interface TransactionListProjection {
    Integer getTransactionId();
    String getTransactionCode();
    String getTransactionStatus();
    BigDecimal getTotalAmount();
    String getCreatedAt();
    Integer getTotalItems();
}

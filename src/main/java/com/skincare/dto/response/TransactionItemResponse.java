package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionItemResponse {
    private Integer transactionDetailId;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}

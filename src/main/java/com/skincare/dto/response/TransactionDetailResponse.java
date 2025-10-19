package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TransactionDetailResponse {
    private Integer transactionId;
    private String transactionCode;
    private Integer transactionStatusId;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String shippingCity;
    private String shippingProvince;
    private String paymentMethod;
    private String createdAt;
    private List<TransactionItemResponse> items;
}
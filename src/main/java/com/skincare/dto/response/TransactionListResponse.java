package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransactionListResponse {
    private Integer transactionId;
    private String transactionCode;
    private String transactionStatus;
    private BigDecimal totalAmount;
    private String createdAt;
    private Integer totalItems;
}
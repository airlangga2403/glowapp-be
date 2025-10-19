package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartSummaryResponse {
    private Integer totalItems;
    private BigDecimal subtotal;
    private List<CartItemResponse> items;
}


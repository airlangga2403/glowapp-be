package com.skincare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String shippingCity;
    private String shippingProvince;
    private String shippingPostalCode;

    @NotBlank(message = "Shipping phone is required")
    private String shippingPhone;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private BigDecimal shippingCost = BigDecimal.ZERO;

    private String notes;
}
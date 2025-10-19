package com.skincare.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecommendationRequest {
    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.01", message = "Budget must be greater than 0")
    private BigDecimal budget;

    @NotBlank(message = "Skin type is required")
    private String skinType; // COMBINATION, DRY, NORMAL, OILY, SENSITIVE

    private Boolean hasAllergy = false;

    @NotBlank(message = "Category is required")
    private String category; // Moisturizer, Cleanser, Serum, etc.
}
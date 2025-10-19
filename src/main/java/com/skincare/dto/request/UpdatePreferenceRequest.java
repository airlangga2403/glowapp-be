package com.skincare.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePreferenceRequest {
    private Integer skinTypeId; // COMBINATION, DRY, NORMAL, OILY, SENSITIVE
    private Boolean hasAllergy;
    private String allergyNotes;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String preferredCategory;
}
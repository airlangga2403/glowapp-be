package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UserProfileResponse {
    private Integer userId;
    private String name;
    private String email;
    private String phone;
    private Integer genderId;
    private String address;
    private LocalDate dateOfBirth;
    private String profileImageUrl;

    private Integer skinTypeId;

    private Boolean hasAllergy;
    private String allergyNotes;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String preferredCategory;
}
package com.skincare.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phone;
    private Integer gender;
    private String address;
    private LocalDate dateOfBirth;
    private String profileImageUrl;
}

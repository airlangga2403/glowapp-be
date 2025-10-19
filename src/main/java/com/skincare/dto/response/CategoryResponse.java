package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String name;
    private String displayName;
    private String icon;
}
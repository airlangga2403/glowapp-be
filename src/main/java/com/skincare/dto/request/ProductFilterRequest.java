package com.skincare.dto.request;

import lombok.Data;

@Data
// TODO GUNAKAN PARAM SEBAIKNYA
public class ProductFilterRequest {
    private String searchTerm;
    private String category;
    private String skinType;
    private Integer page = 0;
    private Integer size = 10;
}

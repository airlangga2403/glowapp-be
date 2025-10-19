package com.skincare.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Integer userId;
    private String name;
    private String email;
    private String token;
}

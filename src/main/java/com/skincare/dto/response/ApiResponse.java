package com.skincare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T payload;

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T payload) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .payload(payload)
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<T>builder()
                .code(HttpStatus.CREATED.value())
                .message(message)
                .payload(payload)
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .build());
    }
}
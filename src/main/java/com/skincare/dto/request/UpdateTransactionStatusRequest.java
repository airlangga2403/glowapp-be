package com.skincare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTransactionStatusRequest {
    @NotBlank(message = "Status is required")
    private Integer transactionstatusId; // ON PROCESS, SUCCESS, CANCELED

    private String paymentProofUrl;
}
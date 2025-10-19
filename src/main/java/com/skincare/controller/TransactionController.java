package com.skincare.controller;

import com.skincare.dto.request.CreateTransactionRequest;
import com.skincare.dto.request.UpdateTransactionStatusRequest;
import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.TransactionDetailResponse;
import com.skincare.dto.response.TransactionListResponse;
import com.skincare.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    // Create Transaction (Checkout)
    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> createTransaction(
            @PathVariable Integer userId,
            @Valid @RequestBody CreateTransactionRequest request) {
        try {
            TransactionDetailResponse response = transactionService.createTransaction(userId, request);
            return ApiResponse.created("Transaction created successfully", response);
        } catch (Exception e) {
            log.error("Create transaction failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Get User Transactions (with search and filter)
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<TransactionListResponse>>> getUserTransactions(
            @PathVariable Integer userId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<TransactionListResponse> transactions = transactionService.getUserTransactions(
                    userId, searchTerm, status, page, size);
            return ApiResponse.success("Transactions retrieved successfully", transactions);
        } catch (Exception e) {
            log.error("Get user transactions failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Get Transaction Detail
    @GetMapping("/users/{userId}/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> getTransactionDetail(
            @PathVariable Integer userId,
            @PathVariable Integer transactionId) {
        try {
            TransactionDetailResponse response = transactionService.getTransactionDetail(userId, transactionId);
            return ApiResponse.success("Transaction detail retrieved successfully", response);
        } catch (Exception e) {
            log.error("Get transaction detail failed", e);
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Update Transaction Status
    @PutMapping("/users/{userId}/{transactionId}/status")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> updateTransactionStatus(
            @PathVariable Integer userId,
            @PathVariable Integer transactionId,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {
        try {
            TransactionDetailResponse response = transactionService.updateTransactionStatus(
                    userId, transactionId, request);
            return ApiResponse.success("Transaction status updated successfully", response);
        } catch (Exception e) {
            log.error("Update transaction status failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
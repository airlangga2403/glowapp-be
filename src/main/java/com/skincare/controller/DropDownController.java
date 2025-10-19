package com.skincare.controller;

import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.DropDownResponse;
import com.skincare.service.SkinTypeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dropdown")
@CrossOrigin(origins = "*")
@Log4j2
public class DropDownController {

    private final SkinTypeService skinTypeService;

    public DropDownController(SkinTypeService skinTypeService) {
        this.skinTypeService = skinTypeService;
    }

//    //  TODO ..
//
//    // DROPDOWN CATEGORY
//    // DRPOPDOWN GENDER
//    // DROPDOWN ACTIVE STATUS
//    // DROPDOWN TRANSACTION STATUS
//    // DROPDOWN SKIN_TYPE
//
//    @GetMapping("/category")
//    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
//        try {
//            List<CategoryResponse> categories = categoryService.getCategories();
//            return ApiResponse.success("Categories retrieved successfully", categories);
//        } catch (Exception e) {
//            log.error("Get categories failed", e);
//            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }
//
//    @GetMapping("/gender")
//    public ResponseEntity<ApiResponse<List<GenderResponse>>> getGenders() {
//        try {
//            List<GenderResponse> genders = genderService.getGenders();
//            return ApiResponse.success("Genders retrieved successfully", genders);
//        } catch (Exception e) {
//            log.error("Get genders failed", e);
//            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }
//
//    @GetMapping("/active-status")
//    public ResponseEntity<ApiResponse<List<ActiveStatusResponse>>> getActiveStatus() {
//        try {
//            List<ActiveStatusResponse> activeStatus = activeStatusService.getActiveStatus();
//            return ApiResponse.success("Active status retrieved successfully", activeStatus);
//        } catch (Exception e) {
//            log.error("Get active status failed", e);
//            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }
//
//    @GetMapping("/transaction-status")
//    public ResponseEntity<ApiResponse<List<TransactionStatusResponse>>> getTransactionStatus() {
//        try {
//            List<TransactionStatusResponse> transactionStatus = transactionStatusService.getTransactionStatus();
//            return ApiResponse.success("Transaction status retrieved successfully", transactionStatus);
//        } catch (Exception e) {
//            log.error("Get transaction status failed", e);
//            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }

    @GetMapping("/skin-type")
    public ResponseEntity<ApiResponse<List<DropDownResponse>>> getSkinType() {
        try {
            List<DropDownResponse> skinType = skinTypeService.getSkinType();
            return ApiResponse.success("Skin type retrieved successfully", skinType);
        } catch (Exception e) {
            log.error("Get skin type failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}

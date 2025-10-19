package com.skincare.controller;

import com.skincare.dto.response.ApiResponse;
import com.skincare.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CsvImportController {

    private final CsvImportService csvImportService;

    /**
     * Import products from CSV file
     * POST /api/admin/import-csv
     */
    @PostMapping("/import-csv")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importCsv(
            @RequestParam("file") MultipartFile file) {

        try {
            log.info("Received CSV import request: {}", file.getOriginalFilename());

            // Validate file
            if (file.isEmpty()) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "File is empty");
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "File must be CSV format");
            }

            // Import CSV
            Map<String, Object> result = csvImportService.importFromCsv(file);

            return ApiResponse.success("CSV imported successfully", result);

        } catch (Exception e) {
            log.error("CSV import failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Import failed: " + e.getMessage());
        }
    }
}

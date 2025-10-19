package com.skincare.controller;

import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.CategoryResponse;
import com.skincare.dto.response.ProductDetailResponse;
import com.skincare.dto.response.ProductListResponse;
import com.skincare.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // Home Page - Search Products
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> searchProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductListResponse> products = productService.searchProducts(searchTerm, page, size);
            return ApiResponse.success("Products retrieved successfully", products);
        } catch (Exception e) {
            log.error("Search products failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Filter by Skin Type
    @GetMapping("/filter-by-skin-type")
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> filterBySkinType(
            @RequestParam Integer skinTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductListResponse> products = productService.filterBySkinType(skinTypeId, page, size);
            return ApiResponse.success("Products filtered by skin type", products);
        } catch (Exception e) {
            log.error("Filter by skin type failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Filter by Category
    @GetMapping("/filter-by-category")
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> filterByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductListResponse> products = productService.filterByCategory(category, page, size);
            return ApiResponse.success("Products filtered by category", products);
        } catch (Exception e) {
            log.error("Filter by category failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Explore - All Products with Filters
    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> exploreProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductListResponse> products = productService.exploreProducts(searchTerm, category, page, size);
            return ApiResponse.success("Products explored successfully", products);
        } catch (Exception e) {
            log.error("Explore products failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Product Detail by ID
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable Integer productId) {
        try {
            ProductDetailResponse product = productService.getProductDetail(productId);
            return ApiResponse.success("Product detail retrieved successfully", product);
        } catch (Exception e) {
            log.error("Get product detail failed", e);
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Special Promo Products
    @GetMapping("/promo")
    public ResponseEntity<ApiResponse<List<ProductListResponse>>> getPromoProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductListResponse> products = productService.getPromoProducts(limit);
            return ApiResponse.success("Promo products retrieved successfully", products);
        } catch (Exception e) {
            log.error("Get promo products failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Get All Categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        try {
            List<CategoryResponse> categories = Arrays.asList(
                    CategoryResponse.builder().name("Moisturizer").displayName("Moisturizer").icon("💧").build(),
                    CategoryResponse.builder().name("Cleanser").displayName("Cleanser").icon("🧼").build(),
                    CategoryResponse.builder().name("Serum").displayName("Treatment (Serum)").icon("💉").build(),
                    CategoryResponse.builder().name("Face Mask").displayName("Face Mask").icon("😷").build(),
                    CategoryResponse.builder().name("Eye Cream").displayName("Eye Cream").icon("👁️").build(),
                    CategoryResponse.builder().name("Sunscreen").displayName("Sun Protect / Sunscreen").icon("☀️").build()
            );
            return ApiResponse.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Get categories failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

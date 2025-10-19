package com.skincare.service;

import com.skincare.dto.response.ProductDetailResponse;
import com.skincare.dto.response.ProductListResponse;
import com.skincare.projection.ProductDetailProjection;
import com.skincare.projection.ProductListProjection;
import com.skincare.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductListResponse> searchProducts(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListProjection> projections = productRepository.searchProducts(searchTerm, pageable);

        return projections.map(this::mapToListResponse);
    }

    public Page<ProductListResponse> filterBySkinType(Integer skinType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListProjection> projections = productRepository.filterBySkinType(skinType, pageable);

        return projections.map(this::mapToListResponse);
    }

    public Page<ProductListResponse> filterByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListProjection> projections = productRepository.filterByCategory(category, pageable);

        return projections.map(this::mapToListResponse);
    }

    public Page<ProductListResponse> exploreProducts(String searchTerm, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListProjection> projections = productRepository.exploreProducts(searchTerm, category, pageable);

        return projections.map(this::mapToListResponse);
    }

    public ProductDetailResponse getProductDetail(Integer productId) {
        ProductDetailProjection projection = productRepository.findDetailById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToDetailResponse(projection);
    }

    public List<ProductListResponse> getPromoProducts(Integer limit) {
        List<ProductListProjection> projections = productRepository.findPromoProducts(limit);
        return projections.stream().map(this::mapToListResponse).collect(Collectors.toList());
    }

    private ProductListResponse mapToListResponse(ProductListProjection p) {
        return ProductListResponse.builder()
                .productId(p.getProductId())
                .name(p.getName())
                .category(p.getCategory())
                .brandName(p.getBrandName())
                .price(p.getPrice())
                .rating(p.getRating())
                .totalReviews(p.getTotalReviews())
                .totalSold(p.getTotalSold())
                .stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl())
                .build();
    }

    private ProductDetailResponse mapToDetailResponse(ProductDetailProjection p) {
        return ProductDetailResponse.builder()
                .productId(p.getProductId())
                .name(p.getName())
                .description(p.getDescription())
                .ingredients(p.getIngredients())
                .category(p.getCategory())
                .brandId(p.getBrandId())
                .brandName(p.getBrandName())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .rating(p.getRating())
                .totalReviews(p.getTotalReviews())
                .totalSold(p.getTotalSold())
                .allergenCount(p.getAllergenCount())
                .beneficialCount(p.getBeneficialCount())
                .imageUrl(p.getImageUrl())
                .weightGrams(p.getWeightGrams())
                .volumeMl(p.getVolumeMl())
                .build();
    }
}


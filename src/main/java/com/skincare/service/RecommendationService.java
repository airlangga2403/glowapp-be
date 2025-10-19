package com.skincare.service;

import com.skincare.dto.request.RecommendationRequest;
import com.skincare.dto.response.ProductRecommendationResponse;
import com.skincare.dto.response.RecommendationResponse;
import com.skincare.entity.Product;
import com.skincare.entity.RecommendationHistory;
import com.skincare.repository.ProductRepository;
import com.skincare.repository.RecommendationHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    // Dependencies
    private final ProductRepository productRepository;
    private final RecommendationHistoryRepository recommendationHistoryRepository;
    private final MLModelService mlModelService;
    private final ObjectMapper objectMapper;

    /**
     * ===================================================================
     * MAIN METHOD: Get TOP 5 Product Recommendations
     * ===================================================================
     *
     * Flow:
     * 1. Query database untuk produk yang compatible (SQL Native Query)
     * 2. Load ML model rules dari decision_tree_rules.json
     * 3. Score setiap produk menggunakan ML model
     * 4. Sort: Match Score (DESC) → Rank (DESC) → Price (ASC)
     * 5. Return TOP 5 dengan reason
     * 6. Save history ke database
     */
    @Transactional
    public RecommendationResponse getRecommendations(Integer userId, RecommendationRequest request) {

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║          RECOMMENDATION REQUEST PROCESSING                    ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ User ID        : {}", userId);
        log.info("│ Budget         : ${}", request.getBudget());
        log.info("│ Skin Type      : {}", request.getSkinType());
        log.info("│ Has Allergy    : {}", request.getHasAllergy());
        log.info("│ Category       : {}", request.getCategory());
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ========== STEP 1: VALIDATE & NORMALIZE INPUT ==========
        String skinType = request.getSkinType().toUpperCase().trim();
        String category = capitalizeFirst(request.getCategory().trim());
        BigDecimal budget = request.getBudget();
        boolean hasAllergy = request.getHasAllergy() != null && request.getHasAllergy();

        log.info("✓ Input validated and normalized");

        // ========== STEP 2: QUERY DATABASE FOR COMPATIBLE PRODUCTS ==========
        log.info("");
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ STEP 1: Querying Database (Native SQL)                     │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        List<Product> compatibleProducts = productRepository.findCompatibleProducts(
                budget,
                skinType,
                category
        );

        if (compatibleProducts.isEmpty()) {
            log.warn("✗ No compatible products found in database");
            log.warn("  Filters applied:");
            log.warn("    - price <= ${}", budget);
            log.warn("    - suitable_for_{} = true", skinType.toLowerCase());
            log.warn("    - category = '{}'", category);
            log.warn("    - stock_quantity > 0");
            log.warn("    - active_status = 1");

            return RecommendationResponse.builder()
                    .success(true)
                    .message(String.format(
                            "No %s products found for %s skin type within $%.2f budget. " +
                                    "Try increasing your budget or selecting a different category.",
                            category, skinType, budget
                    ))
                    .totalFound(0)
                    .recommendations(new ArrayList<>())
                    .build();
        }

        log.info("✓ Found {} compatible products from database", compatibleProducts.size());
        log.info("");

        // ========== STEP 3: LOAD ML MODEL & SCORE EACH PRODUCT ==========
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ STEP 2: Scoring Products (ML Model from Python)            │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        List<ProductRecommendationResponse> scoredProducts = new ArrayList<>();
        int scoredCount = 0;
        int rejectedCount = 0;

        for (Product product : compatibleProducts) {

            // Calculate match score using ML model (decision tree rules from Python)
            double matchScore = mlModelService.predictMatchScore(
                    product,
                    budget.doubleValue(),
                    skinType,
                    hasAllergy,
                    category
            );

            // Only include products above threshold (50.0 from decision_tree_rules.json)
            if (matchScore > 50.0) {
                scoredCount++;

                String reason = generateRecommendationReason(product, matchScore, request);

                ProductRecommendationResponse rec = ProductRecommendationResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .brand(product.getBrand().getName())
                        .price(product.getPrice())
                        .rank(product.getRating())
                        .matchScore(Math.round(matchScore * 10.0) / 10.0)
                        .allergenCount(product.getAllergenCount())
                        .beneficialCount(product.getBeneficialCount())
                        .reason(reason)
                        .imageUrl(product.getImageUrl())
                        .stockQuantity(product.getStockQuantity())
                        .build();

                scoredProducts.add(rec);

                log.info("  ✓ {} | Score: {} | Rank: {} | Price: ${}",
                        truncate(product.getName(), 35),
                        String.format("%.1f", matchScore),
                        product.getRating(),
                        product.getPrice());
            } else {
                rejectedCount++;
                log.debug("  ✗ {} | Score: {} (below threshold)",
                        product.getName(), String.format("%.1f", matchScore));
            }
        }

        log.info("");
        log.info("✓ Scoring completed:");
        log.info("  - Qualified: {} products (score > 50.0)", scoredCount);
        log.info("  - Rejected: {} products (score ≤ 50.0)", rejectedCount);
        log.info("");

        if (scoredProducts.isEmpty()) {
            log.warn("✗ No products scored above threshold (50.0)");

            return RecommendationResponse.builder()
                    .success(true)
                    .message("No high-quality matches found for your criteria. Try adjusting your preferences.")
                    .totalFound(0)
                    .recommendations(new ArrayList<>())
                    .build();
        }

        // ========== STEP 4: SORT BY RANKING CRITERIA ==========
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ STEP 3: Sorting Products (Multi-level)                     │");
        log.info("└─────────────────────────────────────────────────────────────┘");
        log.info("Sorting criteria (priority order):");
        log.info("  1. Match Score (DESC)  - Highest score first");
        log.info("  2. Product Rank (DESC) - Best rating if score tied");
        log.info("  3. Price (ASC)         - Cheapest if score & rank tied");

        scoredProducts.sort(Comparator
                .comparing(ProductRecommendationResponse::getMatchScore).reversed()
                .thenComparing(ProductRecommendationResponse::getRank).reversed()
                .thenComparing(ProductRecommendationResponse::getPrice)
        );

        log.info("✓ Products sorted successfully");
        log.info("");

        // ========== STEP 5: GET TOP 5 ==========
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ STEP 4: Selecting TOP 5 Recommendations                    │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        List<ProductRecommendationResponse> top5 = scoredProducts.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Log TOP 5 results
        for (int i = 0; i < top5.size(); i++) {
            ProductRecommendationResponse rec = top5.get(i);
            log.info("{}. {}", i + 1, rec.getName());
            log.info("   Brand      : {}", rec.getBrand());
            log.info("   Score      : {} | Rank: {}/5.0 | Price: ${}",
                    rec.getMatchScore(), rec.getRank(), rec.getPrice());
            log.info("   Allergens  : {} | Benefits: {} | Stock: {}",
                    rec.getAllergenCount(), rec.getBeneficialCount(), rec.getStockQuantity());
            log.info("   Reason     : {}", rec.getReason());
            if (i < top5.size() - 1) log.info("");
        }

        log.info("");
        log.info("✓ TOP 5 recommendations selected");
        log.info("");

        // ========== STEP 6: SAVE TO DATABASE ==========
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ STEP 5: Saving Recommendation History                      │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        saveRecommendationHistory(userId, request, top5);

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║          RECOMMENDATION COMPLETED SUCCESSFULLY                ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");
        log.info("Summary:");
        log.info("  - Total found: {} products", scoredProducts.size());
        log.info("  - Returned: {} products (TOP 5)", top5.size());
        log.info("  - History saved for user: {}", userId);
        log.info("");

        return RecommendationResponse.builder()
                .success(true)
                .message(String.format("Found %d recommendations (showing top 5)", scoredProducts.size()))
                .totalFound(scoredProducts.size())
                .recommendations(top5)
                .build();
    }

    /**
     * Generate human-readable reason for recommendation
     */
    private String generateRecommendationReason(Product product, double matchScore,
                                                RecommendationRequest request) {
        List<String> reasons = new ArrayList<>();

        // Price value analysis
        double priceRatio = product.getPrice().doubleValue() / request.getBudget().doubleValue();
        if (priceRatio <= 0.5) {
            reasons.add("Excellent value - 50% under budget");
        } else if (priceRatio <= 0.7) {
            reasons.add("Great value - well within budget");
        } else if (priceRatio <= 0.9) {
            reasons.add("Good price point");
        }

        if (product.getRating().compareTo(BigDecimal.valueOf(4.5)) >= 0) {
            reasons.add("Outstanding rating (4.5+ stars)");
        } else if (product.getRating().compareTo(BigDecimal.valueOf(4.3)) >= 0) {
            reasons.add("Highly rated product");
        } else if (product.getRating().compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            reasons.add("Well-reviewed");
        }

        // Allergen consideration
        if (Boolean.TRUE.equals(request.getHasAllergy())) {
            if (product.getAllergenCount() <= 2) {
                reasons.add("Very low allergen count - safe choice");
            } else if (product.getAllergenCount() <= 4) {
                reasons.add("Low allergen count");
            }
        }

        // Beneficial ingredients
        if (product.getBeneficialCount() >= 8) {
            reasons.add("Packed with beneficial ingredients");
        } else if (product.getBeneficialCount() >= 5) {
            reasons.add("Rich in beneficial ingredients");
        }

        // Skin type specific messaging
        String skinType = request.getSkinType().toLowerCase();
        if (skinType.equals("sensitive") && product.getSuitableForSensitive()) {
            reasons.add("Specifically formulated for sensitive skin");
        } else if (skinType.equals("oily") && product.getSuitableForOily()) {
            reasons.add("Suitable for oily skin");
        } else if (skinType.equals("dry") && product.getSuitableForDry()) {
            reasons.add("Perfect for dry skin");
        } else if (skinType.equals("combination") && product.getSuitableForCombination()) {
            reasons.add("Ideal for combination skin");
        }

        // Stock availability
        if (product.getStockQuantity() < 10) {
            reasons.add("Limited stock - order soon!");
        }

        // Default if no specific reasons
        if (reasons.isEmpty()) {
            reasons.add("Good match for your skin type and budget");
        }

        return String.join(", ", reasons);
    }

    /**
     * Save recommendation history to database for analytics
     */
    private void saveRecommendationHistory(Integer userId, RecommendationRequest request,
                                           List<ProductRecommendationResponse> recommendations) {
        try {
            // Extract product IDs
            List<Integer> productIds = recommendations.stream()
                    .map(ProductRecommendationResponse::getProductId)
                    .collect(Collectors.toList());

            // Convert to JSON string
            String productIdsJson = objectMapper.writeValueAsString(productIds);

            // Create history record
            RecommendationHistory history = RecommendationHistory.builder()
                    .userId(userId)
                    .skinType(request.getSkinType())
                    .category(request.getCategory())
                    .budget(request.getBudget())
                    .hasAllergy(request.getHasAllergy())
                    .recommendedProductIds(productIdsJson)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Save to database
            recommendationHistoryRepository.save(history);

            log.info("✓ Recommendation history saved to database");
            log.info("  - Record ID: {}", history.getRecommendationId());
            log.info("  - Product IDs: {}", productIds);

        } catch (Exception e) {
            log.error("✗ Failed to save recommendation history", e);
            // Don't fail the whole request if history save fails
        }
    }

    // ========== HELPER METHODS ==========

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
//package com.skincare.service.uji_coba;
//
//import com.skincare.dto.ProductRecommendation;
//import com.skincare.dto.RecommendationRequest;
//import com.skincare.dto.RecommendationResponse;
//import com.skincare.model.Product;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Service
//public class RecomendarService {
//
//    @Autowired
//    private MLModelService mlModelService;
//
//    /**
//     * Get TOP 5 product recommendations based on user preferences
//     *
//     * Ranking Logic (in order):
//     * 1. Match Score (highest first) - dari ML model
//     * 2. Product Rank/Rating (highest first) - kualitas produk
//     * 3. Price (lowest first) - lebih murah lebih baik jika score & rank sama
//     *
//     * @param request User preferences (budget, skinType, hasAllergy, category)
//     * @return Top 5 recommended products
//     */
//    public RecommendationResponse getTopRecommendations(RecommendationRequest request) {
//        log.info("==========================================================");
//        log.info("RECOMMENDATION REQUEST");
//        log.info("==========================================================");
//        log.info("Budget: ${}", request.getBudget());
//        log.info("Skin Type: {}", request.getSkinType());
//        log.info("Has Allergy: {}", request.getHasAllergy());
//        log.info("Category: {}", request.getCategory());
//        log.info("==========================================================");
//
//        // ========== INPUT VALIDATION ==========
//
//        if (request.getBudget() == null || request.getBudget() <= 0) {
//            log.warn("✗ Invalid budget: {}", request.getBudget());
//            return RecommendationResponse.builder()
//                    .success(false)
//                    .message("Budget must be greater than 0")
//                    .totalFound(0)
//                    .recommendations(new ArrayList<>())
//                    .build();
//        }
//
//        if (request.getSkinType() == null || request.getSkinType().trim().isEmpty()) {
//            log.warn("✗ Skin type is required");
//            return RecommendationResponse.builder()
//                    .success(false)
//                    .message("Skin type is required (dry, normal, oily, sensitive, combination)")
//                    .totalFound(0)
//                    .recommendations(new ArrayList<>())
//                    .build();
//        }
//
//        // Set defaults
//        if (request.getHasAllergy() == null) {
//            request.setHasAllergy(false);
//            log.debug("Setting default hasAllergy: false");
//        }
//
//        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
//            request.setCategory("moisturizer");
//            log.debug("Setting default category: moisturizer");
//        }
//
//        // Normalize inputs
//        String skinType = request.getSkinType().toLowerCase().trim();
//        String category = request.getCategory().toLowerCase().trim();
//
//        // ========== STEP 1: GET COMPATIBLE PRODUCTS ==========
//
//        List<Product> compatibleProducts = mlModelService.getCompatibleProducts(
//                request.getBudget(),
//                skinType,
//                category
//        );
//
//        if (compatibleProducts.isEmpty()) {
//            log.warn("✗ No products found matching criteria");
//            log.warn("  - Try increasing budget");
//            log.warn("  - Try different skin type");
//            log.warn("  - Try different category");
//            return RecommendationResponse.builder()
//                    .success(true)
//                    .message(String.format(
//                            "No %s products found for %s skin type within $%.2f budget",
//                            category, skinType, request.getBudget()
//                    ))
//                    .totalFound(0)
//                    .recommendations(new ArrayList<>())
//                    .build();
//        }
//
//        log.info("✓ Found {} compatible products", compatibleProducts.size());
//
//        // ========== STEP 2: SCORE EACH PRODUCT USING ML MODEL ==========
//
//        List<ProductRecommendation> scoredProducts = new ArrayList<>();
//
//        for (Product product : compatibleProducts) {
//            double matchScore = mlModelService.predictMatchScore(
//                    product,
//                    request.getBudget(),
//                    skinType,
//                    request.getHasAllergy(),
//                    category
//            );
//
//            // Only include products with score > 50 (threshold from ML training)
//            if (matchScore > 50.0) {
//                String reason = generateRecommendationReason(product, matchScore, request);
//
//                ProductRecommendation rec = ProductRecommendation.builder()
//                        .productId(product.getId())
//                        .name(product.getName())
//                        .brand(product.getBrand())
//                        .price(product.getPrice())
//                        .rank(product.getRank())
//                        .matchScore(Math.round(matchScore * 10.0) / 10.0)
//                        .allergenCount(product.getAllergenCount())
//                        .beneficialCount(product.getBeneficialCount())
//                        .reason(reason)
//                        .build();
//
//                scoredProducts.add(rec);
//
//                log.debug("✓ Product qualified: {} (Score: {})", product.getName(), matchScore);
//            } else {
//                log.debug("✗ Product rejected: {} (Score: {} < 50)", product.getName(), matchScore);
//            }
//        }
//
//        if (scoredProducts.isEmpty()) {
//            log.warn("✗ No products scored above threshold (50)");
//            return RecommendationResponse.builder()
//                    .success(true)
//                    .message("No high-quality matches found. Try adjusting your criteria.")
//                    .totalFound(0)
//                    .recommendations(new ArrayList<>())
//                    .build();
//        }
//
//        log.info("✓ {} products passed scoring threshold", scoredProducts.size());
//
//        // ========== STEP 3: SORT BY RANKING CRITERIA ==========
//
//        scoredProducts.sort(Comparator
//                .comparing(ProductRecommendation::getMatchScore).reversed()  // 1. Highest score first
//                .thenComparing(ProductRecommendation::getRank).reversed()    // 2. Best rating first
//                .thenComparing(ProductRecommendation::getPrice)              // 3. Cheapest first
//        );
//
//        log.info("✓ Products sorted by: Score (DESC) → Rank (DESC) → Price (ASC)");
//
//        // ========== STEP 4: GET TOP 5 ==========
//
//        List<ProductRecommendation> top5 = scoredProducts.stream()
//                .limit(5)
//                .collect(Collectors.toList());
//
//        log.info("==========================================================");
//        log.info("TOP 5 RECOMMENDATIONS:");
//        log.info("==========================================================");
//        for (int i = 0; i < top5.size(); i++) {
//            ProductRecommendation rec = top5.get(i);
//            log.info("{}. {} by {} - Score: {} | Rank: {} | Price: ${}",
//                    i + 1, rec.getName(), rec.getBrand(),
//                    rec.getMatchScore(), rec.getRank(), rec.getPrice());
//        }
//        log.info("==========================================================");
//
//        return RecommendationResponse.builder()
//                .success(true)
//                .message(String.format("Found %d recommendations (showing top 5)", scoredProducts.size()))
//                .totalFound(scoredProducts.size())
//                .recommendations(top5)
//                .build();
//    }
//
//    /**
//     * Generate human-readable reason for recommendation
//     */
//    private String generateRecommendationReason(Product product, double matchScore,
//                                                RecommendationRequest request) {
//        List<String> reasons = new ArrayList<>();
//
//        // Price value
//        double priceRatio = product.getPrice() / request.getBudget();
//        if (priceRatio <= 0.5) {
//            reasons.add("Excellent value - 50% under budget");
//        } else if (priceRatio <= 0.7) {
//            reasons.add("Great value - well within budget");
//        } else if (priceRatio <= 0.9) {
//            reasons.add("Good price point");
//        }
//
//        // Product quality
//        if (product.getRank() >= 4.5) {
//            reasons.add("Outstanding rating (4.5+ stars)");
//        } else if (product.getRank() >= 4.3) {
//            reasons.add("Highly rated product");
//        } else if (product.getRank() >= 4.0) {
//            reasons.add("Well-reviewed");
//        }
//
//        // Allergen consideration
//        if (request.getHasAllergy()) {
//            if (product.getAllergenCount() <= 2) {
//                reasons.add("Very low allergen count - safe choice");
//            } else if (product.getAllergenCount() <= 4) {
//                reasons.add("Low allergen count");
//            }
//        }
//
//        // Beneficial ingredients
//        if (product.getBeneficialCount() >= 8) {
//            reasons.add("Packed with beneficial ingredients");
//        } else if (product.getBeneficialCount() >= 5) {
//            reasons.add("Rich in beneficial ingredients");
//        }
//
//        // Skin type specific
//        String skinType = request.getSkinType().toLowerCase();
//        if (skinType.equals("sensitive") && product.getSensitive() == 1) {
//            reasons.add("Specifically formulated for sensitive skin");
//        } else if (skinType.equals("oily") && product.getOily() == 1) {
//            reasons.add("Suitable for oily skin");
//        } else if (skinType.equals("dry") && product.getDry() == 1) {
//            reasons.add("Perfect for dry skin");
//        }
//
//        // Default if no specific reasons
//        if (reasons.isEmpty()) {
//            reasons.add("Good match for your skin type and budget");
//        }
//
//        return String.join(", ", reasons);
//    }
//}
//package com.skincare.service.uji_coba;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.skincare.model.Product;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class MLModelService {
//
//    private List<Product> productDatabase;
//    private Map<String, Integer> skinTypeMap;
//    private Map<String, Integer> categoryMap;
//
//    @PostConstruct
//    public void init() {
//        try {
//            loadProductDatabase();
//            loadMappings();
//            log.info("✓ ML Model Service initialized successfully!");
//            log.info("✓ Loaded {} products", productDatabase.size());
//        } catch (Exception e) {
//            log.error("✗ Failed to initialize ML Model Service", e);
//            throw new RuntimeException("Failed to load model resources", e);
//        }
//    }
//
//    private void loadProductDatabase() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        InputStream inputStream = new ClassPathResource("products_database.json").getInputStream();
//        productDatabase = mapper.readValue(inputStream, new TypeReference<List<Product>>(){});
//        log.debug("Loaded products: {}", productDatabase.size());
//    }
//
//    private void loadMappings() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        InputStream inputStream = new ClassPathResource("model_mappings.json").getInputStream();
//        Map<String, Object> mappings = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>(){});
//
//        skinTypeMap = (Map<String, Integer>) mappings.get("skin_type_map");
//        categoryMap = (Map<String, Integer>) mappings.get("category_map");
//
//        log.debug("Skin type mappings: {}", skinTypeMap);
//        log.debug("Category mappings: {}", categoryMap);
//    }
//
//    /**
//     * Predict match score using ACTUAL ML model logic from Python training
//     * This mimics the Random Forest decision tree logic trained in Python
//     */
//    public double predictMatchScore(Product product, double budget, String skinType,
//                                    boolean hasAllergy, String category) {
//
//        // ========== HARD CONSTRAINTS (Decision Tree Root Nodes) ==========
//
//        // Node 1: Budget constraint - MUST pass
//        if (product.getPrice() > budget) {
//            log.debug("Product {} rejected: Price ${} > Budget ${}",
//                    product.getName(), product.getPrice(), budget);
//            return 0.0;
//        }
//
//        // Node 2: Skin type compatibility - MUST pass
//        if (!product.isSuitableForSkinType(skinType)) {
//            log.debug("Product {} rejected: Not suitable for {} skin",
//                    product.getName(), skinType);
//            return 0.0;
//        }
//
//        // Node 3: Category match - MUST pass
//        if (!product.getLabel().equalsIgnoreCase(category)) {
//            log.debug("Product {} rejected: Category mismatch (want: {}, got: {})",
//                    product.getName(), category, product.getLabel());
//            return 0.0;
//        }
//
//        // ========== FEATURE-BASED SCORING (Decision Tree Leaf Nodes) ==========
//
//        double baseScore = 60.0;
//
//        // Feature 1: Product Quality (Rank) - Weight: 0.25 (highest importance)
//        // Range: 0-20 points
//        double rankScore = (product.getRank() / 5.0) * 20.0;
//
//        // Feature 2: Price Efficiency - Weight: 0.20
//        // Range: 0-15 points
//        // Logic: Cheaper relative to budget = better value
//        double priceUtilization = product.getPrice() / budget;
//        double priceScore = (1.0 - priceUtilization) * 15.0;
//
//        // Feature 3: Value Score - Weight: 0.15
//        // Range: 0-10 points
//        // This is rank/price ratio from Python training
//        double valueBonus = Math.min(10.0, product.getValueScore() * 10.0);
//
//        // Feature 4: Allergen Penalty - Weight: 0.15
//        // Range: -15 to 0 points
//        double allergenPenalty;
//        if (hasAllergy) {
//            // User is allergic: harsh penalty
//            allergenPenalty = Math.min(15.0, product.getAllergenCount() * 3.0);
//        } else {
//            // User not allergic: mild penalty
//            allergenPenalty = Math.min(15.0, product.getAllergenCount() * 1.0);
//        }
//
//        // Feature 5: Beneficial Ingredients - Weight: 0.10
//        // Range: 0-10 points
//        double beneficialBonus = Math.min(10.0, product.getBeneficialCount() * 1.5);
//
//        // Feature 6: Quality Score - Weight: 0.05
//        // Range: 0-5 points
//        double qualityBonus = product.getQualityScore() * 5.0;
//
//        // ========== CALCULATE FINAL SCORE ==========
//
//        double finalScore = baseScore
//                + rankScore
//                + priceScore
//                + valueBonus
//                - allergenPenalty
//                + beneficialBonus
//                + qualityBonus;
//
//        // Normalize to 0-100 range
//        finalScore = Math.max(0.0, Math.min(100.0, finalScore));
//
//        // Log detailed scoring breakdown
//        log.debug("Scoring for {}: Base={}, Rank={}, Price={}, Value={}, Allergen={}, Beneficial={}, Quality={} → TOTAL={}",
//                product.getName(), baseScore, rankScore, priceScore, valueBonus,
//                -allergenPenalty, beneficialBonus, qualityBonus, finalScore);
//
//        return finalScore;
//    }
//
//    /**
//     * Get products that pass hard constraints
//     */
//    public List<Product> getCompatibleProducts(double budget, String skinType, String category) {
//        List<Product> compatible = productDatabase.stream()
//                .filter(p -> p.getPrice() <= budget)
//                .filter(p -> p.isSuitableForSkinType(skinType))
//                .filter(p -> p.getLabel().equalsIgnoreCase(category))
//                .collect(Collectors.toList());
//
//        log.info("Compatible products for budget=${}, skinType={}, category={}: {} out of {}",
//                budget, skinType, category, compatible.size(), productDatabase.size());
//
//        return compatible;
//    }
//
//    public List<Product> getAllProducts() {
//        return new ArrayList<>(productDatabase);
//    }
//}

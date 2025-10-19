package com.skincare.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.skincare.entity.Brand;
import com.skincare.entity.Category;
import com.skincare.entity.Product;
import com.skincare.repository.BrandRepository;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    // ... (konstanta ALLERGEN_KEYWORDS dan BENEFICIAL_KEYWORDS tetap sama) ...
    private static final List<String> ALLERGEN_KEYWORDS = Arrays.asList(
            "fragrance", "parfum", "alcohol denat", "methylparaben", "propylparaben",
            "sodium benzoate", "phenoxyethanol", "lanolin", "citrus",
            "limonene", "linalool", "citronellol", "geraniol", "citral",
            "benzyl", "chlorphenesin"
    );

    private static final List<String> BENEFICIAL_KEYWORDS = Arrays.asList(
            "hyaluronic", "glycerin", "niacinamide", "peptide", "vitamin",
            "squalane", "ceramide", "aloe", "ferment filtrate", "collagen",
            "tocopherol", "panthenol", "retinol", "adenosine", "copper"
    );


    @Transactional
    public Map<String, Object> importFromCsv(MultipartFile file) throws Exception {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║              CSV IMPORT SERVICE STARTED                       ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        List<String[]> records;
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            records = reader.readAll();
            // Hapus header
            if (!records.isEmpty()) {
                records.remove(0);
            }
        } catch (CsvException e) {
            throw new Exception("Failed to parse CSV: " + e.getMessage(), e);
        }

        if (records.isEmpty()) {
            throw new Exception("CSV file is empty or contains only a header");
        }

        // ======================================================================
        // LANGKAH 1: Buat semua Category dan Brand terlebih dahulu
        // ======================================================================
        Map<String, Category> categoryMap = processCategories(records);
        Map<String, Brand> brandMap = processBrands(records);

        // ======================================================================
        // LANGKAH 2: Buat semua Product menggunakan data yang sudah ada
        // ======================================================================
        List<Product> productsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            String[] row = records.get(i);
            try {
                String categoryName = row[0].trim();
                String brandName = row[1].trim();

                Category category = categoryMap.get(categoryName);
                Brand brand = brandMap.get(brandName);

                Product product = parseProductFromCsv(row, category, brand);
                productsToSave.add(product);

            } catch (Exception e) {
                String error = String.format("Row %d (%s): %s", i + 2, row[2], e.getMessage());
                errors.add(error);
                log.error("✗ Failed to process row {}: {}", i + 2, e.getMessage());
            }
        }

        // ======================================================================
        // LANGKAH 3: Simpan semua product dalam satu batch
        // ======================================================================
        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
            log.info("✓ Saved {} products to database", productsToSave.size());
        }

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║              CSV IMPORT COMPLETED                             ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        Map<String, Object> result = new HashMap<>();
        result.put("success", productsToSave.size());
        result.put("failed", errors.size());
        result.put("errors", errors);

        return result;
    }

    private Map<String, Category> processCategories(List<String[]> records) {
        // 1. Ambil semua nama kategori unik dari CSV
        Set<String> categoryNamesInCsv = records.stream()
                .map(row -> row[0].trim())
                .collect(Collectors.toSet());

        // 2. Cari kategori mana yang sudah ada di database
        List<Category> existingCategories = categoryRepository.findAllByNameIn(new ArrayList<>(categoryNamesInCsv));
        Map<String, Category> categoryMap = existingCategories.stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        // 3. Tentukan kategori mana yang baru dan perlu dibuat
        List<Category> newCategoriesToCreate = categoryNamesInCsv.stream()
                .filter(name -> !categoryMap.containsKey(name))
                .map(name -> Category.builder().name(name).activeStatus(1).createdBy(0).build())
                .collect(Collectors.toList());

        // 4. Simpan kategori baru ke database
        if (!newCategoriesToCreate.isEmpty()) {
            List<Category> savedNewCategories = categoryRepository.saveAll(newCategoriesToCreate);
            log.info("✓ Created {} new categories.", savedNewCategories.size());
            // Tambahkan kategori yang baru disimpan ke map
            savedNewCategories.forEach(cat -> categoryMap.put(cat.getName(), cat));
        }

        return categoryMap;
    }

    private Map<String, Brand> processBrands(List<String[]> records) {
        // Prosesnya sama persis seperti kategori
        Set<String> brandNamesInCsv = records.stream()
                .map(row -> row[1].trim())
                .collect(Collectors.toSet());

        List<Brand> existingBrands = brandRepository.findAllByNameIn(new ArrayList<>(brandNamesInCsv));
        Map<String, Brand> brandMap = existingBrands.stream()
                .collect(Collectors.toMap(Brand::getName, Function.identity()));

        List<Brand> newBrandsToCreate = brandNamesInCsv.stream()
                .filter(name -> !brandMap.containsKey(name))
                .map(name -> Brand.builder().name(name).description("Premium skincare brand").activeStatus(1).build())
                .collect(Collectors.toList());

        if (!newBrandsToCreate.isEmpty()) {
            List<Brand> savedNewBrands = brandRepository.saveAll(newBrandsToCreate);
            log.info("✓ Created {} new brands.", savedNewBrands.size());
            savedNewBrands.forEach(brand -> brandMap.put(brand.getName(), brand));
        }

        return brandMap;
    }

    private Product parseProductFromCsv(String[] row, Category category, Brand brand) throws Exception {
        // ... (Logika parsing dan feature engineering sama seperti sebelumnya) ...
        String name = row[2].trim();
        BigDecimal price = new BigDecimal(row[3].trim());
        BigDecimal rating = new BigDecimal(row[4].trim());
        String ingredients = row[5].trim();

        boolean suitableForCombination = "1".equals(row[6].trim());
        boolean suitableForDry = "1".equals(row[7].trim());
        boolean suitableForNormal = "1".equals(row[8].trim());
        boolean suitableForOily = "1".equals(row[9].trim());
        boolean suitableForSensitive = "1".equals(row[10].trim());

        int allergenCount = countKeywords(ingredients, ALLERGEN_KEYWORDS);
        int beneficialCount = countKeywords(ingredients, BENEFICIAL_KEYWORDS);
        BigDecimal qualityScore = rating.divide(new BigDecimal("5.0"), 2, RoundingMode.HALF_UP);
        BigDecimal valueScore = BigDecimal.ZERO;
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            valueScore = qualityScore.divide(price.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
        }

        int stockQuantity = 50 + new Random().nextInt(151);

        return Product.builder()
                .name(name)
                .description("Premium " + category.getName().toLowerCase() + " for your skin care needs")
                .ingredients(ingredients)
                // Gunakan ID dari objek yang sudah pasti ada
                .categoryId(category.getCategoryId())
                .brandId(brand.getBrandId())
                .price(price)
                .rating(rating)
                .stockQuantity(stockQuantity)
                .totalReviews((int) (Math.random() * 500) + 50)
                .totalSold((int) (Math.random() * 200) + 20)
                .allergenCount(allergenCount)
                .beneficialCount(beneficialCount)
                .qualityScore(qualityScore)
                .valueScore(valueScore)
                .suitableForCombination(suitableForCombination)
                .suitableForDry(suitableForDry)
                .suitableForNormal(suitableForNormal)
                .suitableForOily(suitableForOily)
                .suitableForSensitive(suitableForSensitive)
                .imageUrl("https://example.com/products/" + name.toLowerCase().replaceAll(" ", "-") + ".jpg")
                .activeStatus(1)
                .createdBy(0)
                .build();
    }

    // ... (metode countKeywords dan validateHeader tetap sama) ...
    private int countKeywords(String ingredients, List<String> keywords) {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0;
        }
        String ingredientsLower = ingredients.toLowerCase();
        int count = 0;
        for (String keyword : keywords) {
            if (ingredientsLower.contains(keyword.toLowerCase())) {
                count++;
            }
        }
        return count;
    }
}
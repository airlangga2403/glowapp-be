//package com.skincare.config;
//
//import com.skincare.entity.Brand;
//import com.skincare.entity.Product;
//import com.skincare.repository.BrandRepository;
//import com.skincare.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataSeeder implements CommandLineRunner {
//
//    private final BrandRepository brandRepository;
//    private final ProductRepository productRepository;
//
//    @Override
//    public void run(String... args) {
//        if (productRepository.count() == 0) {
//            log.info("Seeding initial data...");
//            seedBrands();
//            seedProducts();
//            log.info("Data seeding completed!");
//        }
//    }
//
//    private void seedBrands() {
//        Brand laMer = Brand.builder()
//                .name("LA MER")
//                .description("Luxury skincare brand")
//                .build();
//        brandRepository.save(laMer);
//
//        Brand kiehls = Brand.builder()
//                .name("KIEHL'S SINCE 1851")
//                .description("Premium skincare since 1851")
//                .build();
//        brandRepository.save(kiehls);
//
//        Brand drunkElephant = Brand.builder()
//                .name("DRUNK ELEPHANT")
//                .description("Clean clinical skincare")
//                .build();
//        brandRepository.save(drunkElephant);
//
//        log.info("Brands seeded: {}", brandRepository.count());
//    }
//
//    private void seedProducts() {
//        Brand laMer = brandRepository.findAll().stream()
//                .filter(b -> b.getName().equals("LA MER"))
//                .findFirst()
//                .orElse(null);
//
//        Brand kiehls = brandRepository.findAll().stream()
//                .filter(b -> b.getName().equals("KIEHL'S SINCE 1851"))
//                .findFirst()
//                .orElse(null);
//
//        if (laMer != null) {
//            Product product1 = Product.builder()
//                    .name("Crème de la Mer")
//                    .description("Legendary moisturizing cream")
//                    .ingredients("Algae Extract, Glycerin, Mineral Oil, Petrolatum...")
////                    .category("Moisturizer")
//                    .brandId(laMer.getBrandId())
////                    .brandName(laMer.getName())
//                    .price(new BigDecimal("175.00"))
//                    .stockQuantity(50)
//                    .rating(BigDecimal.valueOf(4.1))
//                    .totalReviews(120)
//                    .totalSold(85)
//                    .allergenCount(7)
//                    .beneficialCount(5)
//                    .qualityScore(BigDecimal.valueOf(0.82))
//                    .valueScore(BigDecimal.valueOf(0.47))
//                    .suitableForCombination(true)
//                    .suitableForDry(true)
//                    .suitableForNormal(true)
//                    .suitableForOily(true)
//                    .suitableForSensitive(true)
//                    .imageUrl("https://example.com/lamer.jpg")
//                    .volumeMl(60)
//                    .build();
//            productRepository.save(product1);
//        }
//
//        if (kiehls != null) {
//            Product product2 = Product.builder()
//                    .name("Ultra Facial Cream")
//                    .description("24-hour hydrating facial cream")
//                    .ingredients("Water, Glycerin, Cyclohexasiloxane, Squalane...")
////                    .category("Moisturizer")
//                    .brandId(kiehls.getBrandId())
////                    .brandName(kiehls.getName())
//                    .price(new BigDecimal("29.00"))
//                    .stockQuantity(100)
//                    .rating(BigDecimal.valueOf(4.4))
//                    .totalReviews(250)
//                    .totalSold(180)
//                    .allergenCount(4)
//                    .beneficialCount(3)
//                    .qualityScore(BigDecimal.valueOf(0.88))
//                    .valueScore(BigDecimal.valueOf(3.03))
//                    .suitableForCombination(true)
//                    .suitableForDry(true)
//                    .suitableForNormal(true)
//                    .suitableForOily(true)
//                    .suitableForSensitive(true)
//                    .imageUrl("https://example.com/kiehls.jpg")
//                    .volumeMl(50)
//                    .build();
//            productRepository.save(product2);
//        }
//
//        log.info("Products seeded: {}", productRepository.count());
//    }
//}
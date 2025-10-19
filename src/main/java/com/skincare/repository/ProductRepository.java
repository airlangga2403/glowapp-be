package com.skincare.repository;

import com.skincare.entity.Product;
import com.skincare.projection.ProductDetailProjection;
import com.skincare.projection.ProductListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Home Page - Search Products
    @Query(value = "SELECT p.product_id as productId, p.name, p.category, p.brand_name as brandName, " +
            "p.price, p.rating, p.total_reviews as totalReviews, p.total_sold as totalSold, " +
            "p.stock_quantity as stockQuantity, p.image_url as imageUrl " +
            "FROM product p " +
            "WHERE p.active_status = 1 " +
            "AND (:searchTerm IS NULL OR p.name LIKE CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY p.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM product p WHERE p.active_status = 1 " +
                    "AND (:searchTerm IS NULL OR p.name LIKE CONCAT('%', :searchTerm, '%'))",
            nativeQuery = true)
    Page<ProductListProjection> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Filter by Skin Type
    @Query(value = """
            SELECT p.product_id as productId, p.name, p.category, p.brand_name as brandName,
                p.price, p.rating, p.total_reviews as totalReviews, p.total_sold as totalSold,
                p.stock_quantity as stockQuantity, p.image_url as imageUrl
                FROM product p
                WHERE p.active_status = 1
                AND (CASE
                  WHEN :skinType = 1 THEN p.suitable_for_combination = true
                  WHEN :skinType = 2 THEN p.suitable_for_dry = true
                  WHEN :skinType = 3 THEN p.suitable_for_normal = true
                  WHEN :skinType = 4 THEN p.suitable_for_oily = true
                  WHEN :skinType = 5 THEN p.suitable_for_sensitive = true
                  ELSE 1=1 END)
                ORDER BY p.rating DESC, p.total_sold desc """,
            nativeQuery = true)
    Page<ProductListProjection> filterBySkinType(@Param("skinType") Integer skinType, Pageable pageable);

    // Filter by Category
    @Query(value = "SELECT p.product_id as productId, p.name, p.category, p.brand_name as brandName, " +
            "p.price, p.rating, p.total_reviews as totalReviews, p.total_sold as totalSold, " +
            "p.stock_quantity as stockQuantity, p.image_url as imageUrl " +
            "FROM product p " +
            "WHERE p.active_status No String products found for OILY skin type wNo String products found for OILY skin type w 1 " +
            "AND (:category IS NULL OR p.category = :category) " +
            "ORDER BY p.rating DESC, p.total_sold DESC",
            nativeQuery = true)
    Page<ProductListProjection> filterByCategory(@Param("category") String category, Pageable pageable);

    // Explore - All Products with Search and Category Filter
    @Query(value = """
            SELECT p.product_id as productId, p.name, p.category, p.brand_name as brandName,
            p.price, p.rating, p.total_reviews as totalReviews, p.total_sold as totalSold,
            p.stock_quantity as stockQuantity, p.image_url as imageUrl
            FROM product p
            WHERE p.active_status = 1
            AND (:searchTerm IS NULL OR p.name LIKE CONCAT('%', :searchTerm, '%'))
            AND (:category IS NULL OR p.category = :category)
            ORDER BY p.rating DESC, p.total_sold DESC            
    """,
            countQuery =
       """
       SELECT COUNT(*) FROM product p WHERE p.active_status = 1
                    AND (:searchTerm IS NULL OR p.name LIKE CONCAT('%', :searchTerm, '%'))
                    AND (:category IS NULL OR p.category = :category)
        """, nativeQuery = true)
    Page<ProductListProjection> exploreProducts(@Param("searchTerm") String searchTerm,
                                                @Param("category") String category,
                                                Pageable pageable);

    // Product Detail by ID
    @Query(value = "SELECT p.product_id as productId, p.name, p.description, p.ingredients, " +
            "p.category, p.brand_id as brandId, p.brand_name as brandName, " +
            "p.price, p.stock_quantity as stockQuantity, p.rating, " +
            "p.total_reviews as totalReviews, p.total_sold as totalSold, " +
            "p.allergen_count as allergenCount, p.beneficial_count as beneficialCount, " +
            "p.image_url as imageUrl, p.weight_grams as weightGrams, p.volume_ml as volumeMl " +
            "FROM product p " +
            "WHERE p.product_id = :productId AND p.active_status = 1",
            nativeQuery = true)
    Optional<ProductDetailProjection> findDetailById(@Param("productId") Integer productId);

    // Recommendation - Get compatible products
    @Query(value =
            """
            SELECT p.* FROM product p
            JOIN category c ON p.category_id = c.category_id
            WHERE p.active_status = 1
            AND p.price <= :budget
            AND c.category_name = :category
            AND (CASE
              WHEN :skinType = 'COMBINATION' THEN p.suitable_for_combination = true
              WHEN :skinType = 'DRY' THEN p.suitable_for_dry = true
              WHEN :skinType = 'NORMAL' THEN p.suitable_for_normal = true
              WHEN :skinType = 'OILY' THEN p.suitable_for_oily = true
              WHEN :skinType = 'SENSITIVE' THEN p.suitable_for_sensitive = true
              ELSE 1=1 END)
            AND p.stock_quantity > 0          
            """, nativeQuery = true)
    List<Product> findCompatibleProducts(@Param("budget") BigDecimal budget,
                                         @Param("skinType") String skinType,
                                         @Param("category") String category);
    // Special Promo Products (Top rated, high sold)
    @Query(value = "SELECT p.product_id as productId, p.name, p.category, p.brand_name as brandName, " +
            "p.price, p.rating, p.total_reviews as totalReviews, p.total_sold as totalSold, " +
            "p.stock_quantity as stockQuantity, p.image_url as imageUrl " +
            "FROM product p " +
            "WHERE p.active_status = 1 " +
            "AND p.rating >= 4.0 " +
            "AND p.total_sold > 10 " +
            "ORDER BY p.total_sold DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<ProductListProjection> findPromoProducts(@Param("limit") Integer limit);
}

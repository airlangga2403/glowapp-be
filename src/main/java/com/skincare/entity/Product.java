package com.skincare.entity;

import com.skincare.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column()
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "brand_id")
    private Integer brandId;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "total_sold")
    private Integer totalSold = 0;

    // ML Features
    @Column(name = "allergen_count")
    private Integer allergenCount = 0;

    @Column(name = "beneficial_count")
    private Integer beneficialCount = 0;

    @Column(name = "quality_score", precision = 3, scale = 2)
    private BigDecimal qualityScore = BigDecimal.valueOf(0.0);

    @Column(name = "value_score", precision = 5, scale = 2)
    private BigDecimal valueScore = BigDecimal.valueOf(0.0);

    // Skin Type Compatibility
    @Column(name = "suitable_for_combination")
    private Boolean suitableForCombination = false;

    @Column(name = "suitable_for_dry")
    private Boolean suitableForDry = false;

    @Column(name = "suitable_for_normal")
    private Boolean suitableForNormal = false;

    @Column(name = "suitable_for_oily")
    private Boolean suitableForOily = false;

    @Column(name = "suitable_for_sensitive")
    private Boolean suitableForSensitive = false;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Column(name = "category_id")       // CATEGORY MISALKAN MOISTURIZER, DLL
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id",
            insertable = false, updatable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", referencedColumnName = "brand_id",
            insertable = false, updatable = false)
    private Brand brand;

    // Helper method
    // TODO USING ENUMS
    public boolean isSuitableForSkinType(String skinType) {
        if (skinType == null) return false;
        switch (skinType.toUpperCase()) {
            case "COMBINATION": return Boolean.TRUE.equals(suitableForCombination);
            case "DRY": return Boolean.TRUE.equals(suitableForDry);
            case "NORMAL": return Boolean.TRUE.equals(suitableForNormal);
            case "OILY": return Boolean.TRUE.equals(suitableForOily);
            case "SENSITIVE": return Boolean.TRUE.equals(suitableForSensitive);
            default: return false;
        }
    }
}

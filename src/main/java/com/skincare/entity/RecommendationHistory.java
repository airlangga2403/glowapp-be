package com.skincare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Integer recommendationId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "skin_type", length = 50)
    private String skinType;

    @Column(length = 100)
    private String category;

    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "has_allergy")
    private Boolean hasAllergy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommended_product_ids", columnDefinition = "JSON")
    private String recommendedProductIds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

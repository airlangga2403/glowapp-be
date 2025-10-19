package com.skincare.repository;

import com.skincare.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    @Query(value = "SELECT * FROM product_review " +
            "WHERE product_id = :productId AND active_status = 1 " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    Page<ProductReview> findByProductId(@Param("productId") Integer productId, Pageable pageable);
}
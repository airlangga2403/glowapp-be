package com.skincare.repository;

import com.skincare.entity.TransactionDetail;
import com.skincare.projection.TransactionItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Integer> {

    // Get transaction items
    @Query(value = "SELECT td.transaction_detail_id as transactionDetailId, " +
            "td.product_id as productId, td.product_name as productName, " +
            "td.product_image_url as productImageUrl, td.quantity, " +
            "td.price_per_unit as pricePerUnit, td.subtotal " +
            "FROM transaction_detail td " +
            "WHERE td.transaction_id = :transactionId " +
            "ORDER BY td.transaction_detail_id",
            nativeQuery = true)
    List<TransactionItemProjection> findByTransactionId(@Param("transactionId") Integer transactionId);
}


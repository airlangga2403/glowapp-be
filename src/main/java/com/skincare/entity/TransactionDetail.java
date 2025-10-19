package com.skincare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_detail_id")
    private Integer transactionDetailId;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column()
    private Integer quantity;

    @Column(name = "price_per_unit",precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

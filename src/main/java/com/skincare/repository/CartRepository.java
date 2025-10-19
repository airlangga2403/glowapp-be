package com.skincare.repository;

import com.skincare.entity.Cart;
import com.skincare.projection.CartItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Get user's cart items
    @Query(value = "SELECT c.cart_id as cartId, c.product_id as productId, " +
            "c.product_name as productName, c.product_price as productPrice, " +
            "c.product_image_url as productImageUrl, c.quantity, " +
            "(c.quantity * c.product_price) as subtotal " +
            "FROM cart c " +
            "WHERE c.user_id = :userId AND c.active_status = 1 " +
            "ORDER BY c.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM cart c WHERE c.user_id = :userId AND c.active_status = 1",
            nativeQuery = true)
    Page<CartItemProjection> findUserCartItems(@Param("userId") Integer userId, Pageable pageable);

    // Check if product already in cart
    @Query(value = "SELECT * FROM cart WHERE user_id = :userId AND product_id = :productId AND active_status = 1",
            nativeQuery = true)
    Optional<Cart> findByUserAndProduct(@Param("userId") Integer userId,
                                        @Param("productId") Integer productId);

    // Get total cart items count
    @Query(value = "SELECT COUNT(*) FROM cart WHERE user_id = :userId AND active_status = 1",
            nativeQuery = true)
    Integer countUserCartItems(@Param("userId") Integer userId);

    // Clear user cart (soft delete)
    @Modifying
    @Query(value = "UPDATE cart SET active_status = 0 WHERE user_id = :userId",
            nativeQuery = true)
    void clearUserCart(@Param("userId") Integer userId);

    // Delete specific cart item
    @Modifying
    @Query(value = "UPDATE cart SET active_status = 0 WHERE cart_id = :cartId",
            nativeQuery = true)
    void softDeleteCartItem(@Param("cartId") Integer cartId);
}


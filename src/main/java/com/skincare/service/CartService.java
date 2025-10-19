package com.skincare.service;

import com.skincare.dto.request.AddToCartRequest;
import com.skincare.dto.request.UpdateCartQuantityRequest;
import com.skincare.dto.response.CartItemResponse;
import com.skincare.dto.response.CartSummaryResponse;
import com.skincare.entity.Cart;
import com.skincare.entity.Product;
import com.skincare.projection.CartItemProjection;
import com.skincare.repository.CartRepository;
import com.skincare.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addToCart(Integer userId, AddToCartRequest request) {
        // Check if product exists and has stock
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        // Check if product already in cart
        Cart existingCart = cartRepository.findByUserAndProduct(userId, request.getProductId())
                .orElse(null);

        if (existingCart != null) {
            // Update quantity
            int newQuantity = existingCart.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock");
            }
            existingCart.setQuantity(newQuantity);
            Cart updated = cartRepository.save(existingCart);

            return mapToResponse(updated);
        } else {
            // Add new cart item
            Cart cart = Cart.builder()
                    .userId(userId)
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .productImageUrl(product.getImageUrl())
                    .quantity(request.getQuantity())
                    .createdBy(userId)
                    .activeStatus(1)
                    .build();

            Cart saved = cartRepository.save(cart);
            log.info("Product added to cart: userId={}, productId={}", userId, request.getProductId());

            return mapToResponse(saved);
        }
    }

    public Page<CartItemResponse> getUserCart(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CartItemProjection> projections = cartRepository.findUserCartItems(userId, pageable);

        return projections.map(this::mapProjectionToResponse);
    }

    public CartSummaryResponse getCartSummary(Integer userId) {
        Pageable pageable = PageRequest.of(0, 100); // Get all items
        Page<CartItemProjection> projections = cartRepository.findUserCartItems(userId, pageable);

        List<CartItemResponse> items = projections.getContent().stream()
                .map(this::mapProjectionToResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartSummaryResponse.builder()
                .totalItems(items.size())
                .subtotal(subtotal)
                .items(items)
                .build();
    }

    @Transactional
    public CartItemResponse updateCartQuantity(Integer userId, Integer cartId, UpdateCartQuantityRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        // Check stock
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        cart.setQuantity(request.getQuantity());
        Cart updated = cartRepository.save(cart);

        log.info("Cart quantity updated: cartId={}, newQuantity={}", cartId, request.getQuantity());

        return mapToResponse(updated);
    }

    @Transactional
    public void removeFromCart(Integer userId, Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        cartRepository.softDeleteCartItem(cartId);
        log.info("Cart item removed: cartId={}", cartId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        cartRepository.clearUserCart(userId);
        log.info("Cart cleared for user: {}", userId);
    }

    private CartItemResponse mapToResponse(Cart cart) {
        BigDecimal subtotal = cart.getProductPrice().multiply(new BigDecimal(cart.getQuantity()));

        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .productId(cart.getProductId())
                .productName(cart.getProductName())
                .productPrice(cart.getProductPrice())
                .productImageUrl(cart.getProductImageUrl())
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    private CartItemResponse mapProjectionToResponse(CartItemProjection p) {
        return CartItemResponse.builder()
                .cartId(p.getCartId())
                .productId(p.getProductId())
                .productName(p.getProductName())
                .productPrice(p.getProductPrice())
                .productImageUrl(p.getProductImageUrl())
                .quantity(p.getQuantity())
                .subtotal(p.getSubtotal())
                .build();
    }
}
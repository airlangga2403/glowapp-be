package com.skincare.controller;

import com.skincare.dto.request.AddToCartRequest;
import com.skincare.dto.request.UpdateCartQuantityRequest;
import com.skincare.dto.response.ApiResponse;
import com.skincare.dto.response.CartItemResponse;
import com.skincare.dto.response.CartSummaryResponse;
import com.skincare.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    // Add to Cart
    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @PathVariable Integer userId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            CartItemResponse response = cartService.addToCart(userId, request);
            return ApiResponse.created("Product added to cart successfully", response);
        } catch (Exception e) {
            log.error("Add to cart failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Get User Cart Items (Paginated)
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<CartItemResponse>>> getUserCart(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CartItemResponse> cartItems = cartService.getUserCart(userId, page, size);
            return ApiResponse.success("Cart items retrieved successfully", cartItems);
        } catch (Exception e) {
            log.error("Get user cart failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Get Cart Summary (All Items + Total)
    @GetMapping("/users/{userId}/summary")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCartSummary(@PathVariable Integer userId) {
        try {
            CartSummaryResponse summary = cartService.getCartSummary(userId);
            return ApiResponse.success("Cart summary retrieved successfully", summary);
        } catch (Exception e) {
            log.error("Get cart summary failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Update Cart Quantity
    @PutMapping("/users/{userId}/items/{cartId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer cartId,
            @Valid @RequestBody UpdateCartQuantityRequest request) {
        try {
            CartItemResponse response = cartService.updateCartQuantity(userId, cartId, request);
            return ApiResponse.success("Cart quantity updated successfully", response);
        } catch (Exception e) {
            log.error("Update cart quantity failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Remove Item from Cart
    @DeleteMapping("/users/{userId}/items/{cartId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Integer userId,
            @PathVariable Integer cartId) {
        try {
            cartService.removeFromCart(userId, cartId);
            return ApiResponse.success("Item removed from cart successfully", null);
        } catch (Exception e) {
            log.error("Remove from cart failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Clear Cart
    @DeleteMapping("/users/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Integer userId) {
        try {
            cartService.clearCart(userId);
            return ApiResponse.success("Cart cleared successfully", null);
        } catch (Exception e) {
            log.error("Clear cart failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

package com.skincare.service;

import com.skincare.dto.request.CreateTransactionRequest;
import com.skincare.dto.request.UpdateTransactionStatusRequest;
import com.skincare.dto.response.*;
import com.skincare.entity.*;
import com.skincare.enums.TransactionStatusEnums;
import com.skincare.projection.CartItemProjection;
import com.skincare.projection.TransactionItemProjection;
import com.skincare.projection.TransactionListProjection;
import com.skincare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final CartRepository cartRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProductRepository productRepository;

    @Transactional
    public TransactionDetailResponse createTransaction(Integer userId, CreateTransactionRequest request) {
        // Get user cart items
        Pageable pageable = PageRequest.of(0, 100);
        Page<CartItemProjection> cartItems = cartRepository.findUserCartItems(userId, pageable);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Get user info
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate totals
        BigDecimal subtotal = cartItems.getContent().stream()
                .map(CartItemProjection::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = subtotal
                .add(request.getShippingCost())
                .subtract(BigDecimal.ZERO); // discount if any

        // Generate transaction code
        String transactionCode = generateTransactionCode();

        // Create transaction
        Transaction transaction = Transaction.builder()
                .transactionCode(transactionCode)
                .userId(userId)
                .userName(user.getName())
                .userEmail(user.getEmail())
                .transactionStatusId(TransactionStatusEnums.ON_PROCESS.getCode())
                .subtotal(subtotal)
                .shippingCost(request.getShippingCost())
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingProvince(request.getShippingProvince())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingPhone(request.getShippingPhone())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .createdBy(userId)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Create transaction details & update stock
        for (CartItemProjection cartItem : cartItems.getContent()) {
            TransactionDetail detail = TransactionDetail.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .productImageUrl(cartItem.getProductImageUrl())
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(cartItem.getProductPrice())
                    .subtotal(cartItem.getSubtotal())
                    .createdAt(LocalDateTime.now())
                    .build();

            transactionDetailRepository.save(detail);

            // Update product stock & total sold
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                product.setTotalSold(product.getTotalSold() + cartItem.getQuantity());
                productRepository.save(product);
            }
        }

        // Clear user cart
        cartRepository.clearUserCart(userId);

        log.info("Transaction created: {}", transactionCode);

        return getTransactionDetail(userId, savedTransaction.getTransactionId());
    }

    public Page<TransactionListResponse> getUserTransactions(Integer userId, String searchTerm,
                                                             String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionListProjection> projections = transactionRepository.findUserTransactions(
                userId, searchTerm, status, pageable);

        return projections.map(this::mapToListResponse);
    }

    public TransactionDetailResponse getTransactionDetail(Integer userId, Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        List<TransactionItemProjection> items = transactionDetailRepository.findByTransactionId(transactionId);

        List<TransactionItemResponse> itemResponses = items.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        return TransactionDetailResponse.builder()
                .transactionId(transaction.getTransactionId())
                .transactionCode(transaction.getTransactionCode())
                .transactionStatusId(transaction.getTransactionStatusId())
                .subtotal(transaction.getSubtotal())
                .shippingCost(transaction.getShippingCost())
                .discountAmount(transaction.getDiscountAmount())
                .taxAmount(transaction.getTaxAmount())
                .totalAmount(transaction.getTotalAmount())
                .shippingAddress(transaction.getShippingAddress())
                .shippingCity(transaction.getShippingCity())
                .shippingProvince(transaction.getShippingProvince())
                .paymentMethod(transaction.getPaymentMethod())
                .createdAt(transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .items(itemResponses)
                .build();
    }

    @Transactional
    public TransactionDetailResponse updateTransactionStatus(Integer userId, Integer transactionId,
                                                             UpdateTransactionStatusRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        transaction.setTransactionStatusId(request.getTransactionstatusId());

        if (request.getPaymentProofUrl() != null) {
            transaction.setPaymentProofUrl(request.getPaymentProofUrl());
        }

        if (TransactionStatusEnums.COMPLETED.getCode()  ==
                (request.getTransactionstatusId()) && transaction.getPaidAt() == null) {
            transaction.setPaidAt(LocalDateTime.now());
        }

        transactionRepository.save(transaction);
        log.info("Transaction status updated: {} -> {}", transaction.getTransactionCode(), TransactionStatusEnums
                .fromCode(request.getTransactionstatusId()));

        return getTransactionDetail(userId, transactionId);
    }

    private String generateTransactionCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "TRX-" + timestamp + "-" + random;
    }

    private TransactionListResponse mapToListResponse(TransactionListProjection p) {
        return TransactionListResponse.builder()
                .transactionId(p.getTransactionId())
                .transactionCode(p.getTransactionCode())
                .transactionStatus(p.getTransactionStatus())
                .totalAmount(p.getTotalAmount())
                .createdAt(p.getCreatedAt())
                .totalItems(p.getTotalItems())
                .build();
    }

    private TransactionItemResponse mapToItemResponse(TransactionItemProjection p) {
        return TransactionItemResponse.builder()
                .transactionDetailId(p.getTransactionDetailId())
                .productId(p.getProductId())
                .productName(p.getProductName())
                .productImageUrl(p.getProductImageUrl())
                .quantity(p.getQuantity())
                .pricePerUnit(p.getPricePerUnit())
                .subtotal(p.getSubtotal())
                .build();
    }
}
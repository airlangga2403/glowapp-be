package com.skincare.repository;

import com.skincare.entity.Transaction;
import com.skincare.projection.TransactionListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Get user transactions with filter and search
    @Query(value = """
        SELECT t.transaction_id as transactionId,
               t.transaction_code as transactionCode,
               t.transaction_status as transactionStatus,
               t.total_amount as totalAmount,
               TO_CHAR(t.created_at, 'YYYY-MM-DD HH24:MI:SS') as createdAt,
               (SELECT COUNT(*)
                FROM transaction_detail td
                WHERE td.transaction_id = t.transaction_id) as totalItems
        FROM transaction t
        WHERE t.user_id = :userId
          AND t.active_status = 1
          AND (:searchTerm IS NULL OR t.transaction_code LIKE CONCAT('%', :searchTerm, '%'))
          AND (:status IS NULL OR t.transaction_status = :status)
        ORDER BY t.created_at desc          
        """,
            countQuery = """
                SELECT COUNT(*) FROM transaction t 
                WHERE t.user_id = :userId 
                AND t.active_status = 1 
                AND (:searchTerm IS NULL OR t.transaction_code LIKE CONCAT('%', :searchTerm, '%')) 
                AND (:status IS NULL OR t.transaction_status = :status)
                """,
            nativeQuery = true)
    Page<TransactionListProjection> findUserTransactions(@Param("userId") Integer userId,
                                                         @Param("searchTerm") String searchTerm,
                                                         @Param("status") String status,
                                                         Pageable pageable);

    // Find by transaction code
    @Query(value = "SELECT * FROM transaction WHERE transaction_code = :code AND active_status = 1",
            nativeQuery = true)
    Optional<Transaction> findByTransactionCode(@Param("code") String code);
}


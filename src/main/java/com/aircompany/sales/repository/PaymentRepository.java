package com.aircompany.sales.repository;

import com.aircompany.sales.model.Payment;
import com.aircompany.sales.model.Payment.PaymentStatus;
import com.aircompany.sales.model.Payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payment by reservation ID
     */
    Optional<Payment> findByReservationId(Long reservationId);
    
    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Find payments by method
     */
    List<Payment> findByMethod(PaymentMethod method);
    
    /**
     * Find payments by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payments with loyalty points usage
     */
    @Query("SELECT p FROM Payment p WHERE p.loyaltyPointsUsed > 0")
    List<Payment> findPaymentsWithLoyaltyPoints();
    
    /**
     * Calculate total revenue for date range
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateRevenueForDateRange(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculate total loyalty points used in date range
     */
    @Query("SELECT SUM(p.loyaltyPointsUsed) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    Optional<Integer> calculateLoyaltyPointsUsedForDateRange(@Param("startDate") LocalDateTime startDate, 
                                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find failed payments for retry
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.createdAt >= :cutoffDate")
    List<Payment> findFailedPaymentsForRetry(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Count payments by method for analytics
     */
    @Query("SELECT p.method, COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate GROUP BY p.method")
    List<Object[]> countPaymentsByMethodForDateRange(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find payments by customer
     */
    @Query("SELECT p FROM Payment p WHERE p.reservation.customer.id = :customerId ORDER BY p.createdAt DESC")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);
}
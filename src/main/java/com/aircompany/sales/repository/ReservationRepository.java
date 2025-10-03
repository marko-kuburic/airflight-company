package com.aircompany.sales.repository;

import com.aircompany.sales.model.Reservation;
import com.aircompany.sales.model.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find reservation by reservation number
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    /**
     * Find reservations by customer ID
     */
    List<Reservation> findByCustomerId(Long customerId);
    
    /**
     * Find reservations by status
     */
    List<Reservation> findByStatus(ReservationStatus status);
    
    /**
     * Find reservations by customer and status
     */
    List<Reservation> findByCustomerIdAndStatus(Long customerId, ReservationStatus status);
    
    /**
     * Find reservations with expired offers that are still active
     */
    @Query("SELECT r FROM Reservation r WHERE r.offer.expiresAt IS NOT NULL AND r.offer.expiresAt <= :now AND r.status = 'PENDING'")
    List<Reservation> findWithExpiredOffers(@Param("now") LocalDateTime now);
    
    /**
     * Find reservations by date range
     */
    @Query("SELECT r FROM Reservation r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Reservation> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count reservations by status and date range for analytics
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status AND r.createdAt BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(@Param("status") ReservationStatus status, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find reservations for a specific flight
     */
    @Query("SELECT r FROM Reservation r WHERE r.offer.flight.id = :flightId")
    List<Reservation> findByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Check if reservation number exists
     */
    boolean existsByReservationNumber(String reservationNumber);
    
    /**
     * Find recent reservations for a customer (last 30 days)
     */
    @Query("SELECT r FROM Reservation r WHERE r.customer.id = :customerId AND r.createdAt >= :thirtyDaysAgo ORDER BY r.createdAt DESC")
    List<Reservation> findRecentByCustomerId(@Param("customerId") Long customerId, 
                                           @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
}
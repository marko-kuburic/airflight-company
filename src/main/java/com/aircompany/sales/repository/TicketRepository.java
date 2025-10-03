package com.aircompany.sales.repository;

import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.model.Ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * Find tickets by reservation ID
     */
    List<Ticket> findByReservationId(Long reservationId);
    
    /**
     * Find tickets by passenger ID
     */
    List<Ticket> findByPassengerId(Long passengerId);
    
    /**
     * Find tickets by status
     */
    List<Ticket> findByStatus(TicketStatus status);
    
    /**
     * Find tickets by reservation and status
     */
    List<Ticket> findByReservationIdAndStatus(Long reservationId, TicketStatus status);
    
    /**
     * Find ticket by seat number for a specific flight
     */
    @Query("SELECT t FROM Ticket t JOIN t.reservation r JOIN r.offer o JOIN o.flight f " +
           "WHERE f.id = :flightId AND t.seatNumber = :seatNumber")
    Optional<Ticket> findBySeatNumberAndFlightId(@Param("seatNumber") String seatNumber, 
                                                 @Param("flightId") Long flightId);
    
    /**
     * Check if seat is taken for a flight
     */
    @Query("SELECT COUNT(t) > 0 FROM Ticket t JOIN t.reservation r JOIN r.offer o JOIN o.flight f " +
           "WHERE f.id = :flightId AND t.seatNumber = :seatNumber AND t.status IN ('CONFIRMED', 'CREATED')")
    boolean isSeatTaken(@Param("flightId") Long flightId, @Param("seatNumber") String seatNumber);
    
    /**
     * Get all occupied seats for a flight
     */
    @Query("SELECT t.seatNumber FROM Ticket t JOIN t.reservation r JOIN r.offer o JOIN o.flight f " +
           "WHERE f.id = :flightId AND t.status IN ('CONFIRMED', 'CREATED') AND t.seatNumber IS NOT NULL")
    List<String> getOccupiedSeatsForFlight(@Param("flightId") Long flightId);
    
    /**
     * Count tickets by status for analytics
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.createdAt BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(@Param("status") TicketStatus status, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find tickets for a customer across all reservations
     */
    @Query("SELECT t FROM Ticket t WHERE t.reservation.customer.id = :customerId ORDER BY t.createdAt DESC")
    List<Ticket> findByCustomerId(@Param("customerId") Long customerId);
}
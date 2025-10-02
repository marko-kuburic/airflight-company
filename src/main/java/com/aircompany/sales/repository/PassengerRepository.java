package com.aircompany.sales.repository;

import com.aircompany.sales.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    /**
     * Find passenger by document number
     */
    Optional<Passenger> findByDocumentNumber(String documentNumber);
    
    /**
     * Find passengers by email
     */
    List<Passenger> findByEmail(String email);
    
    /**
     * Find passengers by first and last name
     */
    List<Passenger> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Check if passenger exists by document number
     */
    boolean existsByDocumentNumber(String documentNumber);
    
    /**
     * Find passengers with tickets for a specific reservation
     */
    @Query("SELECT DISTINCT p FROM Passenger p JOIN p.tickets t WHERE t.reservation.id = :reservationId")
    List<Passenger> findPassengersByReservationId(@Param("reservationId") Long reservationId);
    
    /**
     * Search passengers by name (case insensitive)
     */
    @Query("SELECT p FROM Passenger p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Passenger> searchByName(@Param("searchTerm") String searchTerm);
}
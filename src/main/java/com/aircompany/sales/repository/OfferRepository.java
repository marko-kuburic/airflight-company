package com.aircompany.sales.repository;

import com.aircompany.sales.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    /**
     * Find active offers
     */
    List<Offer> findByIsActiveTrue();
    
    /**
     * Find valid (active and not expired) offers
     */
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND (o.expiresAt IS NULL OR o.expiresAt > :now)")
    List<Offer> findValidOffers(@Param("now") LocalDateTime now);
    
    /**
     * Find offers by flight ID
     */
    List<Offer> findByFlightId(Long flightId);
    
    /**
     * Find expired offers that are still marked as active
     */
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND o.expiresAt IS NOT NULL AND o.expiresAt <= :now")
    List<Offer> findExpiredActiveOffers(@Param("now") LocalDateTime now);
    
    /**
     * Find offers within date range
     */
    @Query("SELECT o FROM Offer o WHERE o.startDate >= :startDate AND o.endDate <= :endDate")
    List<Offer> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find offers with discount greater than specified percentage
     */
    @Query("SELECT o FROM Offer o WHERE o.discountPercentage >= :minDiscount AND o.isActive = true")
    List<Offer> findByMinimumDiscount(@Param("minDiscount") java.math.BigDecimal minDiscount);
    
    /**
     * Search offers by title (case insensitive)
     */
    @Query("SELECT o FROM Offer o WHERE LOWER(o.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Offer> searchByTitle(@Param("searchTerm") String searchTerm);
}
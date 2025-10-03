package com.aircompany.sales.service;

import com.aircompany.flight.model.Flight;
import com.aircompany.sales.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

/**
 * Service for dynamic pricing based on demand, season, and class
 * Implements the pricing strategy described in the specification
 */
@Service
public class DynamicPricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicPricingService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Base pricing multipliers
    private static final BigDecimal HIGH_DEMAND_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal MEDIUM_DEMAND_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal LOW_DEMAND_MULTIPLIER = new BigDecimal("0.8");
    
    // Seasonal multipliers
    private static final BigDecimal PEAK_SEASON_MULTIPLIER = new BigDecimal("1.4");
    private static final BigDecimal HIGH_SEASON_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal OFF_SEASON_MULTIPLIER = new BigDecimal("0.9");
    
    // Weekend multiplier (Friday, Saturday, Sunday)
    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.15");
    
    // Last minute multipliers
    private static final BigDecimal LAST_MINUTE_DISCOUNT = new BigDecimal("0.7"); // 30% off
    private static final BigDecimal EARLY_BOOKING_DISCOUNT = new BigDecimal("0.85"); // 15% off
    
    /**
     * Get base price for a flight (without dynamic adjustments)
     */
    public BigDecimal getBasePrice(Flight flight) {
        // Base price calculation - simplified version
        // In real implementation, this would come from fare tables based on route distance, aircraft type, etc.
        
        if (flight.getOffers() != null && !flight.getOffers().isEmpty()) {
            // Return the base price of the first offer (typically economy)
            return flight.getOffers().get(0).getBasePrice();
        }
        
        // Fallback: calculate based on route distance (simplified)
        BigDecimal basePrice = new BigDecimal("100"); // Base fare
        
        if (flight.getRoute() != null && flight.getRoute().getTotalDistance() != null) {
            // Add distance-based pricing: 0.10 per km
            BigDecimal distancePrice = flight.getRoute().getTotalDistance().multiply(new BigDecimal("0.10"));
            basePrice = basePrice.add(distancePrice);
        }
        
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate current dynamic price based on all factors
     */
    public BigDecimal getCurrentPrice(Flight flight) {
        BigDecimal basePrice = getBasePrice(flight);
        
        // Apply demand-based multiplier
        BigDecimal demandMultiplier = calculateDemandMultiplier(flight);
        
        // Apply seasonal multiplier
        BigDecimal seasonalMultiplier = calculateSeasonalMultiplier(flight.getDepTime());
        
        // Apply weekend multiplier
        BigDecimal weekendMultiplier = calculateWeekendMultiplier(flight.getDepTime());
        
        // Apply time-based multiplier (early booking vs last minute)
        BigDecimal timeMultiplier = calculateTimeBasedMultiplier(flight.getDepTime());
        
        // Calculate final price
        BigDecimal finalPrice = basePrice
            .multiply(demandMultiplier)
            .multiply(seasonalMultiplier)
            .multiply(weekendMultiplier)
            .multiply(timeMultiplier);
        
        logger.debug("Dynamic pricing for flight {}: base={}, demand={}, season={}, weekend={}, time={}, final={}", 
            flight.getId(), basePrice, demandMultiplier, seasonalMultiplier, weekendMultiplier, timeMultiplier, finalPrice);
        
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate demand-based multiplier
     * Based on seats sold and booking velocity
     */
    private BigDecimal calculateDemandMultiplier(Flight flight) {
        int totalCapacity = flight.getAircraft().getCapacity();
        int occupiedSeats = getOccupiedSeatsCount(flight.getId());
        
        double occupancyRate = (double) occupiedSeats / totalCapacity;
        
        if (occupancyRate >= 0.8) {
            // High demand: 80%+ occupied
            return HIGH_DEMAND_MULTIPLIER;
        } else if (occupancyRate >= 0.5) {
            // Medium demand: 50-79% occupied
            return MEDIUM_DEMAND_MULTIPLIER;
        } else {
            // Low demand: <50% occupied
            return LOW_DEMAND_MULTIPLIER;
        }
    }
    
    /**
     * Calculate seasonal multiplier
     */
    private BigDecimal calculateSeasonalMultiplier(LocalDateTime departureTime) {
        int month = departureTime.getMonthValue();
        
        // Peak season: December, January, July, August
        if (month == 12 || month == 1 || month == 7 || month == 8) {
            return PEAK_SEASON_MULTIPLIER;
        }
        
        // High season: March, April, May, June, September, October
        if (month >= 3 && month <= 6 || month == 9 || month == 10) {
            return HIGH_SEASON_MULTIPLIER;
        }
        
        // Off season: February, November
        return OFF_SEASON_MULTIPLIER;
    }
    
    /**
     * Calculate weekend multiplier
     */
    private BigDecimal calculateWeekendMultiplier(LocalDateTime departureTime) {
        DayOfWeek dayOfWeek = departureTime.getDayOfWeek();
        
        if (dayOfWeek == DayOfWeek.FRIDAY || 
            dayOfWeek == DayOfWeek.SATURDAY || 
            dayOfWeek == DayOfWeek.SUNDAY) {
            return WEEKEND_MULTIPLIER;
        }
        
        return BigDecimal.ONE; // No weekend multiplier for weekdays
    }
    
    /**
     * Calculate time-based multiplier (early booking vs last minute)
     */
    private BigDecimal calculateTimeBasedMultiplier(LocalDateTime departureTime) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilDeparture = ChronoUnit.HOURS.between(now, departureTime);
        
        if (hoursUntilDeparture <= 24) {
            // Last minute booking (within 24 hours) - offer discount to fill seats
            return LAST_MINUTE_DISCOUNT;
        } else if (hoursUntilDeparture >= 30 * 24) {
            // Early booking (30+ days in advance) - offer discount for early commitment
            return EARLY_BOOKING_DISCOUNT;
        }
        
        return BigDecimal.ONE; // Standard pricing for normal booking window
    }
    
    /**
     * Get number of occupied seats for a flight
     */
    private int getOccupiedSeatsCount(Long flightId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM Ticket t " +
            "JOIN t.reservation r " +
            "JOIN r.offer o " +
            "JOIN o.flight f " +
            "WHERE f.id = :flightId " +
            "AND t.status IN ('CONFIRMED', 'USED') " +
            "AND r.status != 'CANCELLED'",
            Long.class
        );
        query.setParameter("flightId", flightId);
        
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }
    
    /**
     * Calculate promotional discount for loyalty members
     */
    public BigDecimal calculateLoyaltyDiscount(String loyaltyTier) {
        switch (loyaltyTier.toUpperCase()) {
            case "PLATINUM":
            case "DIAMOND":
                return new BigDecimal("0.15"); // 15% discount
            case "GOLD":
                return new BigDecimal("0.10"); // 10% discount
            case "SILVER":
                return new BigDecimal("0.05"); // 5% discount
            default:
                return BigDecimal.ZERO; // No discount for Bronze or no tier
        }
    }
    
    /**
     * Apply loyalty discount to price
     */
    public BigDecimal applyLoyaltyDiscount(BigDecimal price, String loyaltyTier) {
        BigDecimal discount = calculateLoyaltyDiscount(loyaltyTier);
        BigDecimal discountAmount = price.multiply(discount);
        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }
}
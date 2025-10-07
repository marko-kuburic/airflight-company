package com.aircompany.sales.service;

import com.aircompany.flight.model.Flight;
import com.aircompany.sales.model.Offer;
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
import java.util.List;
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
     * Get base price for a flight with pre-fetched offers to avoid lazy loading
     */
    public BigDecimal getBasePrice(Flight flight, List<Offer> offers) {
        // Base price calculation - simplified version
        // In real implementation, this would come from fare tables based on route distance, aircraft type, etc.
        
        if (offers != null && !offers.isEmpty()) {
            // Return the base price of the first offer (typically economy)
            return offers.get(0).getBasePrice();
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
        
        // Apply time-of-day multiplier (early morning/late night cheaper)
        BigDecimal timeOfDayMultiplier = calculateTimeOfDayMultiplier(flight.getDepTime());
        
        // Apply micro-fluctuation (±$0.50 - $2.00 per refresh to simulate real-time changes)
        BigDecimal microFluctuation = calculateMicroFluctuation(flight.getId());
        
        // Calculate final price
        BigDecimal finalPrice = basePrice
            .multiply(demandMultiplier)
            .multiply(seasonalMultiplier)
            .multiply(weekendMultiplier)
            .multiply(timeMultiplier)
            .multiply(timeOfDayMultiplier)
            .add(microFluctuation);
        
        logger.debug("Dynamic pricing for flight {}: base={}, demand={}, season={}, weekend={}, time={}, timeOfDay={}, fluctuation={}, final={}", 
            flight.getId(), basePrice, demandMultiplier, seasonalMultiplier, weekendMultiplier, timeMultiplier, timeOfDayMultiplier, microFluctuation, finalPrice);
        
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate current dynamic price based on all factors with pre-fetched offers
     */
    public BigDecimal getCurrentPrice(Flight flight, List<Offer> offers) {
        BigDecimal basePrice = getBasePrice(flight, offers);
        
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
     * Calculate time-of-day multiplier
     * Early morning (00:00-06:00) and late night (22:00-23:59): 10% cheaper
     * Peak hours (07:00-09:00, 17:00-20:00): 5% more expensive
     * Normal hours: standard pricing
     */
    private BigDecimal calculateTimeOfDayMultiplier(LocalDateTime departureTime) {
        int hour = departureTime.getHour();
        
        if (hour >= 0 && hour < 6 || hour >= 22) {
            // Red-eye flights: cheaper
            return new BigDecimal("0.90"); // 10% discount
        } else if ((hour >= 7 && hour < 10) || (hour >= 17 && hour < 21)) {
            // Peak business hours: more expensive
            return new BigDecimal("1.05"); // 5% premium
        }
        
        // Normal hours
        return BigDecimal.ONE;
    }
    
    /**
     * Calculate micro-fluctuation to simulate real-time price changes
     * Adds small random variations (±$0.50 to ±$2.00) based on:
     * - Current timestamp (changes every refresh)
     * - Flight ID (consistent per flight but different across flights)
     * - Market volatility simulation
     */
    private BigDecimal calculateMicroFluctuation(Long flightId) {
        // Use current time in seconds to create fluctuation that changes over time
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        
        // Create a pseudo-random seed that changes every ~30 seconds
        // This ensures prices fluctuate but not TOO rapidly
        long seed = (currentTimeSeconds / 30) + flightId;
        java.util.Random random = new java.util.Random(seed);
        
        // Generate fluctuation between -2.00 and +2.00
        double fluctuationRange = 4.0; // Total range: $4 (from -2 to +2)
        double fluctuation = (random.nextDouble() * fluctuationRange) - 2.0;
        
        return BigDecimal.valueOf(fluctuation).setScale(2, RoundingMode.HALF_UP);
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
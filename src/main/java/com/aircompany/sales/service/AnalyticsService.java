package com.aircompany.sales.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for analytics and reporting functionality
 * Implements the analytics requirements from the specification
 */
@Service
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Get occupancy statistics by cabin class
     */
    public Map<String, Object> getOccupancyByCabinClass(Long flightId, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting occupancy by cabin class for flight: {}, period: {} to {}", flightId, startDate, endDate);
        
        // Query to get sold seats per cabin class using the denormalized cabin_class_name field
        StringBuilder jpql = new StringBuilder(
            "SELECT t.cabinClassName as cabinClass, " +
            "COUNT(t.id) as soldSeats " +
            "FROM Ticket t " +
            "JOIN t.reservation r " +
            "WHERE t.status IN ('CONFIRMED', 'USED') " +
            "AND r.status != 'CANCELLED' " +
            "AND t.cabinClassName IS NOT NULL "
        );
        
        if (flightId != null) {
            jpql.append("AND r.offer.flight.id = :flightId ");
        }
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(t.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        jpql.append("GROUP BY t.cabinClassName");
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        
        if (flightId != null) {
            query.setParameter("flightId", flightId);
        }
        
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        
        List<Object[]> results = query.getResultList();
        
        // Define total capacity per cabin class (only Business and Economy)
        Map<String, Integer> capacityMap = new HashMap<>();
        capacityMap.put("ECONOMY", 300);
        capacityMap.put("BUSINESS", 50);
        
        // Create a map to store sold seats per cabin class
        Map<String, Long> soldSeatsMap = new HashMap<>();
        for (Object[] result : results) {
            String cabinClass = ((String) result[0]).toUpperCase();
            Long soldSeats = (Long) result[1];
            soldSeatsMap.put(cabinClass, soldSeats);
        }
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> cabinStats = new ArrayList<>();
        
        // Always include both Economy and Business, even if no tickets sold
        for (Map.Entry<String, Integer> entry : capacityMap.entrySet()) {
            String cabinClass = entry.getKey();
            Integer totalCapacity = entry.getValue();
            Long soldSeats = soldSeatsMap.getOrDefault(cabinClass, 0L);
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("cabinClass", cabinClass.substring(0, 1).toUpperCase() + cabinClass.substring(1).toLowerCase());
            stat.put("soldSeats", soldSeats);
            stat.put("totalCapacity", totalCapacity);
            
            Double occupancyRate = totalCapacity > 0 ? 
                (soldSeats.doubleValue() / totalCapacity.doubleValue()) * 100 : 0.0;
            stat.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
            
            cabinStats.add(stat);
        }
        
        // Add cabin classes with 0 sold seats
        for (String cabinClass : capacityMap.keySet()) {
            boolean found = cabinStats.stream()
                .anyMatch(stat -> stat.get("cabinClass").toString().equalsIgnoreCase(cabinClass));
            
            if (!found) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("cabinClass", cabinClass.substring(0, 1) + cabinClass.substring(1).toLowerCase()); // Capitalize
                stat.put("soldSeats", 0L);
                stat.put("totalCapacity", capacityMap.get(cabinClass));
                stat.put("occupancyRate", 0.0);
                
                cabinStats.add(stat);
            }
        }
        
        response.put("cabinClassStats", cabinStats);
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Get occupancy statistics by season
     */
    public Map<String, Object> getOccupancyBySeason(int year) {
        logger.info("Getting seasonal occupancy for year: {}", year);
        
        // Define seasons
        Map<String, LocalDate[]> seasons = new HashMap<>();
        seasons.put("Winter", new LocalDate[]{LocalDate.of(year, 12, 1), LocalDate.of(year + 1, 2, 28)});
        seasons.put("Spring", new LocalDate[]{LocalDate.of(year, 3, 1), LocalDate.of(year, 5, 31)});
        seasons.put("Summer", new LocalDate[]{LocalDate.of(year, 6, 1), LocalDate.of(year, 8, 31)});
        seasons.put("Autumn", new LocalDate[]{LocalDate.of(year, 9, 1), LocalDate.of(year, 11, 30)});
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> seasonalStats = new ArrayList<>();
        
        for (Map.Entry<String, LocalDate[]> season : seasons.entrySet()) {
            Map<String, Object> seasonData = getSeasonalData(season.getKey(), season.getValue()[0], season.getValue()[1]);
            seasonalStats.add(seasonData);
        }
        
        response.put("seasonalStats", seasonalStats);
        response.put("year", year);
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Get cancellation rate statistics
     */
    public Map<String, Object> getCancellationRate(LocalDate startDate, LocalDate endDate, String reason) {
        logger.info("Getting cancellation rate for period: {} to {}", startDate, endDate);
        
        // Total tickets issued
        StringBuilder totalJpql = new StringBuilder(
            "SELECT COUNT(t) FROM Ticket t WHERE 1=1 "
        );
        
        if (startDate != null && endDate != null) {
            totalJpql.append("AND DATE(t.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        TypedQuery<Long> totalQuery = entityManager.createQuery(totalJpql.toString(), Long.class);
        if (startDate != null && endDate != null) {
            totalQuery.setParameter("startDate", startDate);
            totalQuery.setParameter("endDate", endDate);
        }
        
        Long totalTickets = totalQuery.getSingleResult();
        
        // Cancelled tickets (status = CANCELLED or REFUNDED)
        StringBuilder cancelledJpql = new StringBuilder(
            "SELECT COUNT(t) FROM Ticket t WHERE (t.status = 'CANCELLED' OR t.status = 'REFUNDED') "
        );
        
        if (startDate != null && endDate != null) {
            cancelledJpql.append("AND DATE(t.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        TypedQuery<Long> cancelledQuery = entityManager.createQuery(cancelledJpql.toString(), Long.class);
        if (startDate != null && endDate != null) {
            cancelledQuery.setParameter("startDate", startDate);
            cancelledQuery.setParameter("endDate", endDate);
        }
        
        Long cancelledTickets = cancelledQuery.getSingleResult();
        
        Double cancellationRate = totalTickets > 0 ? 
            (cancelledTickets.doubleValue() / totalTickets.doubleValue()) * 100 : 0.0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalTickets", totalTickets);
        response.put("cancelledTickets", cancelledTickets);
        response.put("cancellationRate", Math.round(cancellationRate * 100.0) / 100.0);
        response.put("period", Map.of("startDate", startDate, "endDate", endDate));
        response.put("generatedAt", LocalDateTime.now());
        
        // Optional: Get cancellation reasons if available (from a hypothetical cancellation_reasons field)
        // For now, return empty map as we don't have a cancellation reason field
        response.put("cancellationReasons", new HashMap<>());
        
        return response;
    }
    
    /**
     * Get financial indicators
     */
    public Map<String, Object> getFinancialIndicators(Long flightId, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting financial indicators for flight: {}, period: {} to {}", flightId, startDate, endDate);
        
        StringBuilder jpql = new StringBuilder(
            "SELECT " +
            "SUM(p.amount), " +
            "AVG(p.amount), " +
            "COUNT(p.id) " +
            "FROM Payment p " +
            "WHERE p.status = 'COMPLETED' "
        );
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(p.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        
        List<Object[]> results = query.getResultList();
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!results.isEmpty()) {
                Object[] result = results.get(0);
                
                // Handle SUM(p.amount) - can be Double or BigDecimal
                Double totalRevenueDouble = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
                BigDecimal totalRevenue = BigDecimal.valueOf(totalRevenueDouble);
                
                // Handle AVG(t.price) - can be Double or BigDecimal
                Double avgTicketPriceDouble = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
                BigDecimal avgTicketPrice = BigDecimal.valueOf(avgTicketPriceDouble);
                
                Long totalTickets = (Long) result[2];
                
                response.put("totalRevenue", totalRevenue.setScale(2, RoundingMode.HALF_UP));
                response.put("averageTicketPrice", avgTicketPrice.setScale(2, RoundingMode.HALF_UP));
                response.put("totalTicketsSold", totalTickets != null ? totalTickets : 0L);
                
                if (totalTickets != null && totalTickets > 0) {
                    BigDecimal revenuePerSeat = totalRevenue.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP);
                    response.put("revenuePerSeat", revenuePerSeat);
                } else {
                    response.put("revenuePerSeat", BigDecimal.ZERO);
                }
            } else {
                response.put("totalRevenue", BigDecimal.ZERO);
                response.put("averageTicketPrice", BigDecimal.ZERO);
                response.put("totalTicketsSold", 0L);
                response.put("revenuePerSeat", BigDecimal.ZERO);
            }
        } catch (Exception e) {
            logger.error("Error getting financial indicators: " + e.getMessage(), e);
            response.put("totalRevenue", BigDecimal.ZERO);
            response.put("averageTicketPrice", BigDecimal.ZERO);
            response.put("totalTicketsSold", 0L);
            response.put("revenuePerSeat", BigDecimal.ZERO);
        }
        
        response.put("period", Map.of("startDate", startDate, "endDate", endDate));
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Get comprehensive sales report
     */
    public Map<String, Object> getSalesReport(LocalDate startDate, LocalDate endDate, String period) {
        logger.info("Generating sales report for period: {} to {}, granularity: {}", startDate, endDate, period);
        
        Map<String, Object> report = new HashMap<>();
        
        // Get overall statistics
        report.put("financialSummary", getFinancialIndicators(null, startDate, endDate));
        report.put("occupancySummary", getOccupancyByCabinClass(null, startDate, endDate));
        report.put("cancellationSummary", getCancellationRate(startDate, endDate, null));
        
        // Add period details
        report.put("reportPeriod", period);
        report.put("dateRange", Map.of("startDate", startDate, "endDate", endDate));
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }
    
    /**
     * Get route performance statistics
     */
    public Map<String, Object> getRoutePerformance(Long routeId, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting route performance for route: {}, period: {} to {}", routeId, startDate, endDate);
        
        StringBuilder jpql = new StringBuilder(
            "SELECT " +
            "r.name, " +
            "COUNT(t), " +
            "SUM(p.amount), " +
            "AVG(t.price) " +
            "FROM Ticket t " +
            "JOIN t.reservation res " +
            "JOIN res.payment p " +
            "JOIN res.offer o " +
            "JOIN o.flight f " +
            "JOIN f.route r " +
            "WHERE t.status IN ('CONFIRMED', 'USED') " +
            "AND res.status != 'CANCELLED' " +
            "AND p.status = 'COMPLETED' "
        );
        
        if (routeId != null) {
            jpql.append("AND r.id = :routeId ");
        }
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(p.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        jpql.append("GROUP BY r.id, r.name");
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        
        if (routeId != null) {
            query.setParameter("routeId", routeId);
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        
        List<Object[]> results = query.getResultList();
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> routeStats = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("routeName", result[0]);
            stat.put("ticketsSold", result[1]);
            
            // Handle SUM(p.amount) - can be Double or BigDecimal
            Double totalRevenueDouble = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;
            stat.put("totalRevenue", BigDecimal.valueOf(totalRevenueDouble).setScale(2, RoundingMode.HALF_UP));
            
            // Handle AVG(t.price) - can be Double or BigDecimal
            Double avgPriceDouble = result[3] != null ? ((Number) result[3]).doubleValue() : 0.0;
            stat.put("averageTicketPrice", BigDecimal.valueOf(avgPriceDouble).setScale(2, RoundingMode.HALF_UP));
            
            routeStats.add(stat);
        }
        
        response.put("routePerformance", routeStats);
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Get loyalty program statistics
     */
    public Map<String, Object> getLoyaltyStatistics() {
        logger.info("Getting loyalty program statistics");
        
        // Get member count by tier
        TypedQuery<Object[]> tierQuery = entityManager.createQuery(
            "SELECT l.tier, COUNT(l) FROM Loyalty l GROUP BY l.tier",
            Object[].class
        );
        
        List<Object[]> tierResults = tierQuery.getResultList();
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Long> membersByTier = new HashMap<>();
        
        for (Object[] result : tierResults) {
            membersByTier.put(result[0].toString(), (Long) result[1]);
        }
        
        // Get total points distributed
        TypedQuery<Long> pointsQuery = entityManager.createQuery(
            "SELECT SUM(l.points) FROM Loyalty l",
            Long.class
        );
        
        Long totalPoints = pointsQuery.getSingleResult();
        
        response.put("membersByTier", membersByTier);
        response.put("totalPointsDistributed", totalPoints != null ? totalPoints : 0L);
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Get dashboard summary data
     */
    public Map<String, Object> getDashboardSummary() {
        logger.info("Getting dashboard summary");
        
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);
        
        Map<String, Object> summary = new HashMap<>();
        
        // Get current month statistics
        summary.put("currentMonth", getFinancialIndicators(null, lastMonth, today));
        summary.put("occupancy", getOccupancyByCabinClass(null, lastMonth, today));
        summary.put("cancellations", getCancellationRate(lastMonth, today, null));
        summary.put("loyalty", getLoyaltyStatistics());
        
        summary.put("generatedAt", LocalDateTime.now());
        
        return summary;
    }
    
    /**
     * Helper method to get seasonal data
     */
    private Map<String, Object> getSeasonalData(String seasonName, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> occupancyData = getOccupancyByCabinClass(null, startDate, endDate);
        Map<String, Object> seasonData = new HashMap<>();
        
        // Calculate aggregate stats for the season
        String jpql = "SELECT COUNT(t), SUM(t.price), AVG(t.price) FROM Ticket t " +
                     "JOIN t.reservation r " +
                     "WHERE t.status IN ('CONFIRMED', 'USED') " +
                     "AND r.status != 'CANCELLED' " +
                     "AND DATE(t.createdAt) BETWEEN :startDate AND :endDate";
        
        jakarta.persistence.Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        Object[] result = (Object[]) query.getSingleResult();
        Long ticketsSold = (Long) result[0];
        Double totalRevenue = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        Double avgPrice = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;
        
        // Calculate overall occupancy rate from cabin class data
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cabinStats = (List<Map<String, Object>>) occupancyData.get("cabinClassStats");
        double totalSoldSeats = 0;
        double totalCapacity = 0;
        
        if (cabinStats != null) {
            for (Map<String, Object> stat : cabinStats) {
                totalSoldSeats += ((Number) stat.get("soldSeats")).doubleValue();
                totalCapacity += ((Number) stat.get("totalCapacity")).doubleValue();
            }
        }
        
        double occupancyRate = totalCapacity > 0 ? (totalSoldSeats / totalCapacity) * 100 : 0.0;
        
        seasonData.put("season", seasonName);
        seasonData.put("startDate", startDate);
        seasonData.put("endDate", endDate);
        seasonData.put("ticketsSold", ticketsSold);
        seasonData.put("totalRevenue", totalRevenue);
        seasonData.put("averageTicketPrice", avgPrice);
        seasonData.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
        seasonData.put("occupancyData", occupancyData);
        
        return seasonData;
    }
}
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
        
        StringBuilder jpql = new StringBuilder(
            "SELECT cc.name, COUNT(t), f.aircraft.capacity " +
            "FROM Ticket t " +
            "JOIN t.reservation r " +
            "JOIN r.offer o " +
            "JOIN o.fare f " +
            "JOIN f.cabinClass cc " +
            "JOIN o.flight fl " +
            "WHERE t.status IN ('CONFIRMED', 'USED') " +
            "AND r.status != 'CANCELLED' "
        );
        
        if (flightId != null) {
            jpql.append("AND fl.id = :flightId ");
        }
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(fl.depTime) BETWEEN :startDate AND :endDate ");
        }
        
        jpql.append("GROUP BY cc.name, f.aircraft.capacity");
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        
        if (flightId != null) {
            query.setParameter("flightId", flightId);
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        
        List<Object[]> results = query.getResultList();
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> cabinStats = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("cabinClass", result[0]);
            stat.put("soldSeats", result[1]);
            stat.put("totalCapacity", result[2]);
            
            Long soldSeats = (Long) result[1];
            Integer totalCapacity = (Integer) result[2];
            Double occupancyRate = (soldSeats.doubleValue() / totalCapacity.doubleValue()) * 100;
            stat.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
            
            cabinStats.add(stat);
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
        
        // Total reservations
        StringBuilder totalJpql = new StringBuilder(
            "SELECT COUNT(r) FROM Reservation r WHERE 1=1 "
        );
        
        if (startDate != null && endDate != null) {
            totalJpql.append("AND DATE(r.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        TypedQuery<Long> totalQuery = entityManager.createQuery(totalJpql.toString(), Long.class);
        if (startDate != null && endDate != null) {
            totalQuery.setParameter("startDate", startDate);
            totalQuery.setParameter("endDate", endDate);
        }
        
        Long totalReservations = totalQuery.getSingleResult();
        
        // Cancelled reservations
        StringBuilder cancelledJpql = new StringBuilder(
            "SELECT COUNT(r) FROM Reservation r WHERE r.status = 'CANCELLED' "
        );
        
        if (startDate != null && endDate != null) {
            cancelledJpql.append("AND DATE(r.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        TypedQuery<Long> cancelledQuery = entityManager.createQuery(cancelledJpql.toString(), Long.class);
        if (startDate != null && endDate != null) {
            cancelledQuery.setParameter("startDate", startDate);
            cancelledQuery.setParameter("endDate", endDate);
        }
        
        Long cancelledReservations = cancelledQuery.getSingleResult();
        
        Double cancellationRate = totalReservations > 0 ? 
            (cancelledReservations.doubleValue() / totalReservations.doubleValue()) * 100 : 0.0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalReservations", totalReservations);
        response.put("cancelledReservations", cancelledReservations);
        response.put("cancellationRate", Math.round(cancellationRate * 100.0) / 100.0);
        response.put("period", Map.of("startDate", startDate, "endDate", endDate));
        response.put("generatedAt", LocalDateTime.now());
        
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
            "AVG(t.price), " +
            "COUNT(t), " +
            "fl.id " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "JOIN r.tickets t " +
            "JOIN r.offer o " +
            "JOIN o.flight fl " +
            "WHERE p.status = 'COMPLETED' " +
            "AND r.status != 'CANCELLED' "
        );
        
        if (flightId != null) {
            jpql.append("AND fl.id = :flightId ");
        }
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(p.createdAt) BETWEEN :startDate AND :endDate ");
        }
        
        if (flightId != null) {
            jpql.append("GROUP BY fl.id");
        }
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        
        if (flightId != null) {
            query.setParameter("flightId", flightId);
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        
        List<Object[]> results = query.getResultList();
        
        Map<String, Object> response = new HashMap<>();
        
        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            BigDecimal totalRevenue = (BigDecimal) result[0];
            BigDecimal avgTicketPrice = (BigDecimal) result[1];
            Long totalTickets = (Long) result[2];
            
            response.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            response.put("averageTicketPrice", avgTicketPrice != null ? avgTicketPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            response.put("totalTicketsSold", totalTickets != null ? totalTickets : 0L);
            
            if (totalTickets != null && totalTickets > 0) {
                BigDecimal revenuePerSeat = totalRevenue != null ? 
                    totalRevenue.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                response.put("revenuePerSeat", revenuePerSeat);
            }
        } else {
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
            "JOIN res.offer o " +
            "JOIN o.flight f " +
            "JOIN f.route r " +
            "JOIN res.payment p " +
            "WHERE t.status IN ('CONFIRMED', 'USED') " +
            "AND res.status != 'CANCELLED' " +
            "AND p.status = 'COMPLETED' "
        );
        
        if (routeId != null) {
            jpql.append("AND r.id = :routeId ");
        }
        
        if (startDate != null && endDate != null) {
            jpql.append("AND DATE(f.depTime) BETWEEN :startDate AND :endDate ");
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
            stat.put("totalRevenue", result[2]);
            stat.put("averageTicketPrice", result[3]);
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
        
        seasonData.put("season", seasonName);
        seasonData.put("startDate", startDate);
        seasonData.put("endDate", endDate);
        seasonData.put("occupancyData", occupancyData);
        
        return seasonData;
    }
}
package com.aircompany.sales.controller;

import com.aircompany.sales.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    /**
     * Get occupancy statistics by cabin class
     */
    @GetMapping("/occupancy/cabin-class")
    public ResponseEntity<?> getOccupancyByCabinClass(@RequestParam(required = false) Long flightId,
                                                     @RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> occupancyStats = analyticsService.getOccupancyByCabinClass(flightId, startDate, endDate);
            return ResponseEntity.ok(occupancyStats);
        } catch (Exception e) {
            logger.error("Error getting occupancy by cabin class: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting occupancy statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get occupancy statistics by season
     */
    @GetMapping("/occupancy/season")
    public ResponseEntity<?> getOccupancyBySeason(@RequestParam(defaultValue = "2024") int year) {
        try {
            Map<String, Object> seasonalStats = analyticsService.getOccupancyBySeason(year);
            return ResponseEntity.ok(seasonalStats);
        } catch (Exception e) {
            logger.error("Error getting seasonal occupancy: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting seasonal statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get cancellation rate statistics
     */
    @GetMapping("/cancellation-rate")
    public ResponseEntity<?> getCancellationRate(@RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate,
                                                @RequestParam(required = false) String reason) {
        try {
            Map<String, Object> cancellationStats = analyticsService.getCancellationRate(startDate, endDate, reason);
            return ResponseEntity.ok(cancellationStats);
        } catch (Exception e) {
            logger.error("Error getting cancellation rate: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting cancellation statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get financial indicators
     */
    @GetMapping("/financial")
    public ResponseEntity<?> getFinancialIndicators(@RequestParam(required = false) Long flightId,
                                                   @RequestParam(required = false) LocalDate startDate,
                                                   @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> financialStats = analyticsService.getFinancialIndicators(flightId, startDate, endDate);
            return ResponseEntity.ok(financialStats);
        } catch (Exception e) {
            logger.error("Error getting financial indicators: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting financial statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get comprehensive sales report
     */
    @GetMapping("/sales-report")
    public ResponseEntity<?> getSalesReport(@RequestParam(required = false) LocalDate startDate,
                                           @RequestParam(required = false) LocalDate endDate,
                                           @RequestParam(defaultValue = "DAILY") String period) {
        try {
            Map<String, Object> salesReport = analyticsService.getSalesReport(startDate, endDate, period);
            return ResponseEntity.ok(salesReport);
        } catch (Exception e) {
            logger.error("Error generating sales report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error generating sales report: " + e.getMessage()));
        }
    }
    
    /**
     * Get route performance statistics
     */
    @GetMapping("/routes/performance")
    public ResponseEntity<?> getRoutePerformance(@RequestParam(required = false) Long routeId,
                                                @RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> routeStats = analyticsService.getRoutePerformance(routeId, startDate, endDate);
            return ResponseEntity.ok(routeStats);
        } catch (Exception e) {
            logger.error("Error getting route performance: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting route statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get loyalty program statistics
     */
    @GetMapping("/loyalty")
    public ResponseEntity<?> getLoyaltyStatistics() {
        try {
            Map<String, Object> loyaltyStats = analyticsService.getLoyaltyStatistics();
            return ResponseEntity.ok(loyaltyStats);
        } catch (Exception e) {
            logger.error("Error getting loyalty statistics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting loyalty statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get dashboard summary data
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            Map<String, Object> dashboardData = analyticsService.getDashboardSummary();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            logger.error("Error getting dashboard data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting dashboard data: " + e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
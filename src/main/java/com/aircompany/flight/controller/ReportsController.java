package com.aircompany.flight.controller;

import com.aircompany.flight.model.Aircraft;
import com.aircompany.flight.model.Flight;
import com.aircompany.flight.model.Maintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get flights per aircraft and route report
     */
    @GetMapping("/flights-per-aircraft-route")
    public ResponseEntity<?> getFlightsPerAircraftRoute(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            String jpql = "SELECT f FROM Flight f " +
                         "LEFT JOIN FETCH f.aircraft " +
                         "LEFT JOIN FETCH f.route ";
            
            if (startDate != null && endDate != null) {
                jpql += "WHERE f.depTime >= :startDate AND f.depTime <= :endDate";
            }
            
            TypedQuery<Flight> query = entityManager.createQuery(jpql, Flight.class);
            
            if (startDate != null && endDate != null) {
                query.setParameter("startDate", LocalDateTime.parse(startDate + "T00:00:00"));
                query.setParameter("endDate", LocalDateTime.parse(endDate + "T23:59:59"));
            }
            
            List<Flight> flights = query.getResultList();
            
            // Group by aircraft
            Map<String, Map<String, Object>> grouped = new HashMap<>();
            
            for (Flight flight : flights) {
                if (flight.getAircraft() != null) {
                    String key = flight.getAircraft().getModel() + "-" + flight.getAircraft().getRegistration();
                    
                    grouped.putIfAbsent(key, new HashMap<>());
                    Map<String, Object> data = grouped.get(key);
                    
                    data.put("aircraft", flight.getAircraft().getModel());
                    data.put("registration", flight.getAircraft().getRegistration());
                    data.put("count", (Integer) data.getOrDefault("count", 0) + 1);
                    
                    @SuppressWarnings("unchecked")
                    Set<Long> routes = (Set<Long>) data.getOrDefault("routes", new HashSet<Long>());
                    if (flight.getRoute() != null) {
                        routes.add(flight.getRoute().getId());
                    }
                    data.put("routes", routes);
                }
            }
            
            // Convert to list format
            List<Map<String, Object>> result = grouped.values().stream()
                .map(data -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("aircraft", data.get("aircraft") + "-" + data.get("registration"));
                    item.put("count", data.get("count"));
                    @SuppressWarnings("unchecked")
                    Set<Long> routes = (Set<Long>) data.get("routes");
                    item.put("routes", routes.size());
                    return item;
                })
                .collect(Collectors.toList());
            
            logger.info("Flights per aircraft/route report generated: {} entries", result.size());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error generating flights per aircraft/route report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error generating report: " + e.getMessage()));
        }
    }

    /**
     * Get flight statuses report
     */
    @GetMapping("/flight-statuses")
    public ResponseEntity<?> getFlightStatuses(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            String jpql = "SELECT f.status, COUNT(f) FROM Flight f ";
            
            if (startDate != null && endDate != null) {
                jpql += "WHERE f.depTime >= :startDate AND f.depTime <= :endDate ";
            }
            
            jpql += "GROUP BY f.status";
            
            TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
            
            if (startDate != null && endDate != null) {
                query.setParameter("startDate", LocalDateTime.parse(startDate + "T00:00:00"));
                query.setParameter("endDate", LocalDateTime.parse(endDate + "T23:59:59"));
            }
            
            List<Object[]> results = query.getResultList();
            
            Map<String, Long> statuses = new HashMap<>();
            statuses.put("completed", 0L);
            statuses.put("canceled", 0L);
            statuses.put("scheduled", 0L);
            statuses.put("boarding", 0L);
            statuses.put("departed", 0L);
            statuses.put("delayed", 0L);
            
            for (Object[] row : results) {
                String status = row[0].toString();
                Long count = ((Number) row[1]).longValue();
                
                // Map statuses to our display names
                switch (status) {
                    case "LANDED":
                        statuses.put("completed", statuses.get("completed") + count);
                        break;
                    case "CANCELLED":
                        statuses.put("canceled", statuses.get("canceled") + count);
                        break;
                    case "SCHEDULED":
                        statuses.put("scheduled", statuses.get("scheduled") + count);
                        break;
                    case "BOARDING":
                        statuses.put("boarding", statuses.get("boarding") + count);
                        break;
                    case "DEPARTED":
                    case "IN_FLIGHT":
                        statuses.put("departed", statuses.get("departed") + count);
                        break;
                    case "DELAYED":
                        statuses.put("delayed", statuses.get("delayed") + count);
                        break;
                }
            }
            
            logger.info("Flight statuses report generated");
            return ResponseEntity.ok(statuses);
            
        } catch (Exception e) {
            logger.error("Error generating flight statuses report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error generating report: " + e.getMessage()));
        }
    }

    /**
     * Get average time between services for aircraft
     */
    @GetMapping("/avg-time-between-services")
    public ResponseEntity<?> getAvgTimeBetweenServices() {
        try {
            // Get all aircraft
            String aircraftJpql = "SELECT a FROM Aircraft a";
            TypedQuery<Aircraft> aircraftQuery = entityManager.createQuery(aircraftJpql, Aircraft.class);
            aircraftQuery.setMaxResults(10); // Limit to top 10 aircraft
            List<Aircraft> aircraftList = aircraftQuery.getResultList();
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Aircraft aircraft : aircraftList) {
                // Get maintenance services for this aircraft, ordered by date
                String serviceJpql = "SELECT m FROM Maintenance m WHERE m.aircraft.id = :aircraftId ORDER BY m.startDate ASC";
                TypedQuery<Maintenance> serviceQuery = entityManager.createQuery(serviceJpql, Maintenance.class);
                serviceQuery.setParameter("aircraftId", aircraft.getId());
                List<Maintenance> services = serviceQuery.getResultList();
                
                if (services.size() > 1) {
                    // Calculate average time between services (end of previous to start of next)
                    long totalHours = 0;
                    int intervals = 0;
                    
                    for (int i = 1; i < services.size(); i++) {
                        LocalDateTime prevEndDate = services.get(i - 1).getEndDate();
                        LocalDateTime currStartDate = services.get(i).getStartDate();
                        
                        if (prevEndDate != null && currStartDate != null) {
                            long hours = ChronoUnit.HOURS.between(prevEndDate, currStartDate);
                            if (hours > 0) { // Only count positive intervals
                                totalHours += hours;
                                intervals++;
                            }
                        }
                    }
                    
                    if (intervals > 0) {
                        long avgHours = totalHours / intervals;
                        
                        Map<String, Object> item = new HashMap<>();
                        item.put("aircraft", aircraft.getModel() + "-" + aircraft.getRegistration());
                        item.put("avgHours", avgHours);
                        item.put("intervals", intervals);
                        result.add(item);
                    }
                }
            }
            
            logger.info("Average time between services report generated: {} entries", result.size());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error generating avg time between services report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error generating report: " + e.getMessage()));
        }
    }

    /**
     * Get plan change frequency report
     * Counts delayed and canceled flights per route
     */
    @GetMapping("/plan-change-frequency")
    public ResponseEntity<?> getPlanChangeFrequency() {
        try {
            // Get all flights with their routes and statuses
            String jpql = "SELECT f FROM Flight f LEFT JOIN FETCH f.route";
            TypedQuery<Flight> query = entityManager.createQuery(jpql, Flight.class);
            List<Flight> flights = query.getResultList();
            
            // Group by route and count delayed/canceled flights
            Map<Long, Map<String, Object>> routeStats = new HashMap<>();
            
            for (Flight flight : flights) {
                logger.debug("Processing flight: {} with status: {} and route: {}", 
                    flight.getFlightNumber(), flight.getStatus(), 
                    flight.getRoute() != null ? flight.getRoute().getName() : "null");
                
                if (flight.getRoute() != null) {
                    Long routeId = flight.getRoute().getId();
                    routeStats.putIfAbsent(routeId, new HashMap<>());
                    
                    Map<String, Object> stats = routeStats.get(routeId);
                    stats.put("routeId", routeId);
                    stats.put("routeName", flight.getRoute().getName());
                    
                    // Count total flights for this route
                    int totalFlights = (Integer) stats.getOrDefault("totalFlights", 0) + 1;
                    stats.put("totalFlights", totalFlights);
                    
                    // Count delayed OR canceled flights (not both - a flight can't be both)
                    if (Flight.FlightStatus.DELAYED.equals(flight.getStatus()) || Flight.FlightStatus.CANCELLED.equals(flight.getStatus())) {
                        int changeCount = (Integer) stats.getOrDefault("changeCount", 0) + 1;
                        stats.put("changeCount", changeCount);
                        logger.debug("Flight {} is {} - incrementing change count for route {}", 
                            flight.getFlightNumber(), flight.getStatus(), flight.getRoute().getName());
                    }
                } else {
                    logger.debug("Flight {} has no route", flight.getFlightNumber());
                }
            }
            
            // Calculate average changes and find route with most changes
            double totalAvgChanges = 0;
            String mostChangesRoute = "";
            double maxChangeRate = 0;
            int routesWithData = 0;
            
            for (Map<String, Object> stats : routeStats.values()) {
                int totalFlights = (Integer) stats.getOrDefault("totalFlights", 0);
                int changeCount = (Integer) stats.getOrDefault("changeCount", 0);
                
                logger.debug("Route: {} - Total flights: {}, Change count: {}", 
                    stats.get("routeName"), totalFlights, changeCount);
                
                if (totalFlights > 0) {
                    double changeRate = (double) changeCount / totalFlights;
                    totalAvgChanges += changeRate;
                    routesWithData++;
                    
                    logger.debug("Route: {} - Change rate: {}", stats.get("routeName"), changeRate);
                    
                    if (changeRate > maxChangeRate) {
                        maxChangeRate = changeRate;
                        mostChangesRoute = (String) stats.get("routeName") + " (" + String.format("%.1f", changeRate * 100) + "%)";
                        logger.debug("New max change rate: {} for route: {}", maxChangeRate, stats.get("routeName"));
                    }
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            if (routesWithData > 0) {
                result.put("avgChanges", totalAvgChanges / routesWithData);
            } else {
                result.put("avgChanges", 0.0);
            }
            result.put("mostChanges", mostChangesRoute.isEmpty() ? "No data available" : mostChangesRoute);
            
            logger.info("Plan change frequency report generated: {} routes analyzed", routesWithData);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error generating plan change frequency report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error generating report: " + e.getMessage()));
        }
    }

    /**
     * Get all reports in one call
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllReports(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> allReports = new HashMap<>();
            
            // Get all reports
            ResponseEntity<?> flightsPerAircraft = getFlightsPerAircraftRoute(startDate, endDate);
            ResponseEntity<?> flightStatuses = getFlightStatuses(startDate, endDate);
            ResponseEntity<?> avgTimeBetween = getAvgTimeBetweenServices();
            ResponseEntity<?> planChanges = getPlanChangeFrequency();
            
            allReports.put("flightsPerAircraftRoute", flightsPerAircraft.getBody());
            allReports.put("flightStatuses", flightStatuses.getBody());
            allReports.put("avgTimeBetweenServices", avgTimeBetween.getBody());
            allReports.put("planChangeFrequency", planChanges.getBody());
            
            logger.info("All reports generated successfully");
            return ResponseEntity.ok(allReports);
            
        } catch (Exception e) {
            logger.error("Error generating all reports: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error generating reports: " + e.getMessage()));
        }
    }
}


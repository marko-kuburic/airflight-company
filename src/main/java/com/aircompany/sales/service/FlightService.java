package com.aircompany.sales.service;

import com.aircompany.flight.model.Flight;
import com.aircompany.flight.model.Airport;
import com.aircompany.flight.model.Aircraft;
import com.aircompany.sales.dto.FlightSearchRequest;
import com.aircompany.sales.dto.FlightSearchResponse;
import com.aircompany.sales.model.Offer;
import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private OfferService offerService;
    
    @Autowired
    private DynamicPricingService dynamicPricingService;
    
    /**
     * Search for flights based on search criteria
     * Supports "Anywhere" searches where origin and/or destination can be empty
     */
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest searchRequest) {
        logger.info("Searching flights from '{}' to '{}' on {}", 
            searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
        
        // Check for "Anywhere" searches
        boolean hasOrigin = searchRequest.getOrigin() != null && !searchRequest.getOrigin().trim().isEmpty();
        boolean hasDestination = searchRequest.getDestination() != null && !searchRequest.getDestination().trim().isEmpty();
        
        logger.info("Search type: hasOrigin={}, hasDestination={}", hasOrigin, hasDestination);
        
        // Build dynamic query based on search criteria
        StringBuilder jpql = new StringBuilder(
            "SELECT DISTINCT f FROM Flight f " +
            "JOIN FETCH f.route r " +
            "JOIN FETCH r.segments s " +
            "JOIN FETCH s.originAirport origin " +
            "JOIN FETCH s.destinationAirport dest " +
            "JOIN FETCH f.aircraft " +
            "WHERE f.depTime >= :startOfDay AND f.depTime < :endOfDay " +
            "AND f.status = :status "
        );
        
        // Add origin/destination filters if provided and not empty
        if (hasOrigin) {
            jpql.append("AND (origin.iataCode = :origin OR origin.name LIKE :originName) ");
        }
        
        if (hasDestination) {
            jpql.append("AND (dest.iataCode = :destination OR dest.name LIKE :destinationName) ");
        }
        
        jpql.append("ORDER BY f.depTime ASC");
        
        TypedQuery<Flight> query = entityManager.createQuery(jpql.toString(), Flight.class);
        
        // Set date range parameters
        LocalDateTime startOfDay = searchRequest.getDepartureDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        query.setParameter("startOfDay", startOfDay);
        query.setParameter("endOfDay", endOfDay);
        query.setParameter("status", Flight.FlightStatus.SCHEDULED);
        
        if (hasOrigin) {
            query.setParameter("origin", searchRequest.getOrigin().toUpperCase());
            query.setParameter("originName", "%" + searchRequest.getOrigin() + "%");
        }
        
        if (hasDestination) {
            query.setParameter("destination", searchRequest.getDestination().toUpperCase());
            query.setParameter("destinationName", "%" + searchRequest.getDestination() + "%");
        }
        
        // Limit results for "Anywhere" searches to avoid overwhelming the UI
        if (!hasOrigin && !hasDestination) {
            // Both are "Anywhere" - limit to 50 flights
            query.setMaxResults(50);
            logger.info("Anywhere to Anywhere search - limiting to 50 results");
        } else if (!hasOrigin || !hasDestination) {
            // One is "Anywhere" - limit to 100 flights
            query.setMaxResults(100);
            logger.info("Partial Anywhere search - limiting to 100 results");
        }
        
        List<Flight> flights = query.getResultList();
        logger.info("Found {} flights for search criteria", flights.size());
        
        // Convert to response DTOs with dynamic pricing
        return flights.stream()
            .map(this::convertToFlightResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get flight by ID
     */
    public Flight getFlightById(Long id) {
        return entityManager.find(Flight.class, id);
    }
    
    /**
     * Get all flights (for debugging purposes)
     */
    @Transactional(readOnly = true)
    public List<Flight> getAllFlights() {
        String jpql = "SELECT f FROM Flight f ORDER BY f.depTime ASC";
        TypedQuery<Flight> query = entityManager.createQuery(jpql, Flight.class);
        query.setMaxResults(100); // Limit to avoid too much data
        return query.getResultList();
    }
    
    /**
     * Get flight response DTO by ID with current pricing
     */
    @Transactional(readOnly = true)
    public FlightSearchResponse getFlightResponseById(Long id) {
        // Use JOIN FETCH to avoid lazy loading issues
        String jpql = "SELECT DISTINCT f FROM Flight f " +
                     "JOIN FETCH f.route r " +
                     "JOIN FETCH r.segments s " +
                     "JOIN FETCH s.originAirport " +
                     "JOIN FETCH s.destinationAirport " +
                     "JOIN FETCH f.aircraft " +
                     "WHERE f.id = :flightId";
        
        TypedQuery<Flight> query = entityManager.createQuery(jpql, Flight.class);
        query.setParameter("flightId", id);
        
        try {
            Flight flight = query.getSingleResult();
            return convertToFlightResponse(flight);
        } catch (NoResultException e) {
            throw new RuntimeException("Flight not found with ID: " + id);
        }
    }
    
    /**
     * Get occupied seats for a flight
     */
    public List<String> getOccupiedSeats(Long flightId) {
        TypedQuery<String> query = entityManager.createQuery(
            "SELECT t.seatNumber FROM Ticket t " +
            "JOIN t.reservation r " +
            "JOIN r.offer o " +
            "JOIN o.flight f " +
            "WHERE f.id = :flightId " +
            "AND t.seatNumber IS NOT NULL " +
            "AND t.status IN ('CONFIRMED', 'USED') " +
            "AND r.status != 'CANCELLED'",
            String.class
        );
        query.setParameter("flightId", flightId);
        
        return query.getResultList();
    }
    
    /**
     * Check if a seat is available
     */
    public boolean isSeatAvailable(Long flightId, String seatNumber) {
        List<String> occupiedSeats = getOccupiedSeats(flightId);
        return !occupiedSeats.contains(seatNumber);
    }
    
    /**
     * Get seat map configuration for a flight
     */
    public Map<String, Object> getSeatMap(Long flightId) {
        Flight flight = getFlightById(flightId);
        if (flight == null) {
            throw new RuntimeException("Flight not found");
        }
        
        Aircraft aircraft = flight.getAircraft();
        List<String> occupiedSeats = getOccupiedSeats(flightId);
        
        // Generate seat map based on aircraft capacity
        Map<String, Object> seatMap = new HashMap<>();
        seatMap.put("aircraftModel", aircraft.getModel());
        seatMap.put("totalCapacity", aircraft.getCapacity());
        seatMap.put("occupiedSeats", occupiedSeats);
        seatMap.put("availableSeats", aircraft.getCapacity() - occupiedSeats.size());
        
        // Generate available seat numbers (simplified)
        List<String> allSeats = generateSeatNumbers(aircraft.getCapacity());
        List<String> availableSeats = allSeats.stream()
            .filter(seat -> !occupiedSeats.contains(seat))
            .collect(Collectors.toList());
        
        seatMap.put("availableSeatNumbers", availableSeats);
        seatMap.put("seatConfiguration", generateSeatConfiguration(aircraft.getCapacity()));
        
        return seatMap;
    }
    
    /**
     * Convert Flight entity to FlightSearchResponse DTO
     */
    private FlightSearchResponse convertToFlightResponse(Flight flight) {
        List<Offer> offers = offerService.getActiveOffersForFlight(flight.getId());
        
        FlightSearchResponse response = new FlightSearchResponse(flight, offers);
        
        // Set flight number (generate if not exists)
        response.setFlightNumber(generateFlightNumber(flight));
        
        // Calculate duration
        response.setDuration(calculateDuration(flight.getDepTime(), flight.getArrTime()));
        
        // Get dynamic pricing
        response.setBasePrice(dynamicPricingService.getBasePrice(flight, offers));
        response.setCurrentPrice(dynamicPricingService.getCurrentPrice(flight, offers));
        
        // Get available seats
        List<String> occupiedSeats = getOccupiedSeats(flight.getId());
        response.setAvailableSeats(flight.getAircraft().getCapacity() - occupiedSeats.size());
        
        // Set offers information
        if (!offers.isEmpty()) {
            response.setDiscountPercentage(offers.get(0).getDiscountPercentage());
            List<FlightSearchResponse.OfferResponse> offerResponses = offers.stream()
                .map(offer -> {
                    FlightSearchResponse.OfferResponse offerResponse = new FlightSearchResponse.OfferResponse(offer);
                    // Calculate final price with discount
                    BigDecimal discountAmount = response.getCurrentPrice()
                        .multiply(offer.getDiscountPercentage())
                        .divide(new BigDecimal("100"));
                    offerResponse.setFinalPrice(response.getCurrentPrice().subtract(discountAmount));
                    return offerResponse;
                })
                .collect(Collectors.toList());
            response.setOffers(offerResponses);
        }
        
        return response;
    }
    
    /**
     * Generate flight number based on route and date
     */
    private String generateFlightNumber(Flight flight) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMM");
        String dateStr = flight.getDepTime().format(formatter);
        return "AC" + dateStr + String.format("%03d", flight.getId() % 1000);
    }
    
    /**
     * Calculate flight duration
     */
    private String calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        long minutes = ChronoUnit.MINUTES.between(departure, arrival);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        return String.format("%dh %02dm", hours, remainingMinutes);
    }
    
    /**
     * Generate seat numbers for aircraft
     */
    private List<String> generateSeatNumbers(int capacity) {
        List<String> seats = new ArrayList<>();
        int rows = (capacity / 6) + (capacity % 6 > 0 ? 1 : 0); // Assuming 6 seats per row
        char[] seatLetters = {'A', 'B', 'C', 'D', 'E', 'F'};
        
        for (int row = 1; row <= rows; row++) {
            for (char letter : seatLetters) {
                seats.add(row + String.valueOf(letter));
                if (seats.size() >= capacity) break;
            }
            if (seats.size() >= capacity) break;
        }
        
        return seats;
    }
    
    /**
     * Get all airports for dropdown search
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> getAllAirports() {
        String query = """
            SELECT a.iata_code, a.name, a.city, c.name as country_name
            FROM airports a 
            LEFT JOIN countries c ON a.country_code = c.code 
            ORDER BY a.name
        """;
        
        List<Object[]> results = entityManager.createNativeQuery(query).getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, String> airportData = new HashMap<>();
                airportData.put("code", (String) row[0]);
                airportData.put("name", (String) row[1]);
                airportData.put("city", (String) row[2]);
                airportData.put("country", (String) row[3]);
                airportData.put("label", row[2] + " (" + row[0] + ")");
                return airportData;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get all destinations from a specific origin airport
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> getDestinationsFromOrigin(String originCode) {
        String query = """
            SELECT DISTINCT dest.iata_code, dest.name, dest.city, c.name as country_name
            FROM flights f
            JOIN routes r ON f.route_id = r.id
            JOIN segments s ON r.id = s.route_id
            JOIN airports origin ON s.origin_airport_id = origin.id
            JOIN airports dest ON s.destination_airport_id = dest.id
            LEFT JOIN countries c ON dest.country_code = c.code
            WHERE origin.iata_code = :originCode
            AND f.status = 'SCHEDULED'
            ORDER BY dest.city
        """;
        
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("originCode", originCode.toUpperCase())
            .getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, String> airportData = new HashMap<>();
                airportData.put("code", (String) row[0]);
                airportData.put("name", (String) row[1]);
                airportData.put("city", (String) row[2]);
                airportData.put("country", (String) row[3]);
                airportData.put("label", row[2] + " (" + row[0] + ")");
                return airportData;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get all origins to a specific destination airport
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> getOriginsToDestination(String destinationCode) {
        String query = """
            SELECT DISTINCT origin.iata_code, origin.name, origin.city, c.name as country_name
            FROM flights f
            JOIN routes r ON f.route_id = r.id
            JOIN segments s ON r.id = s.route_id
            JOIN airports origin ON s.origin_airport_id = origin.id
            JOIN airports dest ON s.destination_airport_id = dest.id
            LEFT JOIN countries c ON origin.country_code = c.code
            WHERE dest.iata_code = :destinationCode
            AND f.status = 'SCHEDULED'
            ORDER BY origin.city
        """;
        
        List<Object[]> results = entityManager.createNativeQuery(query)
            .setParameter("destinationCode", destinationCode.toUpperCase())
            .getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, String> airportData = new HashMap<>();
                airportData.put("code", (String) row[0]);
                airportData.put("name", (String) row[1]);
                airportData.put("city", (String) row[2]);
                airportData.put("country", (String) row[3]);
                airportData.put("label", row[2] + " (" + row[0] + ")");
                return airportData;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Generate seat configuration map
     */
    private Map<String, Object> generateSeatConfiguration(int capacity) {
        Map<String, Object> config = new HashMap<>();
        int rows = (capacity / 6) + (capacity % 6 > 0 ? 1 : 0);
        
        config.put("rows", rows);
        config.put("seatsPerRow", 6);
        config.put("seatLetters", Arrays.asList("A", "B", "C", "D", "E", "F"));
        config.put("aisleAfter", Arrays.asList("C")); // Aisle after seat C
        
        return config;
    }
}
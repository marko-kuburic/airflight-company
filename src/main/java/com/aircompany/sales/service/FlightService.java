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

import jakarta.persistence.EntityManager;
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
     */
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest searchRequest) {
        logger.info("Searching flights from {} to {} on {}", 
            searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
        
        // Build dynamic query based on search criteria
        StringBuilder jpql = new StringBuilder(
            "SELECT f FROM Flight f " +
            "JOIN f.route r " +
            "JOIN r.segments s " +
            "JOIN s.originAirport origin " +
            "JOIN s.destinationAirport dest " +
            "WHERE DATE(f.depTime) = :departureDate " +
            "AND f.status = :status "
        );
        
        // Add origin/destination filters if provided
        if (searchRequest.getOrigin() != null && !searchRequest.getOrigin().trim().isEmpty()) {
            jpql.append("AND (origin.code = :origin OR origin.name LIKE :originName) ");
        }
        
        if (searchRequest.getDestination() != null && !searchRequest.getDestination().trim().isEmpty()) {
            jpql.append("AND (dest.code = :destination OR dest.name LIKE :destinationName) ");
        }
        
        jpql.append("ORDER BY f.depTime ASC");
        
        TypedQuery<Flight> query = entityManager.createQuery(jpql.toString(), Flight.class);
        query.setParameter("departureDate", searchRequest.getDepartureDate());
        query.setParameter("status", Flight.FlightStatus.SCHEDULED);
        
        if (searchRequest.getOrigin() != null && !searchRequest.getOrigin().trim().isEmpty()) {
            query.setParameter("origin", searchRequest.getOrigin().toUpperCase());
            query.setParameter("originName", "%" + searchRequest.getOrigin() + "%");
        }
        
        if (searchRequest.getDestination() != null && !searchRequest.getDestination().trim().isEmpty()) {
            query.setParameter("destination", searchRequest.getDestination().toUpperCase());
            query.setParameter("destinationName", "%" + searchRequest.getDestination() + "%");
        }
        
        List<Flight> flights = query.getResultList();
        
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
        response.setBasePrice(dynamicPricingService.getBasePrice(flight));
        response.setCurrentPrice(dynamicPricingService.getCurrentPrice(flight));
        
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
    public List<Map<String, String>> getAllAirports() {
        TypedQuery<Airport> query = entityManager.createQuery(
            "SELECT a FROM Airport a ORDER BY a.name", Airport.class);
        
        List<Airport> airports = query.getResultList();
        
        return airports.stream()
            .map(airport -> {
                Map<String, String> airportData = new HashMap<>();
                airportData.put("code", airport.getIataCode());
                airportData.put("name", airport.getName());
                airportData.put("city", airport.getCity());
                airportData.put("country", airport.getCountry().getName());
                airportData.put("label", airport.getCity() + " (" + airport.getIataCode() + ")");
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
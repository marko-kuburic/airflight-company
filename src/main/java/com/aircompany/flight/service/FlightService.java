package com.aircompany.flight.service;

import com.aircompany.flight.dto.FlightRequestDto;
import com.aircompany.flight.dto.FlightResponseDto;
import com.aircompany.flight.model.Aircraft;
import com.aircompany.flight.model.Flight;
import com.aircompany.flight.model.Flight.FlightStatus;
import com.aircompany.flight.model.Route;
import com.aircompany.flight.repository.AircraftRepository;
import com.aircompany.flight.repository.FlightRepository;
import com.aircompany.flight.repository.RouteRepository;
import com.aircompany.hr.model.FlightDispatcher;
import com.aircompany.hr.repository.FlightDispatcherRepository;
import com.aircompany.sales.dto.FlightSearchRequest;
import com.aircompany.sales.dto.FlightSearchResponse;
import com.aircompany.sales.model.Offer;
import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.repository.TicketRepository;
import com.aircompany.sales.service.OfferService;
import com.aircompany.sales.service.DynamicPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class FlightService {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private AircraftRepository aircraftRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private FlightDispatcherRepository flightDispatcherRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private OfferService offerService;
    
    @Autowired
    private DynamicPricingService dynamicPricingService;
    
    // ========== FLIGHT MANAGEMENT METHODS ==========
    
    public List<FlightResponseDto> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<FlightResponseDto> getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<FlightResponseDto> getFlightsByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightResponseDto> getFlightsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return flightRepository.findByDepTimeBetween(startTime, endTime).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightResponseDto> getFlightsByAircraft(Long aircraftId) {
        return flightRepository.findByAircraftId(aircraftId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightResponseDto> getFlightsByRoute(Long routeId) {
        return flightRepository.findByRouteId(routeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightResponseDto> getFlightsByDispatcher(Long flightDispatcherId) {
        return flightRepository.findByFlightDispatcherId(flightDispatcherId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public FlightResponseDto createFlight(FlightRequestDto requestDto) {
        Aircraft aircraft = null;
        if (requestDto.getAircraftId() != null) {
            aircraft = aircraftRepository.findById(requestDto.getAircraftId())
                    .orElseThrow(() -> new IllegalArgumentException("Aircraft with ID " + requestDto.getAircraftId() + " not found"));
        }
        
        Route route = null;
        if (requestDto.getRouteId() != null) {
            route = routeRepository.findById(requestDto.getRouteId())
                    .orElseThrow(() -> new IllegalArgumentException("Route with ID " + requestDto.getRouteId() + " not found"));
        }
        
        FlightDispatcher flightDispatcher = null;
        if (requestDto.getFlightDispatcherId() != null) {
            flightDispatcher = flightDispatcherRepository.findById(requestDto.getFlightDispatcherId())
                    .orElseThrow(() -> new IllegalArgumentException("FlightDispatcher with ID " + requestDto.getFlightDispatcherId() + " not found"));
        }
        
        Flight flight = new Flight(requestDto.getDepTime(), requestDto.getArrTime(), requestDto.getStatus());
        flight.setAircraft(aircraft);
        flight.setRoute(route);
        flight.setFlightDispatcher(flightDispatcher);
        
        Flight savedFlight = flightRepository.save(flight);
        return convertToResponseDto(savedFlight);
    }
    
    public Optional<FlightResponseDto> updateFlight(Long id, FlightRequestDto requestDto) {
        return flightRepository.findById(id)
                .map(flight -> {
                    flight.setDepTime(requestDto.getDepTime());
                    flight.setArrTime(requestDto.getArrTime());
                    flight.setStatus(requestDto.getStatus());
                    
                    if (requestDto.getAircraftId() != null) {
                        Aircraft aircraft = aircraftRepository.findById(requestDto.getAircraftId())
                                .orElseThrow(() -> new IllegalArgumentException("Aircraft with ID " + requestDto.getAircraftId() + " not found"));
                        flight.setAircraft(aircraft);
                    }
                    
                    if (requestDto.getRouteId() != null) {
                        Route route = routeRepository.findById(requestDto.getRouteId())
                                .orElseThrow(() -> new IllegalArgumentException("Route with ID " + requestDto.getRouteId() + " not found"));
                        flight.setRoute(route);
                    }
                    
                    if (requestDto.getFlightDispatcherId() != null) {
                        FlightDispatcher flightDispatcher = flightDispatcherRepository.findById(requestDto.getFlightDispatcherId())
                                .orElseThrow(() -> new IllegalArgumentException("FlightDispatcher with ID " + requestDto.getFlightDispatcherId() + " not found"));
                        flight.setFlightDispatcher(flightDispatcher);
                    }
                    
                    Flight savedFlight = flightRepository.save(flight);
                    return convertToResponseDto(savedFlight);
                });
    }
    
    public Optional<FlightResponseDto> updateFlightStatus(Long id, FlightStatus status) {
        return flightRepository.findById(id)
                .map(flight -> {
                    flight.setStatus(status);
                    Flight savedFlight = flightRepository.save(flight);
                    return convertToResponseDto(savedFlight);
                });
    }
    
    public boolean deleteFlight(Long id) {
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getFlightCountByStatus(FlightStatus status) {
        return flightRepository.countByStatus(status);
    }
    
    // ========== SALES METHODS ==========
    
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
            .map(this::convertToFlightSearchResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get flight entity by ID (for sales operations)
     */
    public Flight getFlightEntityById(Long id) {
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
        Flight flight = getFlightEntityById(flightId);
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
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private FlightResponseDto convertToResponseDto(Flight flight) {
        FlightResponseDto responseDto = new FlightResponseDto();
        responseDto.setId(flight.getId());
        responseDto.setDepTime(flight.getDepTime());
        responseDto.setArrTime(flight.getArrTime());
        responseDto.setStatus(flight.getStatus());
        responseDto.setCreatedAt(flight.getCreatedAt());
        responseDto.setModifiedAt(flight.getModifiedAt());
        
        if (flight.getAircraft() != null) {
            responseDto.setAircraftId(flight.getAircraft().getId());
            responseDto.setAircraftModel(flight.getAircraft().getModel());
        }
        
        if (flight.getRoute() != null) {
            responseDto.setRouteId(flight.getRoute().getId());
            responseDto.setRouteName(flight.getRoute().getName());
        }
        
        if (flight.getFlightDispatcher() != null) {
            responseDto.setFlightDispatcherId(flight.getFlightDispatcher().getId());
            responseDto.setFlightDispatcherName(
                    flight.getFlightDispatcher().getFirstName() + " " + 
                    flight.getFlightDispatcher().getLastName()
            );
        }
        
        responseDto.setScheduleCount(flight.getSchedules().size());
        
        return responseDto;
    }
    
    /**
     * Convert Flight entity to FlightSearchResponse DTO
     */
    private FlightSearchResponse convertToFlightSearchResponse(Flight flight) {
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
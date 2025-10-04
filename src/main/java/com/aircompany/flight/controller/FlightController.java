package com.aircompany.flight.controller;

import com.aircompany.flight.dto.FlightRequestDto;
import com.aircompany.flight.dto.FlightResponseDto;
import com.aircompany.flight.model.Flight;
import com.aircompany.flight.model.Flight.FlightStatus;
import com.aircompany.flight.service.FlightService;
import com.aircompany.sales.dto.FlightSearchRequest;
import com.aircompany.sales.dto.FlightSearchResponse;
import com.aircompany.sales.service.OfferService;
import com.aircompany.sales.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private OfferService offerService;
    
    // ========== FLIGHT MANAGEMENT ENDPOINTS ==========
    
    @GetMapping
    public ResponseEntity<List<FlightResponseDto>> getAllFlights() {
        List<FlightResponseDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/management/{id}")
    public ResponseEntity<FlightResponseDto> getFlightById(@PathVariable Long id) {
        Optional<FlightResponseDto> flight = flightService.getFlightById(id);
        return flight.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FlightResponseDto>> getFlightsByStatus(@PathVariable FlightStatus status) {
        List<FlightResponseDto> flights = flightService.getFlightsByStatus(status);
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<FlightResponseDto>> getFlightsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<FlightResponseDto> flights = flightService.getFlightsByDateRange(startTime, endTime);
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/aircraft/{aircraftId}")
    public ResponseEntity<List<FlightResponseDto>> getFlightsByAircraft(@PathVariable Long aircraftId) {
        List<FlightResponseDto> flights = flightService.getFlightsByAircraft(aircraftId);
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<FlightResponseDto>> getFlightsByRoute(@PathVariable Long routeId) {
        List<FlightResponseDto> flights = flightService.getFlightsByRoute(routeId);
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/dispatcher/{flightDispatcherId}")
    public ResponseEntity<List<FlightResponseDto>> getFlightsByDispatcher(@PathVariable Long flightDispatcherId) {
        List<FlightResponseDto> flights = flightService.getFlightsByDispatcher(flightDispatcherId);
        return ResponseEntity.ok(flights);
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getFlightCountByStatus(@PathVariable FlightStatus status) {
        Long count = flightService.getFlightCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<FlightResponseDto> createFlight(@Valid @RequestBody FlightRequestDto requestDto) {
        try {
            FlightResponseDto createdFlight = flightService.createFlight(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDto> updateFlight(@PathVariable Long id, 
                                                       @Valid @RequestBody FlightRequestDto requestDto) {
        try {
            Optional<FlightResponseDto> updatedFlight = flightService.updateFlight(id, requestDto);
            return updatedFlight.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<FlightResponseDto> updateFlightStatus(@PathVariable Long id, 
                                                             @RequestParam FlightStatus status) {
        Optional<FlightResponseDto> updatedFlight = flightService.updateFlightStatus(id, status);
        return updatedFlight.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        boolean deleted = flightService.deleteFlight(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // ========== SALES ENDPOINTS ==========
    
    /**
     * Search for available flights based on criteria
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@Valid @ModelAttribute FlightSearchRequest searchRequest) {
        try {
            logger.info("Searching flights from {} to {} on {}", 
                searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
            
            List<FlightSearchResponse> flights = flightService.searchFlights(searchRequest);
            
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error searching flights: " + e.getMessage()));
        }
    }
    
    /**
     * Get flight entity by ID (for sales operations)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightEntityById(@PathVariable Long id) {
        try {
            Flight flight = flightService.getFlightEntityById(id);
            return ResponseEntity.ok(flight);
        } catch (Exception e) {
            logger.error("Error getting flight: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Flight not found"));
        }
    }
    
    /**
     * Get offers for a specific flight
     */
    @GetMapping("/{flightId}/offers")
    public ResponseEntity<?> getOffersForFlight(@PathVariable Long flightId) {
        try {
            List<Offer> offers = offerService.getActiveOffersForFlight(flightId);
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            logger.error("Error getting offers for flight: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting offers: " + e.getMessage()));
        }
    }
    
    /**
     * Get occupied seats for a flight
     */
    @GetMapping("/{flightId}/occupied-seats")
    public ResponseEntity<?> getOccupiedSeats(@PathVariable Long flightId) {
        try {
            List<String> occupiedSeats = flightService.getOccupiedSeats(flightId);
            return ResponseEntity.ok(occupiedSeats);
        } catch (Exception e) {
            logger.error("Error getting occupied seats: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting occupied seats: " + e.getMessage()));
        }
    }
    
    /**
     * Check seat availability
     */
    @GetMapping("/{flightId}/seats/{seatNumber}/availability")
    public ResponseEntity<?> checkSeatAvailability(@PathVariable Long flightId, 
                                                  @PathVariable String seatNumber) {
        try {
            boolean isAvailable = flightService.isSeatAvailable(flightId, seatNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("seatNumber", seatNumber);
            response.put("flightId", flightId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking seat availability: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error checking seat availability: " + e.getMessage()));
        }
    }
    
    /**
     * Get aircraft configuration (seat map) for a flight
     */
    @GetMapping("/{flightId}/seat-map")
    public ResponseEntity<?> getSeatMap(@PathVariable Long flightId) {
        try {
            Map<String, Object> seatMap = flightService.getSeatMap(flightId);
            return ResponseEntity.ok(seatMap);
        } catch (Exception e) {
            logger.error("Error getting seat map: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting seat map: " + e.getMessage()));
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
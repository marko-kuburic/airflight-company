package com.aircompany.sales.controller;

import com.aircompany.flight.model.Flight;
import com.aircompany.sales.dto.FlightSearchRequest;
import com.aircompany.sales.dto.FlightSearchResponse;
import com.aircompany.sales.service.FlightService;
import com.aircompany.sales.service.OfferService;
import com.aircompany.sales.model.Offer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private OfferService offerService;
    
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
     * Get flight details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable Long id) {
        try {
            Flight flight = flightService.getFlightById(id);
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
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
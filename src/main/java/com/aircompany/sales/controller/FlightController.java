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
import java.util.stream.Collectors;

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
     * Supports "Anywhere" searches where origin and/or destination can be empty
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@Valid @ModelAttribute FlightSearchRequest searchRequest) {
        try {
            logger.info("Flight search request - Origin: '{}', Destination: '{}', Date: {}", 
                searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
            
            // Handle empty strings as null for "Anywhere" searches
            if (searchRequest.getOrigin() != null && searchRequest.getOrigin().trim().isEmpty()) {
                searchRequest.setOrigin(null);
            }
            if (searchRequest.getDestination() != null && searchRequest.getDestination().trim().isEmpty()) {
                searchRequest.setDestination(null);
            }
            
            List<FlightSearchResponse> flights = flightService.searchFlights(searchRequest);
            
            logger.info("Returning {} flight results", flights.size());
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage(), e);
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
    
    /**
     * Get all airports for dropdown search
     */
    @GetMapping("/airports")
    public ResponseEntity<?> getAirports() {
        try {
            List<Map<String, String>> airports = flightService.getAllAirports();
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            logger.error("Error getting airports: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting airports: " + e.getMessage()));
        }
    }
    
    /**
     * Get all destinations from a specific origin ("Anywhere from X")
     */
    @GetMapping("/destinations")
    public ResponseEntity<?> getDestinationsFromOrigin(@RequestParam String origin) {
        try {
            List<Map<String, String>> destinations = flightService.getDestinationsFromOrigin(origin);
            return ResponseEntity.ok(destinations);
        } catch (Exception e) {
            logger.error("Error getting destinations from origin {}: {}", origin, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting destinations: " + e.getMessage()));
        }
    }
    
    /**
     * Get all origins to a specific destination ("Anywhere to X")
     */
    @GetMapping("/origins")
    public ResponseEntity<?> getOriginsToDestination(@RequestParam String destination) {
        try {
            List<Map<String, String>> origins = flightService.getOriginsToDestination(destination);
            return ResponseEntity.ok(origins);
        } catch (Exception e) {
            logger.error("Error getting origins to destination {}: {}", destination, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting origins: " + e.getMessage()));
        }
    }
    
    /**
     * Debug endpoint - Get all flights (for testing purposes)
     */
    @GetMapping("/debug/all")
    public ResponseEntity<?> getAllFlights() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            
            // Convert to simple map to avoid lazy loading issues
            List<Map<String, Object>> simplifiedFlights = flights.stream()
                .map(flight -> {
                    Map<String, Object> flightMap = new HashMap<>();
                    flightMap.put("id", flight.getId());
                    flightMap.put("flightNumber", flight.getFlightNumber());
                    flightMap.put("departureTime", flight.getDepTime());
                    flightMap.put("arrivalTime", flight.getArrTime());
                    flightMap.put("status", flight.getStatus());
                    return flightMap;
                })
                .collect(Collectors.toList());
            
            logger.info("Debug: Found {} total flights in database", flights.size());
            return ResponseEntity.ok(Map.of(
                "totalFlights", flights.size(),
                "flights", simplifiedFlights
            ));
        } catch (Exception e) {
            logger.error("Error getting all flights: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error getting flights: " + e.getMessage()));
        }
    }
    
    /**
     * Refresh offer for flight when expired - regenerate with new pricing
     */
    @PostMapping("/{flightId}/refresh-offer")
    public ResponseEntity<?> refreshOfferForFlight(@PathVariable Long flightId) {
        try {
            logger.info("Refreshing offer for flight ID: {}", flightId);
            
            // Regenerate offer with new pricing
            Offer newOffer = offerService.regenerateOfferForFlight(flightId);
            
            // Return flight search response with new offer
            FlightSearchResponse flight = flightService.getFlightResponseById(flightId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Offer refreshed with new pricing");
            response.put("flight", flight);
            response.put("newOfferId", newOffer.getId());
            response.put("expiresAt", newOffer.getExpiresAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error refreshing offer for flight {}: {}", flightId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Error refreshing offer: " + e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
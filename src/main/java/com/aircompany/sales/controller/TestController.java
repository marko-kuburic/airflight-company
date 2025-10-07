package com.aircompany.sales.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    /**
     * Test endpoint to verify API is working
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "API is working correctly");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mock flight search for frontend testing
     */
    @GetMapping("/flights")
    public ResponseEntity<?> mockFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String departureDate) {
        
        List<Map<String, Object>> mockFlights = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> flight = new HashMap<>();
            flight.put("id", i);
            flight.put("flightNumber", "AC" + (100 + i));
            flight.put("originAirport", origin != null ? origin : "BEG");
            flight.put("destinationAirport", destination != null ? destination : "CDG");
            flight.put("departureTime", "08:" + (i * 10));
            flight.put("arrivalTime", "10:" + (i * 10 + 30));
            flight.put("price", 200 + (i * 50));
            flight.put("cabinClass", "Economy");
            flight.put("offerValidUntil", "24h");
            flight.put("availableSeats", 150 - (i * 10));
            
            mockFlights.add(flight);
        }
        
        return ResponseEntity.ok(mockFlights);
    }
    
    /**
     * Mock user reservations
     */
    @GetMapping("/reservations/{userId}")
    public ResponseEntity<?> mockReservations(@PathVariable Long userId) {
        List<Map<String, Object>> mockReservations = new ArrayList<>();
        
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("id", 1);
        reservation.put("reservationNumber", "RES001");
        reservation.put("flightNumber", "AC101");
        reservation.put("route", "Belgrade → Paris");
        reservation.put("date", "2025-10-15");
        reservation.put("time", "08:30");
        reservation.put("status", "Confirmed");
        reservation.put("passengerName", "Test User");
        
        mockReservations.add(reservation);
        
        return ResponseEntity.ok(mockReservations);
    }
}
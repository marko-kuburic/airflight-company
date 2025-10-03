package com.aircompany.sales.controller;

import com.aircompany.sales.dto.CreateReservationDto;
import com.aircompany.sales.dto.PaymentDto;
import com.aircompany.sales.model.Reservation;
import com.aircompany.sales.model.Payment;
import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.service.BookingService;
import com.aircompany.sales.repository.ReservationRepository;
import com.aircompany.sales.repository.TicketRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    /**
     * Create a new reservation with tickets
     */
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationDto createDto) {
        try {
            logger.info("Creating reservation for offer: {}", createDto.getOfferId());
            
            Reservation reservation = bookingService.createReservation(createDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error creating reservation: " + e.getMessage());
        }
    }
    
    /**
     * Process payment for reservation
     */
    @PostMapping("/payments")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentDto paymentDto) {
        try {
            logger.info("Processing payment for reservation: {}", paymentDto.getReservationId());
            
            Payment payment = bookingService.processPayment(paymentDto);
            
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error processing payment: " + e.getMessage());
        }
    }
    
    /**
     * Get reservation by ID
     */
    @GetMapping("/reservations/{id}")
    public ResponseEntity<?> getReservation(@PathVariable Long id) {
        try {
            Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
            
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            logger.error("Error getting reservation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get reservation by reservation number
     */
    @GetMapping("/reservations/number/{reservationNumber}")
    public ResponseEntity<?> getReservationByNumber(@PathVariable String reservationNumber) {
        try {
            Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
            
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            logger.error("Error getting reservation by number: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get reservations by customer ID
     */
    @GetMapping("/reservations/customer/{customerId}")
    public ResponseEntity<List<Reservation>> getReservationsByCustomer(@PathVariable Long customerId) {
        List<Reservation> reservations = reservationRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * Get tickets for reservation
     */
    @GetMapping("/reservations/{reservationId}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsForReservation(@PathVariable Long reservationId) {
        List<Ticket> tickets = ticketRepository.findByReservationId(reservationId);
        return ResponseEntity.ok(tickets);
    }
    
    /**
     * Get occupied seats for flight
     */
    @GetMapping("/flights/{flightId}/occupied-seats")
    public ResponseEntity<List<String>> getOccupiedSeats(@PathVariable Long flightId) {
        List<String> occupiedSeats = ticketRepository.getOccupiedSeatsForFlight(flightId);
        return ResponseEntity.ok(occupiedSeats);
    }
    
    /**
     * Check if seat is available
     */
    @GetMapping("/flights/{flightId}/seats/{seatNumber}/availability")
    public ResponseEntity<?> checkSeatAvailability(@PathVariable Long flightId, 
                                                  @PathVariable String seatNumber) {
        boolean isTaken = ticketRepository.isSeatTaken(flightId, seatNumber);
        return ResponseEntity.ok(Map.of("available", !isTaken, "seatNumber", seatNumber));
    }
    
    /**
     * Cancel reservation
     */
    @PostMapping("/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, 
                                              @RequestParam(required = false) String reason) {
        try {
            bookingService.cancelReservation(id, reason);
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error cancelling reservation: " + e.getMessage());
        }
    }
    
    /**
     * Update ticket status
     */
    @PatchMapping("/tickets/{ticketId}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long ticketId, 
                                               @RequestParam String status) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
            ticket.setStatus(Ticket.TicketStatus.valueOf(status.toUpperCase()));
            ticketRepository.save(ticket);
            
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            logger.error("Error updating ticket status: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error updating ticket status: " + e.getMessage());
        }
    }
    
    // Helper for Map.of (Java 8 compatibility)
    private static class Map {
        public static java.util.Map<String, Object> of(String key1, Object value1, String key2, Object value2) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            return map;
        }
        
        public static java.util.Map<String, Object> of(String key, Object value) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put(key, value);
            return map;
        }
    }
}
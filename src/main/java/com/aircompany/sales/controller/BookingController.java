package com.aircompany.sales.controller;

import com.aircompany.sales.dto.CreateReservationDto;
import com.aircompany.sales.dto.PaymentDto;
import com.aircompany.sales.dto.ReservationResponse;
import com.aircompany.sales.exception.SeatAlreadyTakenException;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationDto createDto) {
        try {
            logger.info("Creating reservation for offer: {}", createDto.getOfferId());
            
            Reservation reservation = bookingService.createReservation(createDto);
            
            // Return simple map to avoid lazy loading issues
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", reservation.getId());
            response.put("reservationNumber", reservation.getReservationNumber());
            response.put("status", reservation.getStatus().toString());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SeatAlreadyTakenException e) {
            logger.error("Seat already taken: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error creating reservation: " + e.getMessage());
        }
    }
    
    /**
     * Process payment for reservation
     */
    @PostMapping("/payments")
    @Transactional
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentDto paymentDto) {
        try {
            logger.info("Processing payment for reservation: {}", paymentDto.getReservationId());
            
            Payment payment = bookingService.processPayment(paymentDto);
            
            // Return simple map to avoid lazy loading issues
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", payment.getId());
            response.put("amount", payment.getAmount());
            response.put("status", payment.getStatus().toString());
            response.put("method", payment.getMethod().toString());
            response.put("transactionId", payment.getTransactionId());
            
            return ResponseEntity.ok(response);
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
    @Transactional(readOnly = true)
    public ResponseEntity<?> getReservationsByCustomer(@PathVariable Long customerId) {
        List<Reservation> reservations = reservationRepository.findByCustomerIdWithTickets(customerId);
        
        // Convert to simple DTOs to avoid lazy loading issues
        List<java.util.Map<String, Object>> response = new java.util.ArrayList<>();
        for (Reservation reservation : reservations) {
            java.util.Map<String, Object> resMap = new java.util.HashMap<>();
            resMap.put("id", reservation.getId());
            resMap.put("reservationNumber", reservation.getReservationNumber());
            resMap.put("status", reservation.getStatus().toString());
            resMap.put("createdAt", reservation.getCreatedAt());
            
            // Add offer and flight info
            if (reservation.getOffer() != null) {
                java.util.Map<String, Object> offerMap = new java.util.HashMap<>();
                offerMap.put("id", reservation.getOffer().getId());
                offerMap.put("basePrice", reservation.getOffer().getBasePrice());
                
                if (reservation.getOffer().getFlight() != null) {
                    java.util.Map<String, Object> flightMap = new java.util.HashMap<>();
                    flightMap.put("id", reservation.getOffer().getFlight().getId());
                    flightMap.put("flightNumber", reservation.getOffer().getFlight().getFlightNumber());
                    flightMap.put("departureTime", reservation.getOffer().getFlight().getDepTime());
                    flightMap.put("arrivalTime", reservation.getOffer().getFlight().getArrTime());
                    flightMap.put("status", reservation.getOffer().getFlight().getStatus().toString());
                    
                    // Add route info if available
                    if (reservation.getOffer().getFlight().getRoute() != null) {
                        java.util.Map<String, Object> routeMap = new java.util.HashMap<>();
                        routeMap.put("id", reservation.getOffer().getFlight().getRoute().getId());
                        routeMap.put("name", reservation.getOffer().getFlight().getRoute().getName());
                        routeMap.put("totalDistance", reservation.getOffer().getFlight().getRoute().getTotalDistance());
                        
                        flightMap.put("route", routeMap);
                    }
                    
                    offerMap.put("flight", flightMap);
                }
                
                resMap.put("offer", offerMap);
            }
            
            // Add tickets
            List<java.util.Map<String, Object>> ticketsList = new java.util.ArrayList<>();
            for (Ticket ticket : reservation.getTickets()) {
                java.util.Map<String, Object> ticketMap = new java.util.HashMap<>();
                ticketMap.put("id", ticket.getId());
                ticketMap.put("seatNumber", ticket.getSeatNumber());
                ticketMap.put("price", ticket.getPrice());
                ticketMap.put("status", ticket.getStatus().toString());
                
                // Add passenger info
                if (ticket.getPassenger() != null) {
                    java.util.Map<String, Object> passengerMap = new java.util.HashMap<>();
                    passengerMap.put("firstName", ticket.getPassenger().getFirstName());
                    passengerMap.put("lastName", ticket.getPassenger().getLastName());
                    ticketMap.put("passenger", passengerMap);
                }
                
                ticketsList.add(ticketMap);
            }
            resMap.put("tickets", ticketsList);
            
            // Add payment info if available
            if (reservation.getPayment() != null) {
                java.util.Map<String, Object> paymentMap = new java.util.HashMap<>();
                paymentMap.put("id", reservation.getPayment().getId());
                paymentMap.put("amount", reservation.getPayment().getAmount());
                paymentMap.put("status", reservation.getPayment().getStatus().toString());
                paymentMap.put("method", reservation.getPayment().getMethod().toString());
                resMap.put("payment", paymentMap);
            }
            
            response.add(resMap);
        }
        
        return ResponseEntity.ok(response);
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
        try {
            List<String> occupiedSeats = ticketRepository.getOccupiedSeatsForFlight(flightId);
            return ResponseEntity.ok(occupiedSeats);
        } catch (Exception e) {
            logger.error("Error getting occupied seats for flight {}: {}", flightId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * Check if seat is available
     */
    @GetMapping("/flights/{flightId}/seats/{seatNumber}/availability")
    public ResponseEntity<?> checkSeatAvailability(@PathVariable Long flightId, 
                                                  @PathVariable String seatNumber) {
        try {
            boolean isTaken = ticketRepository.isSeatTaken(flightId, seatNumber);
            return ResponseEntity.ok(java.util.Map.of("available", !isTaken, "seatNumber", seatNumber));
        } catch (Exception e) {
            logger.error("Error checking seat availability: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error checking seat availability: " + e.getMessage());
        }
    }
    
    /**
     * Cancel reservation
     */
    @PostMapping("/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, 
                                              @RequestParam(required = false) String reason) {
        try {
            bookingService.cancelReservation(id, reason);
            return ResponseEntity.ok(java.util.Map.of("message", "Reservation cancelled successfully"));
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
    
    /**
     * Get tickets by customer ID (alternative to reservations endpoint)
     */
    @GetMapping("/tickets/customer/{customerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getTicketsByCustomer(@PathVariable Long customerId) {
        logger.info("=== FETCHING TICKETS FOR CUSTOMER {} ===", customerId);
        List<Ticket> tickets = ticketRepository.findByReservation_Customer_Id(customerId);
        logger.info("Found {} tickets for customer {}", tickets.size(), customerId);

        // Convert to DTOs with joined reservation/flight data
        List<java.util.Map<String, Object>> response = new java.util.ArrayList<>();
        for (Ticket ticket : tickets) {
            logger.info("Processing ticket ID: {}, Seat: {}", ticket.getId(), ticket.getSeatNumber());
            
            java.util.Map<String, Object> ticketMap = new java.util.HashMap<>();
            ticketMap.put("id", ticket.getId());
            ticketMap.put("price", ticket.getPrice());
            ticketMap.put("status", ticket.getStatus().toString());
            ticketMap.put("seatNumber", ticket.getSeatNumber());
            ticketMap.put("createdAt", ticket.getCreatedAt());
            
            // Add cabin class from ticket (not from offer fares)
            if (ticket.getCabinClass() != null) {
                logger.info("Ticket {} has cabin class: ID={}, Name={}", 
                    ticket.getId(), 
                    ticket.getCabinClass().getId(), 
                    ticket.getCabinClass().getName());
                ticketMap.put("cabinClass", ticket.getCabinClass().getName());
            } else {
                logger.warn("Ticket {} has NULL cabin class! Using ECONOMY as fallback", ticket.getId());
                ticketMap.put("cabinClass", "ECONOMY"); // Default fallback
            }

            // Add passenger info
            if (ticket.getPassenger() != null) {
                java.util.Map<String, Object> passengerMap = new java.util.HashMap<>();
                passengerMap.put("firstName", ticket.getPassenger().getFirstName());
                passengerMap.put("lastName", ticket.getPassenger().getLastName());
                passengerMap.put("email", ticket.getPassenger().getEmail());
                ticketMap.put("passenger", passengerMap);
            }

            // Add reservation info
            if (ticket.getReservation() != null) {
                java.util.Map<String, Object> reservationMap = new java.util.HashMap<>();
                reservationMap.put("id", ticket.getReservation().getId());
                reservationMap.put("reservationNumber", ticket.getReservation().getReservationNumber());
                reservationMap.put("status", ticket.getReservation().getStatus().toString());
                reservationMap.put("createdAt", ticket.getReservation().getCreatedAt());

                // Add payment info
                if (ticket.getReservation().getPayment() != null) {
                    java.util.Map<String, Object> paymentMap = new java.util.HashMap<>();
                    paymentMap.put("amount", ticket.getReservation().getPayment().getAmount());
                    paymentMap.put("status", ticket.getReservation().getPayment().getStatus().toString());
                    reservationMap.put("payment", paymentMap);
                }

                // Add offer and flight info
                if (ticket.getReservation().getOffer() != null) {
                    java.util.Map<String, Object> offerMap = new java.util.HashMap<>();
                    offerMap.put("id", ticket.getReservation().getOffer().getId());

                    if (ticket.getReservation().getOffer().getFlight() != null) {
                        java.util.Map<String, Object> flightMap = new java.util.HashMap<>();
                        flightMap.put("id", ticket.getReservation().getOffer().getFlight().getId());
                        flightMap.put("flightNumber", ticket.getReservation().getOffer().getFlight().getFlightNumber());
                        flightMap.put("departureTime", ticket.getReservation().getOffer().getFlight().getDepTime());
                        flightMap.put("arrivalTime", ticket.getReservation().getOffer().getFlight().getArrTime());
                        flightMap.put("status", ticket.getReservation().getOffer().getFlight().getStatus().toString());

                        // Add route info
                        if (ticket.getReservation().getOffer().getFlight().getRoute() != null) {
                            java.util.Map<String, Object> routeMap = new java.util.HashMap<>();
                            routeMap.put("id", ticket.getReservation().getOffer().getFlight().getRoute().getId());
                            routeMap.put("name", ticket.getReservation().getOffer().getFlight().getRoute().getName());
                            routeMap.put("totalDistance", ticket.getReservation().getOffer().getFlight().getRoute().getTotalDistance());
                            flightMap.put("route", routeMap);
                        }

                        offerMap.put("flight", flightMap);
                    }

                    // Add fare info (for cabin class)
                    if (ticket.getReservation().getOffer().getFares() != null && !ticket.getReservation().getOffer().getFares().isEmpty()) {
                        // For simplicity, use the first fare's cabin class
                        java.util.Map<String, Object> fareMap = new java.util.HashMap<>();
                        fareMap.put("cabinClass", java.util.Map.of("name", ticket.getReservation().getOffer().getFares().get(0).getCabinClass().getName()));
                        offerMap.put("fare", fareMap);
                    }

                    reservationMap.put("offer", offerMap);
                }
                
                // Add payment info
                if (ticket.getReservation().getPayment() != null) {
                    java.util.Map<String, Object> paymentMap = new java.util.HashMap<>();
                    paymentMap.put("amount", ticket.getReservation().getPayment().getAmount());
                    paymentMap.put("status", ticket.getReservation().getPayment().getStatus().toString());
                    paymentMap.put("method", ticket.getReservation().getPayment().getMethod().toString());
                    reservationMap.put("payment", paymentMap);
                }

                ticketMap.put("reservation", reservationMap);
            }

            response.add(ticketMap);
        }

        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel a ticket (Business class only)
     * This will:
     * - Check if ticket is Business class
     * - Create a refund payment
     * - Free up the seat
     * - Delete ticket and reservation from database
     */
    @DeleteMapping("/tickets/{ticketId}/cancel")
    @Transactional
    public ResponseEntity<?> cancelTicket(@PathVariable Long ticketId) {
        try {
            logger.info("Attempting to cancel ticket: {}", ticketId);
            
            // Get the ticket
            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
            
            // Check if ticket has Business class cabin
            if (ticket.getCabinClass() == null || 
                !ticket.getCabinClass().getName().equalsIgnoreCase("BUSINESS")) {
                logger.warn("Ticket {} is not Business class, cancellation not allowed", ticketId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of(
                        "success", false,
                        "message", "Only Business class tickets can be cancelled"
                    ));
            }
            
            // Check if ticket is already cancelled or refunded
            if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED || 
                ticket.getStatus() == Ticket.TicketStatus.REFUNDED) {
                logger.warn("Ticket {} is already cancelled/refunded", ticketId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of(
                        "success", false,
                        "message", "Ticket is already cancelled"
                    ));
            }
            
            // Call service method to handle cancellation
            bookingService.cancelTicket(ticket);
            
            logger.info("Successfully cancelled ticket: {}", ticketId);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Ticket cancelled successfully. Your refund has been processed.",
                "refundAmount", ticket.getPrice()
            ));
            
        } catch (Exception e) {
            logger.error("Error cancelling ticket {}: {}", ticketId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                    "success", false,
                    "message", "Error cancelling ticket: " + e.getMessage()
                ));
        }
    }
}
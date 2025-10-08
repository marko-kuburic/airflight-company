package com.aircompany.sales.controller;

import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.repository.TicketRepository;
import com.aircompany.sales.service.NotificationService;
import com.aircompany.sales.service.LoyaltyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @GetMapping("/customer/{customerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCustomerTickets(@PathVariable Long customerId) {
        try {
            logger.info("Fetching tickets for customer: {}", customerId);
            List<Ticket> tickets = ticketRepository.findByReservation_Customer_Id(customerId);
            
            // Convert to simplified DTO structure
            List<Map<String, Object>> ticketDtos = tickets.stream()
                .map(ticket -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", ticket.getId());
                    dto.put("seatNumber", ticket.getSeatNumber());
                    dto.put("status", ticket.getStatus().name());
                    dto.put("price", ticket.getPrice()); // Now contains actual paid amount per ticket
                    dto.put("totalAmount", ticket.getPrice()); // Same as price for consistency
                    
                    // Reservation info
                    if (ticket.getReservation() != null) {
                        Map<String, Object> reservationInfo = new HashMap<>();
                        reservationInfo.put("id", ticket.getReservation().getId());
                        reservationInfo.put("reservationNumber", ticket.getReservation().getReservationNumber());
                        reservationInfo.put("status", ticket.getReservation().getStatus().name());
                        
                        // Payment info
                        if (ticket.getReservation().getPayment() != null) {
                            Map<String, Object> paymentInfo = new HashMap<>();
                            paymentInfo.put("amount", ticket.getReservation().getPayment().getAmount());
                            paymentInfo.put("method", ticket.getReservation().getPayment().getMethod().name());
                            reservationInfo.put("payment", paymentInfo);
                        }
                        
                        dto.put("reservation", reservationInfo);
                    }
                    
                    // Passenger info
                    if (ticket.getPassenger() != null) {
                        Map<String, Object> passengerInfo = new HashMap<>();
                        passengerInfo.put("firstName", ticket.getPassenger().getFirstName());
                        passengerInfo.put("lastName", ticket.getPassenger().getLastName());
                        passengerInfo.put("email", ticket.getPassenger().getEmail());
                        dto.put("passenger", passengerInfo);
                    }
                    
                    // Flight info
                    if (ticket.getReservation() != null && ticket.getReservation().getOffer() != null) {
                        var offer = ticket.getReservation().getOffer();
                        if (offer.getFlight() != null) {
                            Map<String, Object> flightInfo = new HashMap<>();
                            flightInfo.put("flightNumber", offer.getFlight().getFlightNumber());
                            flightInfo.put("departureTime", offer.getFlight().getDepTime());
                            flightInfo.put("arrivalTime", offer.getFlight().getArrTime());
                            
                            if (offer.getFlight().getRoute() != null) {
                                flightInfo.put("route", offer.getFlight().getRoute().getName());
                                flightInfo.put("distance", offer.getFlight().getRoute().getTotalDistance());
                            }
                            
                            dto.put("flight", flightInfo);
                        }
                    }
                    
                    // Use denormalized cabin class name stored directly on ticket
                    String cabinClassName = ticket.getCabinClassName();
                    if (cabinClassName != null && !cabinClassName.isEmpty()) {
                        dto.put("cabinClass", cabinClassName);
                    } else {
                        // Fallback to fare cabin class if needed
                        if (ticket.getReservation() != null && ticket.getReservation().getOffer() != null) {
                            var offer = ticket.getReservation().getOffer();
                            if (offer.getFares() != null && !offer.getFares().isEmpty()) {
                                dto.put("cabinClass", offer.getFares().get(0).getCabinClass().getName());
                            } else {
                                dto.put("cabinClass", "ECONOMY");
                            }
                        } else {
                            dto.put("cabinClass", "ECONOMY");
                        }
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ticketDtos);
        } catch (Exception e) {
            logger.error("Error fetching tickets for customer {}: {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch tickets: " + e.getMessage()));
        }
    }
    
    /**
     * Mark ticket as completed and reward loyalty points
     */
    @PostMapping("/{ticketId}/complete")
    @Transactional
    public ResponseEntity<?> completeTicket(@PathVariable Long ticketId) {
        try {
            logger.info("Completing ticket: {}", ticketId);
            
            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
            // Only process if ticket is CONFIRMED
            if (ticket.getStatus() != Ticket.TicketStatus.CONFIRMED) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ticket is not in CONFIRMED status"));
            }
            
            // Update ticket status to USED
            ticket.setStatus(Ticket.TicketStatus.USED);
            ticketRepository.save(ticket);
            
            // Calculate loyalty points based on distance (1 km = 1 point)
            Integer loyaltyPoints = 0;
            if (ticket.getReservation() != null && 
                ticket.getReservation().getOffer() != null &&
                ticket.getReservation().getOffer().getFlight() != null &&
                ticket.getReservation().getOffer().getFlight().getRoute() != null) {
                
                loyaltyPoints = ticket.getReservation().getOffer()
                    .getFlight().getRoute().getTotalDistance().intValue();
                
                // Award points to customer
                if (ticket.getReservation().getCustomer() != null) {
                    loyaltyService.addPoints(ticket.getReservation().getCustomer().getId(), loyaltyPoints);
                    
                    // Create permanent notification
                    String message = String.format(
                        "Flight %s completed! You traveled %,d km and earned %,d loyalty points. Safe travels! ✈️",
                        ticket.getReservation().getOffer().getFlight().getFlightNumber(),
                        loyaltyPoints,
                        loyaltyPoints
                    );
                    
                    notificationService.createNotification(
                        ticket.getReservation().getCustomer().getId(),
                        message,
                        com.aircompany.hr.model.Notification.NotificationType.GENERAL
                    );
                    
                    logger.info("Awarded {} points to customer {} for completed flight", 
                               loyaltyPoints, ticket.getReservation().getCustomer().getId());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("ticketId", ticketId);
            response.put("loyaltyPoints", loyaltyPoints);
            response.put("message", "Flight completed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error completing ticket {}: {}", ticketId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to complete ticket: " + e.getMessage()));
        }
    }
}

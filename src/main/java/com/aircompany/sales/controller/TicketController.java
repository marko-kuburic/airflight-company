package com.aircompany.sales.controller;

import com.aircompany.sales.model.Ticket;
import com.aircompany.sales.repository.TicketRepository;
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
                            }
                            
                            dto.put("flight", flightInfo);
                        }
                        
                        // Cabin class from fares
                        if (offer.getFares() != null && !offer.getFares().isEmpty()) {
                            dto.put("cabinClass", offer.getFares().get(0).getCabinClass().getName());
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
}

package com.aircompany.sales.service;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.dto.CreateReservationDto;
import com.aircompany.sales.dto.PaymentDto;
import com.aircompany.sales.model.*;
import com.aircompany.sales.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Complete booking process: create reservation with tickets
     */
    @Transactional
    public Reservation createReservation(CreateReservationDto createDto) {
        logger.info("Creating reservation for offer ID: {} and customer ID: {}", 
                   createDto.getOfferId(), createDto.getCustomerId());
        
        // 1. Validate offer
        Offer offer = offerRepository.findByIdWithFares(createDto.getOfferId())
            .orElseThrow(() -> new RuntimeException("Offer not found"));
        
        if (!offer.isValidAndActive()) {
            throw new RuntimeException("Offer is no longer valid or has expired");
        }
        
        // 2. Validate and retrieve customer
        Customer customer = customerRepository.findById(createDto.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + createDto.getCustomerId()));
        
        // 3. Check seat availability
        validateSeatAvailability(createDto, offer);
        
        // 4. Create reservation
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(generateReservationNumber());
        reservation.setOffer(offer);
        reservation.setCustomer(customer);
        reservation.setSpecialRequests(createDto.getSpecialRequests());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        
        reservation = reservationRepository.save(reservation);
        
        // 4. Create tickets with passengers
        List<Ticket> tickets = createTicketsWithPassengers(createDto.getTickets(), reservation, offer);
        reservation.setTickets(tickets);
        
        logger.info("Reservation created successfully: {}", reservation.getReservationNumber());
        return reservation;
    }
    
    /**
     * Process payment for reservation
     */
    public Payment processPayment(PaymentDto paymentDto) {
        logger.info("Processing payment for reservation ID: {}", paymentDto.getReservationId());
        
        Reservation reservation = reservationRepository.findById(paymentDto.getReservationId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // Calculate payment breakdown
        // Use the totalAmount from frontend if provided (includes seat premiums), otherwise calculate from tickets
        BigDecimal totalAmount = paymentDto.getTotalAmount() != null ? 
            paymentDto.getTotalAmount() : calculateTotalAmount(reservation);
        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(paymentDto.getLoyaltyPointsToUse());
        BigDecimal cashAmount = totalAmount.subtract(loyaltyDiscount);
        
        if (cashAmount.compareTo(BigDecimal.ZERO) < 0) {
            cashAmount = BigDecimal.ZERO;
        }
        
        // Create payment
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(totalAmount);
        payment.setCashAmount(cashAmount);
        payment.setLoyaltyPointsUsed(paymentDto.getLoyaltyPointsToUse());
        payment.setMethod(paymentDto.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        
        // Process payment based on method
        boolean paymentSuccess = processPaymentByMethod(payment, paymentDto);
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            
            // Update ticket statuses and prices to reflect actual paid amount
            int ticketCount = reservation.getTickets().size();
            BigDecimal pricePerTicket = totalAmount.divide(new BigDecimal(ticketCount), 2, java.math.RoundingMode.HALF_UP);
            
            reservation.getTickets().forEach(ticket -> {
                ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
                ticket.setPrice(pricePerTicket); // Update to actual paid price (including extras like seat selection)
            });
            
            // Deduct loyalty points if used
            if (paymentDto.getLoyaltyPointsToUse() > 0) {
                loyaltyService.deductPoints(reservation.getCustomer().getId(), 
                                          paymentDto.getLoyaltyPointsToUse());
            }
            
            // Award loyalty points for purchase
            awardLoyaltyPoints(reservation);
            
            // Create reservation confirmation notification
            notificationService.createNotification(
                reservation.getCustomer().getId(),
                String.format("Reservation %s confirmed! Your booking is complete.", reservation.getReservationNumber()),
                com.aircompany.hr.model.Notification.NotificationType.GENERAL
            );
            
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        
        payment = paymentRepository.save(payment);
        reservation.setPayment(payment);
        reservationRepository.save(reservation);
        
        logger.info("Payment processed with status: {}", payment.getStatus());
        return payment;
    }
    
    /**
     * Complete a flight - called when flight actually completes
     * This awards additional points and updates reservation status
     */
    public void completeFlightForReservation(Long reservationId) {
        logger.info("Completing flight for reservation ID: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Cannot complete flight for non-confirmed reservation");
        }
        
        // Update reservation status to completed
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        
        // Update all tickets to completed
        reservation.getTickets().forEach(ticket -> 
            ticket.setStatus(Ticket.TicketStatus.USED));
        
        // Award loyalty points for flight completion
        try {
            customerService.awardPointsForReservation(reservation.getCustomer().getId(), reservation.getId());
            logger.info("Awarded flight completion points for reservation: {}", reservation.getReservationNumber());
        } catch (Exception e) {
            logger.error("Failed to award flight completion points for reservation {}: {}", 
                reservation.getReservationNumber(), e.getMessage());
        }
        
        reservationRepository.save(reservation);
        logger.info("Flight completed for reservation: {}", reservation.getReservationNumber());
    }
    
    /**
     * Cancel reservation
     */
    public void cancelReservation(Long reservationId, String reason) {
        logger.info("Cancelling reservation ID: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }
        
        // Cancel reservation
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        
        // Cancel all tickets
        reservation.getTickets().forEach(ticket -> 
            ticket.setStatus(Ticket.TicketStatus.CANCELLED));
        
        // Process refund if payment exists
        if (reservation.getPayment() != null && 
            reservation.getPayment().getStatus() == Payment.PaymentStatus.COMPLETED) {
            processRefund(reservation.getPayment());
        }
        
        reservationRepository.save(reservation);
        logger.info("Reservation cancelled successfully");
    }
    
    // Helper methods
    
    private void validateSeatAvailability(CreateReservationDto createDto, Offer offer) {
        Long flightId = offer.getFlight().getId();
        
        for (CreateReservationDto.CreateTicketDto ticketDto : createDto.getTickets()) {
            if (ticketDto.getSeatNumber() != null) {
                boolean seatTaken = ticketRepository.isSeatTaken(flightId, ticketDto.getSeatNumber());
                if (seatTaken) {
                    throw new RuntimeException("Seat " + ticketDto.getSeatNumber() + " is already taken");
                }
            }
        }
    }
    
    private List<Ticket> createTicketsWithPassengers(List<CreateReservationDto.CreateTicketDto> ticketDtos, 
                                                     Reservation reservation, Offer offer) {
        List<Ticket> tickets = new ArrayList<>();
        BigDecimal ticketPrice = offer.getLowestFarePrice();
        
        for (CreateReservationDto.CreateTicketDto ticketDto : ticketDtos) {
            // Create or find passenger
            Passenger passenger = findOrCreatePassenger(ticketDto.getPassenger());
            
            // Create ticket
            Ticket ticket = new Ticket(ticketPrice, reservation, passenger);
            ticket.setSeatNumber(ticketDto.getSeatNumber());
            ticket.setStatus(Ticket.TicketStatus.CREATED);
            
            ticket = ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        
        return tickets;
    }
    
    private Passenger findOrCreatePassenger(com.aircompany.sales.dto.PassengerDto passengerDto) {
        // Try to find existing passenger by document number
        return passengerRepository.findByDocumentNumber(passengerDto.getDocumentNumber())
            .orElseGet(() -> {
                // Create new passenger
                Passenger passenger = new Passenger();
                passenger.setFirstName(passengerDto.getFirstName());
                passenger.setLastName(passengerDto.getLastName());
                passenger.setDateOfBirth(passengerDto.getDateOfBirth());
                passenger.setDocumentNumber(passengerDto.getDocumentNumber());
                passenger.setEmail(passengerDto.getEmail());
                passenger.setPhone(passengerDto.getPhone());
                passenger.setNationality(passengerDto.getNationality());
                return passengerRepository.save(passenger);
            });
    }
    
    private BigDecimal calculateTotalAmount(Reservation reservation) {
        return reservation.getTickets().stream()
            .map(Ticket::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateLoyaltyDiscount(Integer loyaltyPoints) {
        if (loyaltyPoints == null || loyaltyPoints <= 0) {
            return BigDecimal.ZERO;
        }
        // Assume 1 point = $0.01
        return new BigDecimal(loyaltyPoints).divide(new BigDecimal("100"));
    }
    
    private boolean processPaymentByMethod(Payment payment, PaymentDto paymentDto) {
        // Simulate payment processing
        switch (payment.getMethod()) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                return processCreditCardPayment(paymentDto);
            case BANK_TRANSFER:
                return processBankTransfer(paymentDto);
            case LOYALTY_POINTS:
                return payment.getCashAmount().compareTo(BigDecimal.ZERO) == 0;
            case COMBINED:
                return processCreditCardPayment(paymentDto);
            default:
                return true; // For other methods like CASH, VOUCHER
        }
    }
    
    private boolean processCreditCardPayment(PaymentDto paymentDto) {
        // Simulate credit card processing
        // In real implementation, integrate with payment gateway
        return paymentDto.getCardNumber() != null && !paymentDto.getCardNumber().isEmpty();
    }
    
    private boolean processBankTransfer(PaymentDto paymentDto) {
        // Simulate bank transfer processing
        return paymentDto.getBankAccount() != null && !paymentDto.getBankAccount().isEmpty();
    }
    
    private void processRefund(Payment payment) {
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        
        // Return loyalty points if they were used
        if (payment.getLoyaltyPointsUsed() > 0) {
            loyaltyService.addPoints(payment.getReservation().getCustomer().getId(), 
                                   payment.getLoyaltyPointsUsed());
        }
        
        paymentRepository.save(payment);
    }
    
    private void awardLoyaltyPoints(Reservation reservation) {
        // Use CustomerService's comprehensive point awarding system
        try {
            customerService.awardPointsForReservation(reservation.getCustomer().getId(), reservation.getId());
            logger.info("Awarded loyalty points for reservation: {}", reservation.getReservationNumber());
        } catch (Exception e) {
            logger.error("Failed to award loyalty points for reservation {}: {}", 
                reservation.getReservationNumber(), e.getMessage());
        }
    }
    
    private String generateReservationNumber() {
        return "RES-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
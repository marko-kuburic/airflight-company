package com.aircompany.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for creating a new reservation with tickets and passengers
 */
public class CreateReservationDto {
    
    @NotNull(message = "Offer ID is required")
    private Long offerId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private String specialRequests;
    
    @NotEmpty(message = "At least one ticket is required")
    @Valid
    private List<CreateTicketDto> tickets;
    
    // Constructors
    public CreateReservationDto() {}
    
    public CreateReservationDto(Long offerId, Long customerId, List<CreateTicketDto> tickets) {
        this.offerId = offerId;
        this.customerId = customerId;
        this.tickets = tickets;
    }
    
    // Getters and Setters
    public Long getOfferId() {
        return offerId;
    }
    
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public List<CreateTicketDto> getTickets() {
        return tickets;
    }
    
    public void setTickets(List<CreateTicketDto> tickets) {
        this.tickets = tickets;
    }
    
    /**
     * Nested DTO for ticket creation
     */
    public static class CreateTicketDto {
        
        @Valid
        private PassengerDto passenger;
        
        private String seatNumber;
        
        // Constructors
        public CreateTicketDto() {}
        
        public CreateTicketDto(PassengerDto passenger, String seatNumber) {
            this.passenger = passenger;
            this.seatNumber = seatNumber;
        }
        
        // Getters and Setters
        public PassengerDto getPassenger() {
            return passenger;
        }
        
        public void setPassenger(PassengerDto passenger) {
            this.passenger = passenger;
        }
        
        public String getSeatNumber() {
            return seatNumber;
        }
        
        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }
    }
}
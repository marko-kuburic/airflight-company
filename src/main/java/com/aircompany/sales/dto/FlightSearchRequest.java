package com.aircompany.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FlightSearchRequest {
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;
    
    private LocalDate returnDate; // null for one-way flights
    
    private Integer passengers = 1;
    
    private String cabinClass = "ECONOMY"; // ECONOMY, BUSINESS, FIRST
    
    // Constructors
    public FlightSearchRequest() {}
    
    public FlightSearchRequest(String origin, String destination, LocalDate departureDate) {
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
    }
    
    // Getters and Setters
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public Integer getPassengers() {
        return passengers;
    }
    
    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }
    
    public String getCabinClass() {
        return cabinClass;
    }
    
    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }
}
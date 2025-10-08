package com.aircompany.flight.dto;

import com.aircompany.flight.model.Flight.FlightStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class FlightRequestDto {
    
    @NotBlank(message = "Flight number is required")
    private String flightNumber;
    
    @NotNull(message = "Departure time is required")
    private LocalDateTime depTime;
    
    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrTime;
    
    @NotNull(message = "Flight status is required")
    private FlightStatus status;
    
    private Long aircraftId;
    private Long offerId;
    private Long routeId;
    private Long flightDispatcherId;
    
    // Constructors
    public FlightRequestDto() {}
    
    public FlightRequestDto(String flightNumber, LocalDateTime depTime, LocalDateTime arrTime, FlightStatus status) {
        this.flightNumber = flightNumber;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.status = status;
    }
    
    // Getters and Setters
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public LocalDateTime getDepTime() {
        return depTime;
    }
    
    public void setDepTime(LocalDateTime depTime) {
        this.depTime = depTime;
    }
    
    public LocalDateTime getArrTime() {
        return arrTime;
    }
    
    public void setArrTime(LocalDateTime arrTime) {
        this.arrTime = arrTime;
    }
    
    public FlightStatus getStatus() {
        return status;
    }
    
    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    
    public Long getAircraftId() {
        return aircraftId;
    }
    
    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }
    
    public Long getOfferId() {
        return offerId;
    }
    
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public Long getFlightDispatcherId() {
        return flightDispatcherId;
    }
    
    public void setFlightDispatcherId(Long flightDispatcherId) {
        this.flightDispatcherId = flightDispatcherId;
    }
}

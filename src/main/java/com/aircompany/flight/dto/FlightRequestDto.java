package com.aircompany.flight.dto;

import com.aircompany.flight.model.Flight.FlightStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FlightRequestDto {
    
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
    
    public FlightRequestDto(LocalDateTime depTime, LocalDateTime arrTime, FlightStatus status) {
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.status = status;
    }
    
    // Getters and Setters
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

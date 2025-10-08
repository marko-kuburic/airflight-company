package com.aircompany.flight.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

public class SegmentRequestDto {
    
    @NotNull(message = "Route ID is required")
    private Long routeId;
    
    @NotNull(message = "Origin airport ID is required")
    private Long originAirportId;
    
    @NotNull(message = "Destination airport ID is required")
    private Long destinationAirportId;
    
    // Distance will be calculated automatically from airport coordinates
    private BigDecimal distance;
    
    private LocalTime departureTime;
    
    private LocalTime arrivalTime;
    
    // Constructors
    public SegmentRequestDto() {}
    
    public SegmentRequestDto(Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance) {
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
    }
    
    public SegmentRequestDto(Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance, LocalTime departureTime, LocalTime arrivalTime) {
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
    
    // Getters and Setters
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public Long getOriginAirportId() {
        return originAirportId;
    }
    
    public void setOriginAirportId(Long originAirportId) {
        this.originAirportId = originAirportId;
    }
    
    public Long getDestinationAirportId() {
        return destinationAirportId;
    }
    
    public void setDestinationAirportId(Long destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }
    
    public BigDecimal getDistance() {
        return distance;
    }
    
    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }
    
    public LocalTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

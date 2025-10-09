package com.aircompany.flight.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SegmentRequestDto {
    
    @NotNull(message = "Route ID is required")
    private Long routeId;
    
    @NotNull(message = "Origin airport ID is required")
    private Long originAirportId;
    
    @NotNull(message = "Destination airport ID is required")
    private Long destinationAirportId;
    
    // Distance and duration will be calculated automatically from airport coordinates
    private BigDecimal distance;
    
    private Integer durationMinutes;
    
    private Integer layoverMinutes; // Time to wait at destination before next segment (in minutes)
    
    // Constructors
    public SegmentRequestDto() {}
    
    public SegmentRequestDto(Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance) {
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
    }
    
    public SegmentRequestDto(Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance, Integer durationMinutes) {
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
        this.durationMinutes = durationMinutes;
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
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public Integer getLayoverMinutes() {
        return layoverMinutes;
    }
    
    public void setLayoverMinutes(Integer layoverMinutes) {
        this.layoverMinutes = layoverMinutes;
    }
}

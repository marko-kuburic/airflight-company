package com.aircompany.flight.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SegmentResponseDto {
    
    private Long id;
    private Long routeId;
    private String routeName;
    private Long originAirportId;
    private String originAirportCode;
    private String originAirportName;
    private Long destinationAirportId;
    private String destinationAirportCode;
    private String destinationAirportName;
    private BigDecimal distance;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    // Constructors
    public SegmentResponseDto() {}
    
    public SegmentResponseDto(Long id, Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance) {
        this.id = id;
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
    }
    
    public SegmentResponseDto(Long id, Long routeId, Long originAirportId, Long destinationAirportId, BigDecimal distance, LocalTime departureTime, LocalTime arrivalTime) {
        this.id = id;
        this.routeId = routeId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.distance = distance;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public String getRouteName() {
        return routeName;
    }
    
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public Long getOriginAirportId() {
        return originAirportId;
    }
    
    public void setOriginAirportId(Long originAirportId) {
        this.originAirportId = originAirportId;
    }
    
    public String getOriginAirportCode() {
        return originAirportCode;
    }
    
    public void setOriginAirportCode(String originAirportCode) {
        this.originAirportCode = originAirportCode;
    }
    
    public String getOriginAirportName() {
        return originAirportName;
    }
    
    public void setOriginAirportName(String originAirportName) {
        this.originAirportName = originAirportName;
    }
    
    public Long getDestinationAirportId() {
        return destinationAirportId;
    }
    
    public void setDestinationAirportId(Long destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }
    
    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }
    
    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }
    
    public String getDestinationAirportName() {
        return destinationAirportName;
    }
    
    public void setDestinationAirportName(String destinationAirportName) {
        this.destinationAirportName = destinationAirportName;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}

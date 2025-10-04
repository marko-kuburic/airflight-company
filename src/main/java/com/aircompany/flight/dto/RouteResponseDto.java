package com.aircompany.flight.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RouteResponseDto {
    
    private Long id;
    private String name;
    private BigDecimal totalDistance;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer segmentCount;
    private Integer flightCount;
    
    // Constructors
    public RouteResponseDto() {}
    
    public RouteResponseDto(Long id, String name, BigDecimal totalDistance) {
        this.id = id;
        this.name = name;
        this.totalDistance = totalDistance;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(BigDecimal totalDistance) {
        this.totalDistance = totalDistance;
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
    
    public Integer getSegmentCount() {
        return segmentCount;
    }
    
    public void setSegmentCount(Integer segmentCount) {
        this.segmentCount = segmentCount;
    }
    
    public Integer getFlightCount() {
        return flightCount;
    }
    
    public void setFlightCount(Integer flightCount) {
        this.flightCount = flightCount;
    }
}

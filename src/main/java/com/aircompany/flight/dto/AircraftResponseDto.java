package com.aircompany.flight.dto;

import com.aircompany.flight.model.Aircraft.AircraftStatus;
import java.time.LocalDateTime;

public class AircraftResponseDto {
    
    private Long id;
    private String model;
    private AircraftStatus status;
    private Integer capacity;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean hasActiveService;
    private Integer flightCount;
    
    // Constructors
    public AircraftResponseDto() {}
    
    public AircraftResponseDto(Long id, String model, AircraftStatus status, Integer capacity) {
        this.id = id;
        this.model = model;
        this.status = status;
        this.capacity = capacity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public AircraftStatus getStatus() {
        return status;
    }
    
    public void setStatus(AircraftStatus status) {
        this.status = status;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
    
    public Boolean getHasActiveService() {
        return hasActiveService;
    }
    
    public void setHasActiveService(Boolean hasActiveService) {
        this.hasActiveService = hasActiveService;
    }
    
    public Integer getFlightCount() {
        return flightCount;
    }
    
    public void setFlightCount(Integer flightCount) {
        this.flightCount = flightCount;
    }
}

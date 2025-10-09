package com.aircompany.flight.dto;

import com.aircompany.flight.model.Aircraft.AircraftStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AircraftRequestDto {
    
    @NotBlank(message = "Aircraft registration is required")
    private String registration;
    
    @NotBlank(message = "Aircraft model is required")
    private String model;
    
    @NotNull(message = "Aircraft status is required")
    private AircraftStatus status;
    
    @NotNull(message = "Capacity is required")
    private Integer capacity;
    
    // Constructors
    public AircraftRequestDto() {}
    
    public AircraftRequestDto(String registration, String model, AircraftStatus status, Integer capacity) {
        this.registration = registration;
        this.model = model;
        this.status = status;
        this.capacity = capacity;
    }
    
    // Getters and Setters
    public String getRegistration() {
        return registration;
    }
    
    public void setRegistration(String registration) {
        this.registration = registration;
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
}

package com.aircompany.flight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RouteRequestDto {
    
    @NotBlank(message = "Route name is required")
    private String name;
    
    @NotNull(message = "Total distance is required")
    private BigDecimal totalDistance;
    
    // Constructors
    public RouteRequestDto() {}
    
    public RouteRequestDto(String name, BigDecimal totalDistance) {
        this.name = name;
        this.totalDistance = totalDistance;
    }
    
    // Getters and Setters
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
}

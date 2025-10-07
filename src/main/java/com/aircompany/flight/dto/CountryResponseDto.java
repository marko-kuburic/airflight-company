package com.aircompany.flight.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CountryResponseDto {
    
    private String code;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<String> airportCodes; // Simplified airport representation
    
    // Constructors
    public CountryResponseDto() {}
    
    public CountryResponseDto(String code, String name, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.code = code;
        this.name = name;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public List<String> getAirportCodes() {
        return airportCodes;
    }
    
    public void setAirportCodes(List<String> airportCodes) {
        this.airportCodes = airportCodes;
    }
}

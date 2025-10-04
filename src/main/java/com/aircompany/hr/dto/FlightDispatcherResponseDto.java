package com.aircompany.hr.dto;

import java.time.LocalDateTime;

public class FlightDispatcherResponseDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dispatchLicense;
    private Integer experienceYears;
    private String aircraftTypesQualified;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    // Related entity information
    private Long airportId;
    private String airportCode;
    private String airportName;
    private Integer flightCount;
    
    // Constructors
    public FlightDispatcherResponseDto() {}
    
    public FlightDispatcherResponseDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDispatchLicense() {
        return dispatchLicense;
    }
    
    public void setDispatchLicense(String dispatchLicense) {
        this.dispatchLicense = dispatchLicense;
    }
    
    public Integer getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public String getAircraftTypesQualified() {
        return aircraftTypesQualified;
    }
    
    public void setAircraftTypesQualified(String aircraftTypesQualified) {
        this.aircraftTypesQualified = aircraftTypesQualified;
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
    
    public Long getAirportId() {
        return airportId;
    }
    
    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }
    
    public String getAirportCode() {
        return airportCode;
    }
    
    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
    
    public String getAirportName() {
        return airportName;
    }
    
    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }
    
    public Integer getFlightCount() {
        return flightCount;
    }
    
    public void setFlightCount(Integer flightCount) {
        this.flightCount = flightCount;
    }
}

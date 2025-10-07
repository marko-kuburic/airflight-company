package com.aircompany.hr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlightDispatcherRequestDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private String dispatchLicense;
    private Integer experienceYears;
    private String aircraftTypesQualified;
    
    @NotNull(message = "Airport ID is required")
    private Long airportId;
    
    // Constructors
    public FlightDispatcherRequestDto() {}
    
    public FlightDispatcherRequestDto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Long getAirportId() {
        return airportId;
    }
    
    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }
}

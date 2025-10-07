package com.aircompany.hr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TechnicianRequestDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private Integer flightHours;
    private LocalDateTime lastDutyEnd;
    private String specialization;
    
    @NotNull(message = "Airport ID is required")
    private Long airportId;
    
    // Constructors
    public TechnicianRequestDto() {}
    
    public TechnicianRequestDto(String firstName, String lastName, String email, String password) {
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
    
    public Integer getFlightHours() {
        return flightHours;
    }
    
    public void setFlightHours(Integer flightHours) {
        this.flightHours = flightHours;
    }
    
    public LocalDateTime getLastDutyEnd() {
        return lastDutyEnd;
    }
    
    public void setLastDutyEnd(LocalDateTime lastDutyEnd) {
        this.lastDutyEnd = lastDutyEnd;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public Long getAirportId() {
        return airportId;
    }
    
    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }
}

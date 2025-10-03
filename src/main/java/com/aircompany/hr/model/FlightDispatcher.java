package com.aircompany.hr.model;

import jakarta.persistence.*;
import com.aircompany.flight.model.Flight;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("FLIGHT_DISPATCHER")
public class FlightDispatcher extends User {
    
    @Column(name = "dispatch_license")
    private String dispatchLicense;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(name = "aircraft_types_qualified")
    private String aircraftTypesQualified;
    
    @OneToMany(mappedBy = "flightDispatcher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();
    
    // Constructors
    public FlightDispatcher() {
        super();
    }
    
    public FlightDispatcher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Getters and Setters
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
    
    public List<Flight> getFlights() {
        return flights;
    }
    
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    
    @Override
    public String getUserType() {
        return "FLIGHT_DISPATCHER";
    }
}

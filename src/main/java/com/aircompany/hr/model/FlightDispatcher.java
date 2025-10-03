package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;
import com.aircompany.flight.model.Flight;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("FLIGHT_DISPATCHER")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FlightDispatcher extends User {
    
    @Column(name = "dispatch_license")
    private String dispatchLicense;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(name = "aircraft_types_qualified")
    private String aircraftTypesQualified;
    
    @OneToMany(mappedBy = "flightDispatcher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();
    
    // Custom constructor
    public FlightDispatcher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "FLIGHT_DISPATCHER";
    }
}

package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CREW_DISPATCHER")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CrewDispatcher extends User {
    
    @Column(name = "dispatch_license")
    private String dispatchLicense;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @OneToMany(mappedBy = "crewDispatcher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> managedSchedules = new ArrayList<>();
    
    public CrewDispatcher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "CREW_DISPATCHER";
    }
}

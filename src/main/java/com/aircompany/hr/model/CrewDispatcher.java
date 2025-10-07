package com.aircompany.hr.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CREW_DISPATCHER")
public class CrewDispatcher extends User {
    
    @Column(name = "dispatch_license")
    private String dispatchLicense;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @OneToMany(mappedBy = "crewDispatcher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> managedSchedules = new ArrayList<>();
    
    // Constructors
    public CrewDispatcher() {
        super();
    }
    
    public CrewDispatcher(String firstName, String lastName, String email, String password) {
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
    
    public List<Schedule> getManagedSchedules() {
        return managedSchedules;
    }
    
    public void setManagedSchedules(List<Schedule> managedSchedules) {
        this.managedSchedules = managedSchedules;
    }
    
    @Override
    public String getUserType() {
        return "CREW_DISPATCHER";
    }
}

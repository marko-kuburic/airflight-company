package com.aircompany.hr.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.flight.model.Maintenance;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TECHNICIAN")
@EntityListeners(AuditingEntityListener.class)
public class Technician extends User {
    
    @Column(name = "flight_hours")
    private Integer flightHours;
    
    @Column(name = "last_duty_end")
    private LocalDateTime lastDutyEnd;
    
    @Column(name = "specialization")
    private String specialization;
    
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Maintenance> services = new ArrayList<>();
    
    // Constructors
    public Technician() {
        super();
    }
    
    public Technician(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Getters and Setters
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
    
    public List<Maintenance> getServices() {
        return services;
    }
    
    public void setServices(List<Maintenance> services) {
        this.services = services;
    }
    
    @Override
    public String getUserType() {
        return "TECHNICIAN";
    }
}

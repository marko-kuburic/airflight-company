package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.flight.model.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TECHNICIAN")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Technician extends User {
    
    @Column(name = "flight_hours")
    private Integer flightHours;
    
    @Column(name = "last_duty_end")
    private LocalDateTime lastDutyEnd;
    
    @Column(name = "specialization")
    private String specialization;
    
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Service> services = new ArrayList<>();
    
    // Custom constructor
    public Technician(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "TECHNICIAN";
    }
}

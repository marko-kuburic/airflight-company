package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Employee extends User {
    
    @Column(name = "function")
    private String function;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "crew_position")
    private CrewPosition crewPosition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    
    @OneToMany(mappedBy = "pilot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AircraftTypeRating> aircraftTypeRatings = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingRecord> trainingRecords = new ArrayList<>();
    
    @Column(name = "flight_hours_current_month")
    private Integer flightHoursCurrentMonth = 0;
    
    @Column(name = "duty_hours_current_month")
    private Integer dutyHoursCurrentMonth = 0;
    
    @Column(name = "flight_hours_total")
    private Integer flightHoursTotal = 0;
    
    @Column(name = "last_duty_end")
    private LocalDateTime lastDutyEnd;
    
    @Column(name = "last_rest_start")
    private LocalDateTime lastRestStart;
    
    @Column(name = "consecutive_duty_days")
    private Integer consecutiveDutyDays = 0;
    
    public Employee(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "EMPLOYEE";
    }
}

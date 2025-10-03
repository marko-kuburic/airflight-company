package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.flight.model.Flight;
import com.aircompany.hr.model.Employee;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@EntityListeners(AuditingEntityListener.class)
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "duty_start", nullable = false)
    private LocalDateTime dutyStart;
    
    @NotNull
    @Column(name = "duty_end", nullable = false)
    private LocalDateTime dutyEnd;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ScheduleRole role;
    
    @Column(name = "is_standby")
    private Boolean isStandby = false;
    
    @Column(name = "replacement_reason")
    private String replacementReason;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private Flight flight;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_dispatcher_id")
    private CrewDispatcher crewDispatcher;
    
    public Schedule(LocalDateTime dutyStart, LocalDateTime dutyEnd, ScheduleRole role) {
        this.dutyStart = dutyStart;
        this.dutyEnd = dutyEnd;
        this.role = role;
    }
    
    // Default constructor
    public Schedule() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDutyStart() {
        return dutyStart;
    }
    
    public void setDutyStart(LocalDateTime dutyStart) {
        this.dutyStart = dutyStart;
    }
    
    public LocalDateTime getDutyEnd() {
        return dutyEnd;
    }
    
    public void setDutyEnd(LocalDateTime dutyEnd) {
        this.dutyEnd = dutyEnd;
    }
    
    public ScheduleRole getRole() {
        return role;
    }
    
    public void setRole(ScheduleRole role) {
        this.role = role;
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
    
    public Flight getFlight() {
        return flight;
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    
    public CrewDispatcher getCrewDispatcher() {
        return crewDispatcher;
    }
    
    public void setCrewDispatcher(CrewDispatcher crewDispatcher) {
        this.crewDispatcher = crewDispatcher;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public enum ScheduleRole {
        PILOT,
        CABIN_CREW
    }
}

package com.aircompany.hr.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {
    
    @Column(name = "`function`")
    private String function;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "crew_position")
    private CrewPosition crewPosition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();
    
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
    
    // Constructors
    public Employee() {
        super();
    }
    
    public Employee(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Getters and Setters
    public String getFunction() {
        return function;
    }
    
    public void setFunction(String function) {
        this.function = function;
    }
    
    public CrewPosition getCrewPosition() {
        return crewPosition;
    }
    
    public void setCrewPosition(CrewPosition crewPosition) {
        this.crewPosition = crewPosition;
    }
    
    public Qualification getQualification() {
        return qualification;
    }
    
    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }
    
    public List<Schedule> getSchedules() {
        return schedules;
    }
    
    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
    
    public List<AircraftTypeRating> getAircraftTypeRatings() {
        return aircraftTypeRatings;
    }
    
    public void setAircraftTypeRatings(List<AircraftTypeRating> aircraftTypeRatings) {
        this.aircraftTypeRatings = aircraftTypeRatings;
    }
    
    public List<TrainingRecord> getTrainingRecords() {
        return trainingRecords;
    }
    
    public void setTrainingRecords(List<TrainingRecord> trainingRecords) {
        this.trainingRecords = trainingRecords;
    }
    
    public Integer getFlightHoursCurrentMonth() {
        return flightHoursCurrentMonth;
    }
    
    public void setFlightHoursCurrentMonth(Integer flightHoursCurrentMonth) {
        this.flightHoursCurrentMonth = flightHoursCurrentMonth;
    }
    
    public Integer getDutyHoursCurrentMonth() {
        return dutyHoursCurrentMonth;
    }
    
    public void setDutyHoursCurrentMonth(Integer dutyHoursCurrentMonth) {
        this.dutyHoursCurrentMonth = dutyHoursCurrentMonth;
    }
    
    public Integer getFlightHoursTotal() {
        return flightHoursTotal;
    }
    
    public void setFlightHoursTotal(Integer flightHoursTotal) {
        this.flightHoursTotal = flightHoursTotal;
    }
    
    public LocalDateTime getLastDutyEnd() {
        return lastDutyEnd;
    }
    
    public void setLastDutyEnd(LocalDateTime lastDutyEnd) {
        this.lastDutyEnd = lastDutyEnd;
    }
    
    public LocalDateTime getLastRestStart() {
        return lastRestStart;
    }
    
    public void setLastRestStart(LocalDateTime lastRestStart) {
        this.lastRestStart = lastRestStart;
    }
    
    public Integer getConsecutiveDutyDays() {
        return consecutiveDutyDays;
    }
    
    public void setConsecutiveDutyDays(Integer consecutiveDutyDays) {
        this.consecutiveDutyDays = consecutiveDutyDays;
    }
    
    @Override
    public String getUserType() {
        return "EMPLOYEE";
    }
}

package com.aircompany.flight.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// import com.aircompany.hr.model.Schedule;
// import com.aircompany.hr.model.FlightDispatcher;
import com.aircompany.sales.model.Offer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flights")
@EntityListeners(AuditingEntityListener.class)
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;
    
    @NotNull
    @Column(name = "dep_time", nullable = false)
    private LocalDateTime depTime;
    
    @NotNull
    @Column(name = "arr_time", nullable = false)
    private LocalDateTime arrTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FlightStatus status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Offer> offers = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;
    
    // @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Schedule> schedules = new ArrayList<>();
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "flight_dispatcher_id")
    // private FlightDispatcher flightDispatcher;
    
    
    // Constructors
    public Flight() {}
    
    public Flight(LocalDateTime depTime, LocalDateTime arrTime, FlightStatus status) {
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public LocalDateTime getDepTime() {
        return depTime;
    }
    
    public void setDepTime(LocalDateTime depTime) {
        this.depTime = depTime;
    }
    
    public LocalDateTime getArrTime() {
        return arrTime;
    }
    
    public void setArrTime(LocalDateTime arrTime) {
        this.arrTime = arrTime;
    }
    
    public FlightStatus getStatus() {
        return status;
    }
    
    public void setStatus(FlightStatus status) {
        this.status = status;
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
    
    public Aircraft getAircraft() {
        return aircraft;
    }
    
    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }
    
    public List<Offer> getOffers() {
        return offers;
    }
    
    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
    // public List<Schedule> getSchedules() {
    //     return schedules;
    // }
    
    // public void setSchedules(List<Schedule> schedules) {
    //     this.schedules = schedules;
    // }
    
    // public FlightDispatcher getFlightDispatcher() {
    //     return flightDispatcher;
    // }
    
    // public void setFlightDispatcher(FlightDispatcher flightDispatcher) {
    //     this.flightDispatcher = flightDispatcher;
    // }
    
    
    // Nested Enum
    public enum FlightStatus {
        SCHEDULED,
        BOARDING,
        DEPARTED,
        IN_FLIGHT,
        LANDED,
        CANCELLED,
        DELAYED
    }
}

package com.aircompany.flight.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aircraft")
@EntityListeners(AuditingEntityListener.class)
public class Aircraft {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "registration", nullable = false, unique = true)
    private String registration;
    
    @NotBlank
    @Column(name = "model", nullable = false)
    private String model;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AircraftStatus status;
    
    @NotNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Maintenance> services = new ArrayList<>();
    
    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();
    
    // Constructors
    public Aircraft() {}
    
    public Aircraft(String registration, String model, AircraftStatus status, Integer capacity) {
        this.registration = registration;
        this.model = model;
        this.status = status;
        this.capacity = capacity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRegistration() {
        return registration;
    }
    
    public void setRegistration(String registration) {
        this.registration = registration;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public AircraftStatus getStatus() {
        return status;
    }
    
    public void setStatus(AircraftStatus status) {
        this.status = status;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
    
    public List<Maintenance> getServices() {
        return services;
    }
    
    public void setServices(List<Maintenance> services) {
        this.services = services;
    }
    
    public List<Flight> getFlights() {
        return flights;
    }
    
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    
    // Nested Enum
    public enum AircraftStatus {
        AVAILABLE,
        ACTIVE,
        MAINTENANCE,
        RETIRED
    }
}

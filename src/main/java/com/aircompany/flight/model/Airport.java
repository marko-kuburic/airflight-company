package com.aircompany.flight.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.hr.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airports")
@EntityListeners(AuditingEntityListener.class)
public class Airport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 3)
    @Column(name = "iata_code", length = 3, nullable = false, unique = true)
    private String iataCode; // IATA 3-letter airport code (e.g., JFK, LAX)
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank
    @Column(name = "city", nullable = false)
    private String city;
    
    @NotNull
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @NotNull
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @NotBlank
    @Column(name = "timezone", nullable = false)
    private String timezone; // e.g., "America/New_York", "Europe/London"
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;
    
    @OneToMany(mappedBy = "airport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> employees = new ArrayList<>();
    
    @OneToMany(mappedBy = "originAirport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Segment> originSegments = new ArrayList<>();
    
    @OneToMany(mappedBy = "destinationAirport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Segment> destinationSegments = new ArrayList<>();
    
    // Constructors
    public Airport() {}
    
    public Airport(String iataCode, String name, String city, BigDecimal latitude, BigDecimal longitude, String timezone, Country country) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.country = country;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIataCode() {
        return iataCode;
    }
    
    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public List<User> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }
    
    public List<Segment> getOriginSegments() {
        return originSegments;
    }
    
    public void setOriginSegments(List<Segment> originSegments) {
        this.originSegments = originSegments;
    }
    
    public List<Segment> getDestinationSegments() {
        return destinationSegments;
    }
    
    public void setDestinationSegments(List<Segment> destinationSegments) {
        this.destinationSegments = destinationSegments;
    }
}

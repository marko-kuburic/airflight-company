package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft_type_ratings")
@EntityListeners(AuditingEntityListener.class)
public class AircraftTypeRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilot_id", nullable = false)
    private Employee pilot;
    
    @NotBlank
    @Column(name = "aircraft_type", nullable = false)
    private String aircraftType; // Boeing 737, Airbus A320, etc.
    
    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    @Column(name = "certification_body")
    private String certificationBody; // CAA, EASA, FAA, etc.
    
    @Column(name = "certificate_number")
    private String certificateNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RatingStatus status = RatingStatus.ACTIVE;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    // Custom constructor
    public AircraftTypeRating(Employee pilot, String aircraftType, LocalDate issueDate, 
                             LocalDate expiryDate, String certificationBody) {
        this.pilot = pilot;
        this.aircraftType = aircraftType;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.certificationBody = certificationBody;
    }
    
    // Default constructor
    public AircraftTypeRating() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Employee getPilot() {
        return pilot;
    }
    
    public void setPilot(Employee pilot) {
        this.pilot = pilot;
    }
    
    public String getAircraftType() {
        return aircraftType;
    }
    
    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getCertificationBody() {
        return certificationBody;
    }
    
    public void setCertificationBody(String certificationBody) {
        this.certificationBody = certificationBody;
    }
    
    public String getCertificateNumber() {
        return certificateNumber;
    }
    
    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }
    
    public RatingStatus getStatus() {
        return status;
    }
    
    public void setStatus(RatingStatus status) {
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
    
    // Business logic methods
    public boolean isValid() {
        return status == RatingStatus.ACTIVE && expiryDate.isAfter(LocalDate.now());
    }
    
    public boolean isExpiringSoon(int daysThreshold) {
        return expiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }
    
    // Nested Enum
    public enum RatingStatus {
        ACTIVE("Active"),
        SUSPENDED("Suspended"),
        EXPIRED("Expired"),
        REVOKED("Revoked");
        
        private final String displayName;
        
        RatingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
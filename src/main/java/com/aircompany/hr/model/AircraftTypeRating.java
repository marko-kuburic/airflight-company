package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft_type_ratings")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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
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
@Table(name = "training_records")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TrainingRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "training_type", nullable = false)
    private TrainingType trainingType;
    
    @NotBlank
    @Column(name = "training_name", nullable = false)
    private String trainingName;
    
    @Column(name = "description")
    private String description;
    
    @NotNull
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;
    
    @Column(name = "next_due_date")
    private LocalDate nextDueDate;
    
    @Column(name = "certification_body")
    private String certificationBody;
    
    @Column(name = "certificate_number")
    private String certificateNumber;
    
    @Column(name = "instructor_name")
    private String instructorName;
    
    @Column(name = "training_hours")
    private Integer trainingHours;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TrainingStatus status = TrainingStatus.COMPLETED;
    
    @Column(name = "score")
    private Double score;
    
    @Column(name = "pass_threshold")
    private Double passThreshold;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    // Custom constructor
    public TrainingRecord(Employee employee, TrainingType trainingType, String trainingName, 
                         LocalDate completionDate, String certificationBody) {
        this.employee = employee;
        this.trainingType = trainingType;
        this.trainingName = trainingName;
        this.completionDate = completionDate;
        this.certificationBody = certificationBody;
    }
    
    // Business logic methods
    public boolean isExpired() {
        return nextDueDate != null && nextDueDate.isBefore(LocalDate.now());
    }
    
    public boolean isDueSoon(int daysThreshold) {
        return nextDueDate != null && 
               nextDueDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }
    
    public boolean isPassed() {
        return score != null && passThreshold != null && score >= passThreshold;
    }
    
    // Nested Enums
    public enum TrainingType {
        INITIAL_TRAINING("Initial Training"),
        RECURRENT_TRAINING("Recurrent Training"),
        PROFICIENCY_CHECK("Proficiency Check"),
        LINE_CHECK("Line Check"),
        SIMULATOR_TRAINING("Simulator Training"),
        GROUND_SCHOOL("Ground School"),
        SAFETY_TRAINING("Safety Training"),
        SECURITY_TRAINING("Security Training"),
        EMERGENCY_PROCEDURES("Emergency Procedures"),
        AIRCRAFT_SPECIFIC("Aircraft Specific Training"),
        REGULATORY_COMPLIANCE("Regulatory Compliance"),
        LANGUAGE_PROFICIENCY("Language Proficiency"),
        MEDICAL_TRAINING("Medical Training"),
        OTHER("Other");
        
        private final String displayName;
        
        TrainingType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TrainingStatus {
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        PASSED("Passed"),
        FAILED("Failed"),
        CANCELLED("Cancelled"),
        OVERDUE("Overdue");
        
        private final String displayName;
        
        TrainingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
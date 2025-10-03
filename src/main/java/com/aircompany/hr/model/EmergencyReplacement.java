package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_replacements")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class EmergencyReplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_employee_id", nullable = false)
    private Employee originalEmployee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replacement_employee_id", nullable = false)
    private Employee replacementEmployee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    
    @NotNull
    @Column(name = "replacement_time", nullable = false)
    private LocalDateTime replacementTime;
    
    @Column(name = "reason")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReplacementStatus status = ReplacementStatus.ACTIVE;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public EmergencyReplacement(Employee originalEmployee, Employee replacementEmployee, 
                               Schedule schedule, String reason) {
        this.originalEmployee = originalEmployee;
        this.replacementEmployee = replacementEmployee;
        this.schedule = schedule;
        this.reason = reason;
        this.replacementTime = LocalDateTime.now();
    }
    
    public enum ReplacementStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}
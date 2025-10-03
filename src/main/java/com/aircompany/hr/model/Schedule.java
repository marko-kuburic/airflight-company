package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.flight.model.Flight;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
    
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
    
    public enum ScheduleRole {
        PILOT,
        CABIN_CREW
    }
}

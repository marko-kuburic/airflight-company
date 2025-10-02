package com.aircompany.hr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "resp_status")
    private ResponseStatus respStatus;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Constructors
    public Notification() {}
    
    public Notification(String message, NotificationType type, User user) {
        this.message = message;
        this.type = type;
        this.user = user;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.respStatus = ResponseStatus.PENDING;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public ResponseStatus getRespStatus() {
        return respStatus;
    }
    
    public void setRespStatus(ResponseStatus respStatus) {
        this.respStatus = respStatus;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Nested Enums
    public enum NotificationType {
        FLIGHT_UPDATE,
        SCHEDULE_CHANGE,
        MAINTENANCE_ALERT,
        SECURITY_NOTICE,
        SYSTEM_ANNOUNCEMENT,
        GENERAL
    }
    
    public enum ResponseStatus {
        PENDING,
        ACKNOWLEDGED,
        REJECTED,
        COMPLETED
    }
}

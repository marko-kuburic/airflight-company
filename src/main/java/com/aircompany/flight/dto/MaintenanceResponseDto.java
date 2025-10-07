package com.aircompany.flight.dto;

import com.aircompany.flight.model.Maintenance.ServiceType;
import com.aircompany.flight.model.Maintenance.ServiceStatus;
import java.time.LocalDateTime;

public class MaintenanceResponseDto {
    
    private Long id;
    private ServiceType serviceType;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ServiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    // Related entity information
    private Long aircraftId;
    private String aircraftModel;
    private Long technicianId;
    private String technicianName;
    
    // Constructors
    public MaintenanceResponseDto() {}
    
    public MaintenanceResponseDto(Long id, ServiceType serviceType, String description, ServiceStatus status) {
        this.id = id;
        this.serviceType = serviceType;
        this.description = description;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ServiceType getServiceType() {
        return serviceType;
    }
    
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public ServiceStatus getStatus() {
        return status;
    }
    
    public void setStatus(ServiceStatus status) {
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
    
    public Long getAircraftId() {
        return aircraftId;
    }
    
    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }
    
    public String getAircraftModel() {
        return aircraftModel;
    }
    
    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }
    
    public Long getTechnicianId() {
        return technicianId;
    }
    
    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
    
    public String getTechnicianName() {
        return technicianName;
    }
    
    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }
}

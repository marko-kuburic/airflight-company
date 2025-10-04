package com.aircompany.flight.dto;

import com.aircompany.flight.model.Maintenance.ServiceType;
import com.aircompany.flight.model.Maintenance.ServiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class MaintenanceRequestDto {
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private ServiceStatus status;
    
    @NotNull(message = "Aircraft ID is required")
    private Long aircraftId;
    
    private Long technicianId;
    
    // Constructors
    public MaintenanceRequestDto() {}
    
    public MaintenanceRequestDto(ServiceType serviceType, String description, Long aircraftId) {
        this.serviceType = serviceType;
        this.description = description;
        this.aircraftId = aircraftId;
        this.status = ServiceStatus.SCHEDULED;
    }
    
    // Getters and Setters
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
    
    public Long getAircraftId() {
        return aircraftId;
    }
    
    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }
    
    public Long getTechnicianId() {
        return technicianId;
    }
    
    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}

package com.aircompany.flight.dto;

import com.aircompany.flight.model.Flight.FlightStatus;
import java.time.LocalDateTime;

public class FlightResponseDto {
    
    private Long id;
    private LocalDateTime depTime;
    private LocalDateTime arrTime;
    private FlightStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    // Related entity IDs and names for simplified response
    private Long aircraftId;
    private String aircraftModel;
    private Long offerId;
    private Long routeId;
    private String routeName;
    private Long flightDispatcherId;
    private String flightDispatcherName;
    private Integer scheduleCount;
    
    // Constructors
    public FlightResponseDto() {}
    
    public FlightResponseDto(Long id, LocalDateTime depTime, LocalDateTime arrTime, FlightStatus status) {
        this.id = id;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDepTime() {
        return depTime;
    }
    
    public void setDepTime(LocalDateTime depTime) {
        this.depTime = depTime;
    }
    
    public LocalDateTime getArrTime() {
        return arrTime;
    }
    
    public void setArrTime(LocalDateTime arrTime) {
        this.arrTime = arrTime;
    }
    
    public FlightStatus getStatus() {
        return status;
    }
    
    public void setStatus(FlightStatus status) {
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
    
    public Long getOfferId() {
        return offerId;
    }
    
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public String getRouteName() {
        return routeName;
    }
    
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public Long getFlightDispatcherId() {
        return flightDispatcherId;
    }
    
    public void setFlightDispatcherId(Long flightDispatcherId) {
        this.flightDispatcherId = flightDispatcherId;
    }
    
    public String getFlightDispatcherName() {
        return flightDispatcherName;
    }
    
    public void setFlightDispatcherName(String flightDispatcherName) {
        this.flightDispatcherName = flightDispatcherName;
    }
    
    public Integer getScheduleCount() {
        return scheduleCount;
    }
    
    public void setScheduleCount(Integer scheduleCount) {
        this.scheduleCount = scheduleCount;
    }
}

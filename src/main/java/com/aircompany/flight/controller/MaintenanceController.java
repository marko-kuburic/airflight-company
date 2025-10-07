package com.aircompany.flight.controller;

import com.aircompany.flight.dto.MaintenanceRequestDto;
import com.aircompany.flight.dto.MaintenanceResponseDto;
import com.aircompany.flight.model.Maintenance.ServiceStatus;
import com.aircompany.flight.model.Maintenance.ServiceType;
import com.aircompany.flight.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/services")
@CrossOrigin(origins = "*")
public class MaintenanceController {
    
    @Autowired
    private MaintenanceService maintenanceService;
    
    @GetMapping
    public ResponseEntity<List<MaintenanceResponseDto>> getAllServices() {
        List<MaintenanceResponseDto> services = maintenanceService.getAllServices();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> getServiceById(@PathVariable Long id) {
        Optional<MaintenanceResponseDto> service = maintenanceService.getServiceById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MaintenanceResponseDto>> getServicesByStatus(@PathVariable ServiceStatus status) {
        List<MaintenanceResponseDto> services = maintenanceService.getServicesByStatus(status);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<MaintenanceResponseDto>> getServicesByType(@PathVariable ServiceType serviceType) {
        List<MaintenanceResponseDto> services = maintenanceService.getServicesByType(serviceType);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/aircraft/{aircraftId}")
    public ResponseEntity<List<MaintenanceResponseDto>> getServicesByAircraft(@PathVariable Long aircraftId) {
        List<MaintenanceResponseDto> services = maintenanceService.getServicesByAircraft(aircraftId);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/aircraft/{aircraftId}/active")
    public ResponseEntity<List<MaintenanceResponseDto>> getActiveServicesByAircraft(@PathVariable Long aircraftId) {
        List<MaintenanceResponseDto> services = maintenanceService.getActiveServicesByAircraft(aircraftId);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<MaintenanceResponseDto>> getServicesByTechnician(@PathVariable Long technicianId) {
        List<MaintenanceResponseDto> services = maintenanceService.getServicesByTechnician(technicianId);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/technician/{technicianId}/active")
    public ResponseEntity<List<MaintenanceResponseDto>> getActiveServicesByTechnician(@PathVariable Long technicianId) {
        List<MaintenanceResponseDto> services = maintenanceService.getActiveServicesByTechnician(technicianId);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<MaintenanceResponseDto>> getServicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<MaintenanceResponseDto> services = maintenanceService.getServicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getServiceCountByStatus(@PathVariable ServiceStatus status) {
        Long count = maintenanceService.getServiceCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<MaintenanceResponseDto> createService(@Valid @RequestBody MaintenanceRequestDto requestDto) {
        try {
            MaintenanceResponseDto createdService = maintenanceService.createService(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> updateService(@PathVariable Long id, 
                                                         @Valid @RequestBody MaintenanceRequestDto requestDto) {
        try {
            Optional<MaintenanceResponseDto> updatedService = maintenanceService.updateService(id, requestDto);
            return updatedService.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceResponseDto> updateServiceStatus(@PathVariable Long id, 
                                                               @RequestParam ServiceStatus status) {
        Optional<MaintenanceResponseDto> updatedService = maintenanceService.updateServiceStatus(id, status);
        return updatedService.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        boolean deleted = maintenanceService.deleteService(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

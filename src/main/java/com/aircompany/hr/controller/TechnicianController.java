package com.aircompany.hr.controller;

import com.aircompany.hr.dto.TechnicianRequestDto;
import com.aircompany.hr.dto.TechnicianResponseDto;
import com.aircompany.hr.service.TechnicianService;
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
@RequestMapping("/api/hr/technicians")
@CrossOrigin(origins = "*")
public class TechnicianController {
    
    @Autowired
    private TechnicianService technicianService;
    
    @GetMapping
    public ResponseEntity<List<TechnicianResponseDto>> getAllTechnicians() {
        List<TechnicianResponseDto> technicians = technicianService.getAllTechnicians();
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponseDto> getTechnicianById(@PathVariable Long id) {
        Optional<TechnicianResponseDto> technician = technicianService.getTechnicianById(id);
        return technician.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/specialization")
    public ResponseEntity<List<TechnicianResponseDto>> searchTechniciansBySpecialization(@RequestParam String specialization) {
        List<TechnicianResponseDto> technicians = technicianService.searchTechniciansBySpecialization(specialization);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/flight-hours/min/{minFlightHours}")
    public ResponseEntity<List<TechnicianResponseDto>> getTechniciansByMinFlightHours(@PathVariable Integer minFlightHours) {
        List<TechnicianResponseDto> technicians = technicianService.getTechniciansByMinFlightHours(minFlightHours);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/flight-hours/max/{maxFlightHours}")
    public ResponseEntity<List<TechnicianResponseDto>> getTechniciansByMaxFlightHours(@PathVariable Integer maxFlightHours) {
        List<TechnicianResponseDto> technicians = technicianService.getTechniciansByMaxFlightHours(maxFlightHours);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/flight-hours/range")
    public ResponseEntity<List<TechnicianResponseDto>> getTechniciansByFlightHoursRange(
            @RequestParam Integer minFlightHours, 
            @RequestParam Integer maxFlightHours) {
        List<TechnicianResponseDto> technicians = technicianService.getTechniciansByFlightHoursRange(minFlightHours, maxFlightHours);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/airport/{airportId}")
    public ResponseEntity<List<TechnicianResponseDto>> getTechniciansByAirport(@PathVariable Long airportId) {
        List<TechnicianResponseDto> technicians = technicianService.getTechniciansByAirport(airportId);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/specialization/{specialization}/airport/{airportId}")
    public ResponseEntity<List<TechnicianResponseDto>> getTechniciansBySpecializationAndAirport(
            @PathVariable String specialization, 
            @PathVariable Long airportId) {
        List<TechnicianResponseDto> technicians = technicianService.getTechniciansBySpecializationAndAirport(specialization, airportId);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<TechnicianResponseDto>> getAvailableTechnicians(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cutoffTime) {
        List<TechnicianResponseDto> technicians = technicianService.getAvailableTechnicians(cutoffTime);
        return ResponseEntity.ok(technicians);
    }
    
    @GetMapping("/{id}/with-airport")
    public ResponseEntity<TechnicianResponseDto> getTechnicianWithAirport(@PathVariable Long id) {
        Optional<TechnicianResponseDto> technician = technicianService.getTechnicianWithAirport(id);
        return technician.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-services")
    public ResponseEntity<TechnicianResponseDto> getTechnicianWithServices(@PathVariable Long id) {
        Optional<TechnicianResponseDto> technician = technicianService.getTechnicianWithServices(id);
        return technician.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count/specialization/{specialization}")
    public ResponseEntity<Long> getTechnicianCountBySpecialization(@PathVariable String specialization) {
        Long count = technicianService.getTechnicianCountBySpecialization(specialization);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<TechnicianResponseDto> createTechnician(@Valid @RequestBody TechnicianRequestDto requestDto) {
        try {
            TechnicianResponseDto createdTechnician = technicianService.createTechnician(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTechnician);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TechnicianResponseDto> updateTechnician(@PathVariable Long id, 
                                                               @Valid @RequestBody TechnicianRequestDto requestDto) {
        try {
            Optional<TechnicianResponseDto> updatedTechnician = technicianService.updateTechnician(id, requestDto);
            return updatedTechnician.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(@PathVariable Long id) {
        boolean deleted = technicianService.deleteTechnician(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

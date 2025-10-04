package com.aircompany.flight.controller;

import com.aircompany.flight.dto.AircraftRequestDto;
import com.aircompany.flight.dto.AircraftResponseDto;
import com.aircompany.flight.model.Aircraft.AircraftStatus;
import com.aircompany.flight.service.AircraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/aircraft")
@CrossOrigin(origins = "*")
public class AircraftController {
    
    @Autowired
    private AircraftService aircraftService;
    
    @GetMapping
    public ResponseEntity<List<AircraftResponseDto>> getAllAircraft() {
        List<AircraftResponseDto> aircraft = aircraftService.getAllAircraft();
        return ResponseEntity.ok(aircraft);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponseDto> getAircraftById(@PathVariable Long id) {
        Optional<AircraftResponseDto> aircraft = aircraftService.getAircraftById(id);
        return aircraft.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AircraftResponseDto>> getAircraftByStatus(@PathVariable AircraftStatus status) {
        List<AircraftResponseDto> aircraft = aircraftService.getAircraftByStatus(status);
        return ResponseEntity.ok(aircraft);
    }
    
    @GetMapping("/search/model")
    public ResponseEntity<List<AircraftResponseDto>> searchAircraftByModel(@RequestParam String model) {
        List<AircraftResponseDto> aircraft = aircraftService.searchAircraftByModel(model);
        return ResponseEntity.ok(aircraft);
    }
    
    @GetMapping("/capacity/range")
    public ResponseEntity<List<AircraftResponseDto>> getAircraftByCapacityRange(
            @RequestParam Integer minCapacity, 
            @RequestParam Integer maxCapacity) {
        List<AircraftResponseDto> aircraft = aircraftService.getAircraftByCapacityRange(minCapacity, maxCapacity);
        return ResponseEntity.ok(aircraft);
    }
    
    @GetMapping("/capacity/min/{minCapacity}")
    public ResponseEntity<List<AircraftResponseDto>> getAircraftByMinCapacity(@PathVariable Integer minCapacity) {
        List<AircraftResponseDto> aircraft = aircraftService.getAircraftByMinCapacity(minCapacity);
        return ResponseEntity.ok(aircraft);
    }
    
    @GetMapping("/{id}/with-service")
    public ResponseEntity<AircraftResponseDto> getAircraftWithService(@PathVariable Long id) {
        Optional<AircraftResponseDto> aircraft = aircraftService.getAircraftWithService(id);
        return aircraft.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-flights")
    public ResponseEntity<AircraftResponseDto> getAircraftWithFlights(@PathVariable Long id) {
        Optional<AircraftResponseDto> aircraft = aircraftService.getAircraftWithFlights(id);
        return aircraft.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getAircraftCountByStatus(@PathVariable AircraftStatus status) {
        Long count = aircraftService.getAircraftCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<AircraftResponseDto> createAircraft(@Valid @RequestBody AircraftRequestDto requestDto) {
        AircraftResponseDto createdAircraft = aircraftService.createAircraft(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAircraft);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponseDto> updateAircraft(@PathVariable Long id, 
                                                           @Valid @RequestBody AircraftRequestDto requestDto) {
        Optional<AircraftResponseDto> updatedAircraft = aircraftService.updateAircraft(id, requestDto);
        return updatedAircraft.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<AircraftResponseDto> updateAircraftStatus(@PathVariable Long id, 
                                                                 @RequestParam AircraftStatus status) {
        Optional<AircraftResponseDto> updatedAircraft = aircraftService.updateAircraftStatus(id, status);
        return updatedAircraft.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        boolean deleted = aircraftService.deleteAircraft(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

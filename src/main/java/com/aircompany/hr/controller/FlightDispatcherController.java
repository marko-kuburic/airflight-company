package com.aircompany.hr.controller;

import com.aircompany.hr.dto.FlightDispatcherRequestDto;
import com.aircompany.hr.dto.FlightDispatcherResponseDto;
import com.aircompany.hr.service.FlightDispatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hr/flight-dispatchers")
@CrossOrigin(origins = "*")
public class FlightDispatcherController {
    
    @Autowired
    private FlightDispatcherService flightDispatcherService;
    
    @GetMapping
    public ResponseEntity<List<FlightDispatcherResponseDto>> getAllFlightDispatchers() {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.getAllFlightDispatchers();
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlightDispatcherResponseDto> getFlightDispatcherById(@PathVariable Long id) {
        Optional<FlightDispatcherResponseDto> flightDispatcher = flightDispatcherService.getFlightDispatcherById(id);
        return flightDispatcher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/license")
    public ResponseEntity<List<FlightDispatcherResponseDto>> searchFlightDispatchersByLicense(@RequestParam String license) {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.searchFlightDispatchersByLicense(license);
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/experience/min/{minExperience}")
    public ResponseEntity<List<FlightDispatcherResponseDto>> getFlightDispatchersByMinExperience(@PathVariable Integer minExperience) {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.getFlightDispatchersByMinExperience(minExperience);
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/aircraft-type")
    public ResponseEntity<List<FlightDispatcherResponseDto>> getFlightDispatchersByAircraftType(@RequestParam String aircraftType) {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.getFlightDispatchersByAircraftType(aircraftType);
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/airport/{airportId}")
    public ResponseEntity<List<FlightDispatcherResponseDto>> getFlightDispatchersByAirport(@PathVariable Long airportId) {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.getFlightDispatchersByAirport(airportId);
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/valid-license")
    public ResponseEntity<List<FlightDispatcherResponseDto>> getFlightDispatchersWithValidLicense() {
        List<FlightDispatcherResponseDto> flightDispatchers = flightDispatcherService.getFlightDispatchersWithValidLicense();
        return ResponseEntity.ok(flightDispatchers);
    }
    
    @GetMapping("/{id}/with-airport")
    public ResponseEntity<FlightDispatcherResponseDto> getFlightDispatcherWithAirport(@PathVariable Long id) {
        Optional<FlightDispatcherResponseDto> flightDispatcher = flightDispatcherService.getFlightDispatcherWithAirport(id);
        return flightDispatcher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-flights")
    public ResponseEntity<FlightDispatcherResponseDto> getFlightDispatcherWithFlights(@PathVariable Long id) {
        Optional<FlightDispatcherResponseDto> flightDispatcher = flightDispatcherService.getFlightDispatcherWithFlights(id);
        return flightDispatcher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count/experience/min/{minExperience}")
    public ResponseEntity<Long> getFlightDispatcherCountByMinExperience(@PathVariable Integer minExperience) {
        Long count = flightDispatcherService.getFlightDispatcherCountByMinExperience(minExperience);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<FlightDispatcherResponseDto> createFlightDispatcher(@Valid @RequestBody FlightDispatcherRequestDto requestDto) {
        try {
            FlightDispatcherResponseDto createdFlightDispatcher = flightDispatcherService.createFlightDispatcher(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFlightDispatcher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FlightDispatcherResponseDto> updateFlightDispatcher(@PathVariable Long id, 
                                                                           @Valid @RequestBody FlightDispatcherRequestDto requestDto) {
        try {
            Optional<FlightDispatcherResponseDto> updatedFlightDispatcher = flightDispatcherService.updateFlightDispatcher(id, requestDto);
            return updatedFlightDispatcher.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightDispatcher(@PathVariable Long id) {
        boolean deleted = flightDispatcherService.deleteFlightDispatcher(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

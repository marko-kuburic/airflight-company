package com.aircompany.flight.controller;

import com.aircompany.flight.dto.AirportRequestDto;
import com.aircompany.flight.dto.AirportResponseDto;
import com.aircompany.flight.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/airports")
@CrossOrigin(origins = "*")
public class AirportController {
    
    @Autowired
    private AirportService airportService;
    
    @GetMapping
    public ResponseEntity<List<AirportResponseDto>> getAllAirports() {
        List<AirportResponseDto> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AirportResponseDto> getAirportById(@PathVariable Long id) {
        Optional<AirportResponseDto> airport = airportService.getAirportById(id);
        return airport.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/iata/{iataCode}")
    public ResponseEntity<AirportResponseDto> getAirportByIataCode(@PathVariable String iataCode) {
        Optional<AirportResponseDto> airport = airportService.getAirportByIataCode(iataCode);
        return airport.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/city")
    public ResponseEntity<List<AirportResponseDto>> searchAirportsByCity(@RequestParam String city) {
        List<AirportResponseDto> airports = airportService.searchAirportsByCity(city);
        return ResponseEntity.ok(airports);
    }
    
    @GetMapping("/search/name")
    public ResponseEntity<List<AirportResponseDto>> searchAirportsByName(@RequestParam String name) {
        List<AirportResponseDto> airports = airportService.searchAirportsByName(name);
        return ResponseEntity.ok(airports);
    }
    
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<AirportResponseDto>> getAirportsByCountryCode(@PathVariable String countryCode) {
        List<AirportResponseDto> airports = airportService.getAirportsByCountryCode(countryCode);
        return ResponseEntity.ok(airports);
    }
    
    @PostMapping
    public ResponseEntity<AirportResponseDto> createAirport(@Valid @RequestBody AirportRequestDto requestDto) {
        try {
            AirportResponseDto createdAirport = airportService.createAirport(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAirport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AirportResponseDto> updateAirport(@PathVariable Long id, 
                                                          @Valid @RequestBody AirportRequestDto requestDto) {
        try {
            Optional<AirportResponseDto> updatedAirport = airportService.updateAirport(id, requestDto);
            return updatedAirport.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) {
        boolean deleted = airportService.deleteAirport(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

package com.aircompany.flight.controller;

import com.aircompany.flight.dto.CountryRequestDto;
import com.aircompany.flight.dto.CountryResponseDto;
import com.aircompany.flight.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/countries")
@CrossOrigin(origins = "*")
public class CountryController {
    
    @Autowired
    private CountryService countryService;
    
    @GetMapping
    public ResponseEntity<List<CountryResponseDto>> getAllCountries() {
        List<CountryResponseDto> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<CountryResponseDto> getCountryByCode(@PathVariable String code) {
        Optional<CountryResponseDto> country = countryService.getCountryByCode(code);
        return country.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CountryResponseDto>> searchCountriesByName(@RequestParam String name) {
        List<CountryResponseDto> countries = countryService.searchCountriesByName(name);
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/{code}/with-airports")
    public ResponseEntity<CountryResponseDto> getCountryWithAirports(@PathVariable String code) {
        Optional<CountryResponseDto> country = countryService.getCountryWithAirports(code);
        return country.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CountryResponseDto> createCountry(@Valid @RequestBody CountryRequestDto requestDto) {
        try {
            CountryResponseDto createdCountry = countryService.createCountry(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCountry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{code}")
    public ResponseEntity<CountryResponseDto> updateCountry(@PathVariable String code, 
                                                          @Valid @RequestBody CountryRequestDto requestDto) {
        Optional<CountryResponseDto> updatedCountry = countryService.updateCountry(code, requestDto);
        return updatedCountry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCountry(@PathVariable String code) {
        boolean deleted = countryService.deleteCountry(code);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

package com.aircompany.hr.service;

import com.aircompany.flight.model.Airport;
import com.aircompany.flight.repository.AirportRepository;
import com.aircompany.hr.dto.FlightDispatcherRequestDto;
import com.aircompany.hr.dto.FlightDispatcherResponseDto;
import com.aircompany.hr.model.FlightDispatcher;
import com.aircompany.hr.repository.FlightDispatcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightDispatcherService {
    
    @Autowired
    private FlightDispatcherRepository flightDispatcherRepository;
    
    @Autowired
    private AirportRepository airportRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<FlightDispatcherResponseDto> getAllFlightDispatchers() {
        return flightDispatcherRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<FlightDispatcherResponseDto> getFlightDispatcherById(Long id) {
        return flightDispatcherRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<FlightDispatcherResponseDto> searchFlightDispatchersByLicense(String license) {
        return flightDispatcherRepository.findByDispatchLicenseContainingIgnoreCase(license).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightDispatcherResponseDto> getFlightDispatchersByMinExperience(Integer minExperience) {
        return flightDispatcherRepository.findByExperienceYearsGreaterThanEqual(minExperience).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightDispatcherResponseDto> getFlightDispatchersByAircraftType(String aircraftType) {
        return flightDispatcherRepository.findByAircraftTypesQualifiedContainingIgnoreCase(aircraftType).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightDispatcherResponseDto> getFlightDispatchersByAirport(Long airportId) {
        return flightDispatcherRepository.findByAirportId(airportId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public FlightDispatcherResponseDto createFlightDispatcher(FlightDispatcherRequestDto requestDto) {
        Airport airport = airportRepository.findById(requestDto.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Airport with ID " + requestDto.getAirportId() + " not found"));
        
        FlightDispatcher flightDispatcher = new FlightDispatcher(
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword())
        );
        
        flightDispatcher.setDispatchLicense(requestDto.getDispatchLicense());
        flightDispatcher.setExperienceYears(requestDto.getExperienceYears());
        flightDispatcher.setAircraftTypesQualified(requestDto.getAircraftTypesQualified());
        flightDispatcher.setAirport(airport);
        
        FlightDispatcher savedFlightDispatcher = flightDispatcherRepository.save(flightDispatcher);
        return convertToResponseDto(savedFlightDispatcher);
    }
    
    public Optional<FlightDispatcherResponseDto> updateFlightDispatcher(Long id, FlightDispatcherRequestDto requestDto) {
        return flightDispatcherRepository.findById(id)
                .map(flightDispatcher -> {
                    Airport airport = airportRepository.findById(requestDto.getAirportId())
                            .orElseThrow(() -> new IllegalArgumentException("Airport with ID " + requestDto.getAirportId() + " not found"));
                    
                    flightDispatcher.setFirstName(requestDto.getFirstName());
                    flightDispatcher.setLastName(requestDto.getLastName());
                    flightDispatcher.setEmail(requestDto.getEmail());
                    
                    if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
                        flightDispatcher.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                    }
                    
                    flightDispatcher.setDispatchLicense(requestDto.getDispatchLicense());
                    flightDispatcher.setExperienceYears(requestDto.getExperienceYears());
                    flightDispatcher.setAircraftTypesQualified(requestDto.getAircraftTypesQualified());
                    flightDispatcher.setAirport(airport);
                    
                    FlightDispatcher savedFlightDispatcher = flightDispatcherRepository.save(flightDispatcher);
                    return convertToResponseDto(savedFlightDispatcher);
                });
    }
    
    public boolean deleteFlightDispatcher(Long id) {
        if (flightDispatcherRepository.existsById(id)) {
            flightDispatcherRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getFlightDispatcherCountByMinExperience(Integer minExperience) {
        return flightDispatcherRepository.countByMinExperience(minExperience);
    }
    
    public List<FlightDispatcherResponseDto> getFlightDispatchersWithValidLicense() {
        return flightDispatcherRepository.findWithValidLicense().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<FlightDispatcherResponseDto> getFlightDispatcherWithAirport(Long id) {
        return flightDispatcherRepository.findByIdWithAirport(id)
                .map(this::convertToResponseDto);
    }
    
    public Optional<FlightDispatcherResponseDto> getFlightDispatcherWithFlights(Long id) {
        return flightDispatcherRepository.findByIdWithFlights(id)
                .map(flightDispatcher -> {
                    FlightDispatcherResponseDto responseDto = convertToResponseDto(flightDispatcher);
                    responseDto.setFlightCount(flightDispatcher.getFlights().size());
                    return responseDto;
                });
    }
    
    private FlightDispatcherResponseDto convertToResponseDto(FlightDispatcher flightDispatcher) {
        FlightDispatcherResponseDto responseDto = new FlightDispatcherResponseDto();
        responseDto.setId(flightDispatcher.getId());
        responseDto.setFirstName(flightDispatcher.getFirstName());
        responseDto.setLastName(flightDispatcher.getLastName());
        responseDto.setEmail(flightDispatcher.getEmail());
        responseDto.setDispatchLicense(flightDispatcher.getDispatchLicense());
        responseDto.setExperienceYears(flightDispatcher.getExperienceYears());
        responseDto.setAircraftTypesQualified(flightDispatcher.getAircraftTypesQualified());
        responseDto.setCreatedAt(flightDispatcher.getCreatedAt());
        responseDto.setModifiedAt(flightDispatcher.getModifiedAt());
        
        if (flightDispatcher.getAirport() != null) {
            responseDto.setAirportId(flightDispatcher.getAirport().getId());
            responseDto.setAirportCode(flightDispatcher.getAirport().getIataCode());
            responseDto.setAirportName(flightDispatcher.getAirport().getName());
        }
        
        return responseDto;
    }
}

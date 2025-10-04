package com.aircompany.hr.service;

import com.aircompany.flight.model.Airport;
import com.aircompany.flight.repository.AirportRepository;
import com.aircompany.hr.dto.TechnicianRequestDto;
import com.aircompany.hr.dto.TechnicianResponseDto;
import com.aircompany.hr.model.Technician;
import com.aircompany.hr.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TechnicianService {
    
    @Autowired
    private TechnicianRepository technicianRepository;
    
    @Autowired
    private AirportRepository airportRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<TechnicianResponseDto> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<TechnicianResponseDto> getTechnicianById(Long id) {
        return technicianRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<TechnicianResponseDto> searchTechniciansBySpecialization(String specialization) {
        return technicianRepository.findBySpecializationContainingIgnoreCase(specialization).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<TechnicianResponseDto> getTechniciansByMinFlightHours(Integer minFlightHours) {
        return technicianRepository.findByFlightHoursGreaterThanEqual(minFlightHours).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<TechnicianResponseDto> getTechniciansByMaxFlightHours(Integer maxFlightHours) {
        return technicianRepository.findByFlightHoursLessThanEqual(maxFlightHours).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<TechnicianResponseDto> getTechniciansByFlightHoursRange(Integer minFlightHours, Integer maxFlightHours) {
        return technicianRepository.findByFlightHoursBetween(minFlightHours, maxFlightHours).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<TechnicianResponseDto> getTechniciansByAirport(Long airportId) {
        return technicianRepository.findByAirportId(airportId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public TechnicianResponseDto createTechnician(TechnicianRequestDto requestDto) {
        Airport airport = airportRepository.findById(requestDto.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Airport with ID " + requestDto.getAirportId() + " not found"));
        
        Technician technician = new Technician(
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword())
        );
        
        technician.setFlightHours(requestDto.getFlightHours());
        technician.setLastDutyEnd(requestDto.getLastDutyEnd());
        technician.setSpecialization(requestDto.getSpecialization());
        technician.setAirport(airport);
        
        Technician savedTechnician = technicianRepository.save(technician);
        return convertToResponseDto(savedTechnician);
    }
    
    public Optional<TechnicianResponseDto> updateTechnician(Long id, TechnicianRequestDto requestDto) {
        return technicianRepository.findById(id)
                .map(technician -> {
                    Airport airport = airportRepository.findById(requestDto.getAirportId())
                            .orElseThrow(() -> new IllegalArgumentException("Airport with ID " + requestDto.getAirportId() + " not found"));
                    
                    technician.setFirstName(requestDto.getFirstName());
                    technician.setLastName(requestDto.getLastName());
                    technician.setEmail(requestDto.getEmail());
                    
                    if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
                        technician.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                    }
                    
                    technician.setFlightHours(requestDto.getFlightHours());
                    technician.setLastDutyEnd(requestDto.getLastDutyEnd());
                    technician.setSpecialization(requestDto.getSpecialization());
                    technician.setAirport(airport);
                    
                    Technician savedTechnician = technicianRepository.save(technician);
                    return convertToResponseDto(savedTechnician);
                });
    }
    
    public boolean deleteTechnician(Long id) {
        if (technicianRepository.existsById(id)) {
            technicianRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<TechnicianResponseDto> getAvailableTechnicians(LocalDateTime cutoffTime) {
        return technicianRepository.findAvailableTechnicians(cutoffTime).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Long getTechnicianCountBySpecialization(String specialization) {
        return technicianRepository.countBySpecialization(specialization);
    }
    
    public List<TechnicianResponseDto> getTechniciansBySpecializationAndAirport(String specialization, Long airportId) {
        return technicianRepository.findBySpecializationAndAirport(specialization, airportId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<TechnicianResponseDto> getTechnicianWithAirport(Long id) {
        return technicianRepository.findByIdWithAirport(id)
                .map(this::convertToResponseDto);
    }
    
    public Optional<TechnicianResponseDto> getTechnicianWithServices(Long id) {
        return technicianRepository.findByIdWithServices(id)
                .map(technician -> {
                    TechnicianResponseDto responseDto = convertToResponseDto(technician);
                    responseDto.setServiceCount(technician.getServices().size());
                    return responseDto;
                });
    }
    
    private TechnicianResponseDto convertToResponseDto(Technician technician) {
        TechnicianResponseDto responseDto = new TechnicianResponseDto();
        responseDto.setId(technician.getId());
        responseDto.setFirstName(technician.getFirstName());
        responseDto.setLastName(technician.getLastName());
        responseDto.setEmail(technician.getEmail());
        responseDto.setFlightHours(technician.getFlightHours());
        responseDto.setLastDutyEnd(technician.getLastDutyEnd());
        responseDto.setSpecialization(technician.getSpecialization());
        responseDto.setCreatedAt(technician.getCreatedAt());
        responseDto.setModifiedAt(technician.getModifiedAt());
        
        if (technician.getAirport() != null) {
            responseDto.setAirportId(technician.getAirport().getId());
            responseDto.setAirportCode(technician.getAirport().getIataCode());
            responseDto.setAirportName(technician.getAirport().getName());
        }
        
        return responseDto;
    }
}

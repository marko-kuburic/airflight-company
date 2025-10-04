package com.aircompany.flight.service;

import com.aircompany.flight.dto.AircraftRequestDto;
import com.aircompany.flight.dto.AircraftResponseDto;
import com.aircompany.flight.model.Aircraft;
import com.aircompany.flight.model.Aircraft.AircraftStatus;
import com.aircompany.flight.repository.AircraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AircraftService {
    
    @Autowired
    private AircraftRepository aircraftRepository;
    
    public List<AircraftResponseDto> getAllAircraft() {
        return aircraftRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<AircraftResponseDto> getAircraftById(Long id) {
        return aircraftRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<AircraftResponseDto> getAircraftByStatus(AircraftStatus status) {
        return aircraftRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<AircraftResponseDto> getAircraftByCapacity(Integer minCapacity, Integer maxCapacity) {
        return aircraftRepository.findByCapacityBetween(minCapacity, maxCapacity).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public AircraftResponseDto createAircraft(AircraftRequestDto requestDto) {
        Aircraft aircraft = new Aircraft(requestDto.getModel(), requestDto.getStatus(), requestDto.getCapacity());
        Aircraft savedAircraft = aircraftRepository.save(aircraft);
        return convertToResponseDto(savedAircraft);
    }
    
    public Optional<AircraftResponseDto> updateAircraft(Long id, AircraftRequestDto requestDto) {
        return aircraftRepository.findById(id)
                .map(aircraft -> {
                    aircraft.setModel(requestDto.getModel());
                    aircraft.setStatus(requestDto.getStatus());
                    aircraft.setCapacity(requestDto.getCapacity());
                    Aircraft savedAircraft = aircraftRepository.save(aircraft);
                    return convertToResponseDto(savedAircraft);
                });
    }
    
    public Optional<AircraftResponseDto> updateAircraftStatus(Long id, AircraftStatus status) {
        return aircraftRepository.findById(id)
                .map(aircraft -> {
                    aircraft.setStatus(status);
                    Aircraft savedAircraft = aircraftRepository.save(aircraft);
                    return convertToResponseDto(savedAircraft);
                });
    }
    
    public boolean deleteAircraft(Long id) {
        if (aircraftRepository.existsById(id)) {
            aircraftRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getAircraftCountByStatus(AircraftStatus status) {
        return aircraftRepository.countByStatus(status);
    }
    
    public Long getTotalAircraftCount() {
        return aircraftRepository.count();
    }
    
    public List<AircraftResponseDto> searchAircraftByModel(String model) {
        return aircraftRepository.findByModelContainingIgnoreCase(model).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<AircraftResponseDto> getAircraftByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        return aircraftRepository.findByCapacityBetween(minCapacity, maxCapacity).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<AircraftResponseDto> getAircraftByMinCapacity(Integer minCapacity) {
        return aircraftRepository.findByCapacityGreaterThanEqual(minCapacity).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<AircraftResponseDto> getAircraftWithService(Long id) {
        return aircraftRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public Optional<AircraftResponseDto> getAircraftWithFlights(Long id) {
        return aircraftRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    private AircraftResponseDto convertToResponseDto(Aircraft aircraft) {
        AircraftResponseDto responseDto = new AircraftResponseDto();
        responseDto.setId(aircraft.getId());
        responseDto.setModel(aircraft.getModel());
        responseDto.setStatus(aircraft.getStatus());
        responseDto.setCapacity(aircraft.getCapacity());
        responseDto.setCreatedAt(aircraft.getCreatedAt());
        responseDto.setModifiedAt(aircraft.getModifiedAt());
        
        // Service information can be added later if needed
        
        responseDto.setFlightCount(aircraft.getFlights().size());
        
        return responseDto;
    }
}
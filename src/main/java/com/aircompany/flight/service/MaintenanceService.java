package com.aircompany.flight.service;

import com.aircompany.flight.dto.MaintenanceRequestDto;
import com.aircompany.flight.dto.MaintenanceResponseDto;
import com.aircompany.flight.model.Aircraft;
import com.aircompany.flight.model.Maintenance;
import com.aircompany.flight.model.Maintenance.ServiceStatus;
import com.aircompany.flight.model.Maintenance.ServiceType;
import com.aircompany.flight.repository.AircraftRepository;
import com.aircompany.flight.repository.MaintenanceRepository;
import com.aircompany.hr.model.Technician;
import com.aircompany.hr.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenanceService {
    
    @Autowired
    private MaintenanceRepository maintenanceRepository;
    
    @Autowired
    private AircraftRepository aircraftRepository;
    
    @Autowired
    private TechnicianRepository technicianRepository;
    
    public List<MaintenanceResponseDto> getAllServices() {
        return maintenanceRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<MaintenanceResponseDto> getServiceById(Long id) {
        return maintenanceRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<MaintenanceResponseDto> getServicesByStatus(ServiceStatus status) {
        return maintenanceRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<MaintenanceResponseDto> getServicesByType(ServiceType serviceType) {
        return maintenanceRepository.findByServiceType(serviceType).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<MaintenanceResponseDto> getServicesByAircraft(Long aircraftId) {
        return maintenanceRepository.findByAircraftId(aircraftId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<MaintenanceResponseDto> getServicesByTechnician(Long technicianId) {
        return maintenanceRepository.findByTechnicianId(technicianId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<MaintenanceResponseDto> getServicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return maintenanceRepository.findByStartDateBetween(startDate, endDate).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public MaintenanceResponseDto createService(MaintenanceRequestDto requestDto) {
        Aircraft aircraft = aircraftRepository.findById(requestDto.getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Aircraft with ID " + requestDto.getAircraftId() + " not found"));
        
        Maintenance service = new Maintenance(requestDto.getServiceType(), requestDto.getDescription(), aircraft);
        service.setStartDate(requestDto.getStartDate());
        service.setEndDate(requestDto.getEndDate());
        service.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : ServiceStatus.SCHEDULED);
        
        if (requestDto.getTechnicianId() != null) {
            Technician technician = technicianRepository.findById(requestDto.getTechnicianId())
                    .orElseThrow(() -> new IllegalArgumentException("Technician with ID " + requestDto.getTechnicianId() + " not found"));
            service.setTechnician(technician);
        }
        
        Maintenance savedService = maintenanceRepository.save(service);
        return convertToResponseDto(savedService);
    }
    
    public Optional<MaintenanceResponseDto> updateService(Long id, MaintenanceRequestDto requestDto) {
        return maintenanceRepository.findById(id)
                .map(service -> {
                    Aircraft aircraft = aircraftRepository.findById(requestDto.getAircraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Aircraft with ID " + requestDto.getAircraftId() + " not found"));
                    
                    service.setServiceType(requestDto.getServiceType());
                    service.setDescription(requestDto.getDescription());
                    service.setStartDate(requestDto.getStartDate());
                    service.setEndDate(requestDto.getEndDate());
                    service.setStatus(requestDto.getStatus());
                    service.setAircraft(aircraft);
                    
                    if (requestDto.getTechnicianId() != null) {
                        Technician technician = technicianRepository.findById(requestDto.getTechnicianId())
                                .orElseThrow(() -> new IllegalArgumentException("Technician with ID " + requestDto.getTechnicianId() + " not found"));
                        service.setTechnician(technician);
                    } else {
                        service.setTechnician(null);
                    }
                    
                    Maintenance savedService = maintenanceRepository.save(service);
                    return convertToResponseDto(savedService);
                });
    }
    
    public Optional<MaintenanceResponseDto> updateServiceStatus(Long id, ServiceStatus status) {
        return maintenanceRepository.findById(id)
                .map(service -> {
                    service.setStatus(status);
                    Maintenance savedService = maintenanceRepository.save(service);
                    return convertToResponseDto(savedService);
                });
    }
    
    public boolean deleteService(Long id) {
        if (maintenanceRepository.existsById(id)) {
            maintenanceRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getServiceCountByStatus(ServiceStatus status) {
        return maintenanceRepository.countByStatus(status);
    }
    
    public List<MaintenanceResponseDto> getActiveServicesByAircraft(Long aircraftId) {
        return maintenanceRepository.findByAircraftIdAndStatusIn(aircraftId, 
                List.of(ServiceStatus.SCHEDULED, ServiceStatus.IN_PROGRESS)).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<MaintenanceResponseDto> getActiveServicesByTechnician(Long technicianId) {
        return maintenanceRepository.findByTechnicianIdAndStatusIn(technicianId, 
                List.of(ServiceStatus.SCHEDULED, ServiceStatus.IN_PROGRESS)).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    private MaintenanceResponseDto convertToResponseDto(Maintenance service) {
        MaintenanceResponseDto responseDto = new MaintenanceResponseDto();
        responseDto.setId(service.getId());
        responseDto.setServiceType(service.getServiceType());
        responseDto.setDescription(service.getDescription());
        responseDto.setStartDate(service.getStartDate());
        responseDto.setEndDate(service.getEndDate());
        responseDto.setStatus(service.getStatus());
        responseDto.setCreatedAt(service.getCreatedAt());
        responseDto.setModifiedAt(service.getModifiedAt());
        
        if (service.getAircraft() != null) {
            responseDto.setAircraftId(service.getAircraft().getId());
            responseDto.setAircraftModel(service.getAircraft().getModel());
        }
        
        if (service.getTechnician() != null) {
            responseDto.setTechnicianId(service.getTechnician().getId());
            responseDto.setTechnicianName(
                    service.getTechnician().getFirstName() + " " + 
                    service.getTechnician().getLastName()
            );
        }
        
        return responseDto;
    }
}
package com.aircompany.flight.service;

import com.aircompany.flight.dto.SegmentRequestDto;
import com.aircompany.flight.dto.SegmentResponseDto;
import com.aircompany.flight.model.Airport;
import com.aircompany.flight.model.Route;
import com.aircompany.flight.model.Segment;
import com.aircompany.flight.repository.AirportRepository;
import com.aircompany.flight.repository.RouteRepository;
import com.aircompany.flight.repository.SegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SegmentService {
    
    @Autowired
    private SegmentRepository segmentRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private AirportRepository airportRepository;
    
    public List<SegmentResponseDto> getAllSegments() {
        return segmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<SegmentResponseDto> getSegmentById(Long id) {
        return segmentRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<SegmentResponseDto> getSegmentsByRoute(Long routeId) {
        return segmentRepository.findByRouteId(routeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<SegmentResponseDto> getSegmentsByOriginAirport(Long originAirportId) {
        return segmentRepository.findByOriginAirportId(originAirportId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<SegmentResponseDto> getSegmentsByDestinationAirport(Long destinationAirportId) {
        return segmentRepository.findByDestinationAirportId(destinationAirportId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<SegmentResponseDto> getSegmentsByDistanceRange(BigDecimal minDistance, BigDecimal maxDistance) {
        return segmentRepository.findByDistanceBetween(minDistance, maxDistance).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public SegmentResponseDto createSegment(SegmentRequestDto requestDto) {
        Route route = routeRepository.findById(requestDto.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("Route with ID " + requestDto.getRouteId() + " not found"));
        
        Airport originAirport = airportRepository.findById(requestDto.getOriginAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Origin airport with ID " + requestDto.getOriginAirportId() + " not found"));
        
        Airport destinationAirport = airportRepository.findById(requestDto.getDestinationAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Destination airport with ID " + requestDto.getDestinationAirportId() + " not found"));
        
        Segment segment = new Segment();
        segment.setRoute(route);
        segment.setOriginAirport(originAirport);
        segment.setDestinationAirport(destinationAirport);
        segment.setDistance(requestDto.getDistance());
        
        Segment savedSegment = segmentRepository.save(segment);
        return convertToResponseDto(savedSegment);
    }
    
    public Optional<SegmentResponseDto> updateSegment(Long id, SegmentRequestDto requestDto) {
        return segmentRepository.findById(id)
                .map(segment -> {
                    Route route = routeRepository.findById(requestDto.getRouteId())
                            .orElseThrow(() -> new IllegalArgumentException("Route with ID " + requestDto.getRouteId() + " not found"));
                    
                    Airport originAirport = airportRepository.findById(requestDto.getOriginAirportId())
                            .orElseThrow(() -> new IllegalArgumentException("Origin airport with ID " + requestDto.getOriginAirportId() + " not found"));
                    
                    Airport destinationAirport = airportRepository.findById(requestDto.getDestinationAirportId())
                            .orElseThrow(() -> new IllegalArgumentException("Destination airport with ID " + requestDto.getDestinationAirportId() + " not found"));
                    
                    segment.setRoute(route);
                    segment.setOriginAirport(originAirport);
                    segment.setDestinationAirport(destinationAirport);
                    segment.setDistance(requestDto.getDistance());
                    
                    Segment savedSegment = segmentRepository.save(segment);
                    return convertToResponseDto(savedSegment);
                });
    }
    
    public boolean deleteSegment(Long id) {
        if (segmentRepository.existsById(id)) {
            segmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<SegmentResponseDto> getSegmentsByRouteOrdered(Long routeId) {
        return segmentRepository.findByRouteIdOrderById(routeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<SegmentResponseDto> getSegmentsByOriginAndDestination(Long originId, Long destinationId) {
        return segmentRepository.findByOriginAndDestination(originId, destinationId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    private SegmentResponseDto convertToResponseDto(Segment segment) {
        SegmentResponseDto responseDto = new SegmentResponseDto();
        responseDto.setId(segment.getId());
        responseDto.setRouteId(segment.getRoute().getId());
        responseDto.setRouteName(segment.getRoute().getName());
        responseDto.setOriginAirportId(segment.getOriginAirport().getId());
        responseDto.setOriginAirportCode(segment.getOriginAirport().getIataCode());
        responseDto.setOriginAirportName(segment.getOriginAirport().getName());
        responseDto.setDestinationAirportId(segment.getDestinationAirport().getId());
        responseDto.setDestinationAirportCode(segment.getDestinationAirport().getIataCode());
        responseDto.setDestinationAirportName(segment.getDestinationAirport().getName());
        responseDto.setDistance(segment.getDistance());
        responseDto.setCreatedAt(segment.getCreatedAt());
        responseDto.setModifiedAt(segment.getModifiedAt());
        return responseDto;
    }
}

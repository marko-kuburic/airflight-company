package com.aircompany.flight.service;

import com.aircompany.flight.dto.RouteRequestDto;
import com.aircompany.flight.dto.RouteResponseDto;
import com.aircompany.flight.model.Route;
import com.aircompany.flight.model.Segment;
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
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private SegmentRepository segmentRepository;
    
    public List<RouteResponseDto> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<RouteResponseDto> getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public List<RouteResponseDto> searchRoutesByName(String name) {
        return routeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<RouteResponseDto> getRoutesByDistanceRange(BigDecimal minDistance, BigDecimal maxDistance) {
        return routeRepository.findByTotalDistanceBetween(minDistance, maxDistance).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<RouteResponseDto> getRoutesByMinDistance(BigDecimal minDistance) {
        return routeRepository.findByTotalDistanceGreaterThanEqual(minDistance).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<RouteResponseDto> getRoutesByMaxDistance(BigDecimal maxDistance) {
        return routeRepository.findByTotalDistanceLessThanEqual(maxDistance).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public RouteResponseDto createRoute(RouteRequestDto requestDto) {
        Route route = new Route(requestDto.getName(), BigDecimal.ZERO); // Will be calculated from segments
        Route savedRoute = routeRepository.save(route);
        return convertToResponseDto(savedRoute);
    }
    
    public Optional<RouteResponseDto> updateRoute(Long id, RouteRequestDto requestDto) {
        return routeRepository.findById(id)
                .map(route -> {
                    route.setName(requestDto.getName());
                    route.setTotalDistance(requestDto.getTotalDistance());
                    Route savedRoute = routeRepository.save(route);
                    return convertToResponseDto(savedRoute);
                });
    }
    
    public boolean deleteRoute(Long id) {
        if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getTotalRouteCount() {
        return routeRepository.countAllRoutes();
    }
    
    public Optional<RouteResponseDto> getRouteWithSegments(Long id) {
        return routeRepository.findByIdWithSegments(id)
                .map(route -> {
                    RouteResponseDto responseDto = convertToResponseDto(route);
                    responseDto.setSegmentCount(route.getSegments().size());
                    return responseDto;
                });
    }
    
    public Optional<RouteResponseDto> getRouteWithFlights(Long id) {
        return routeRepository.findByIdWithFlights(id)
                .map(route -> {
                    RouteResponseDto responseDto = convertToResponseDto(route);
                    responseDto.setFlightCount(route.getFlights().size());
                    return responseDto;
                });
    }
    
    private RouteResponseDto convertToResponseDto(Route route) {
        RouteResponseDto responseDto = new RouteResponseDto();
        responseDto.setId(route.getId());
        responseDto.setName(route.getName());
        
        // Calculate total distance from segments
        BigDecimal totalDistance = calculateTotalDistanceFromSegments(route);
        responseDto.setTotalDistance(totalDistance);
        
        responseDto.setCreatedAt(route.getCreatedAt());
        responseDto.setModifiedAt(route.getModifiedAt());
        return responseDto;
    }
    
    /**
     * Calculate total distance from all segments of a route
     */
    private BigDecimal calculateTotalDistanceFromSegments(Route route) {
        return route.getSegments().stream()
                .map(Segment::getDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Recalculate and update total distance for a route based on its segments
     */
    public void recalculateTotalDistance(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route with ID " + routeId + " not found"));
        
        // Explicitly fetch segments to avoid lazy loading issues
        List<Segment> segments = segmentRepository.findByRouteId(routeId);
        BigDecimal totalDistance = segments.stream()
                .map(Segment::getDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        route.setTotalDistance(totalDistance);
        routeRepository.save(route);
    }
}

package com.aircompany.flight.service;

import com.aircompany.flight.dto.RouteRequestDto;
import com.aircompany.flight.dto.RouteResponseDto;
import com.aircompany.flight.model.Route;
import com.aircompany.flight.repository.RouteRepository;
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
        Route route = new Route(requestDto.getName(), requestDto.getTotalDistance());
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
        responseDto.setTotalDistance(route.getTotalDistance());
        responseDto.setCreatedAt(route.getCreatedAt());
        responseDto.setModifiedAt(route.getModifiedAt());
        return responseDto;
    }
}

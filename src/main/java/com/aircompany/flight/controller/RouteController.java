package com.aircompany.flight.controller;

import com.aircompany.flight.dto.RouteRequestDto;
import com.aircompany.flight.dto.RouteResponseDto;
import com.aircompany.flight.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/routes")
@CrossOrigin(origins = "*")
public class RouteController {
    
    @Autowired
    private RouteService routeService;
    
    @GetMapping
    public ResponseEntity<List<RouteResponseDto>> getAllRoutes() {
        List<RouteResponseDto> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDto> getRouteById(@PathVariable Long id) {
        Optional<RouteResponseDto> route = routeService.getRouteById(id);
        return route.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/name")
    public ResponseEntity<List<RouteResponseDto>> searchRoutesByName(@RequestParam String name) {
        List<RouteResponseDto> routes = routeService.searchRoutesByName(name);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/distance/range")
    public ResponseEntity<List<RouteResponseDto>> getRoutesByDistanceRange(
            @RequestParam BigDecimal minDistance, 
            @RequestParam BigDecimal maxDistance) {
        List<RouteResponseDto> routes = routeService.getRoutesByDistanceRange(minDistance, maxDistance);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/distance/min/{minDistance}")
    public ResponseEntity<List<RouteResponseDto>> getRoutesByMinDistance(@PathVariable BigDecimal minDistance) {
        List<RouteResponseDto> routes = routeService.getRoutesByMinDistance(minDistance);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/distance/max/{maxDistance}")
    public ResponseEntity<List<RouteResponseDto>> getRoutesByMaxDistance(@PathVariable BigDecimal maxDistance) {
        List<RouteResponseDto> routes = routeService.getRoutesByMaxDistance(maxDistance);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/{id}/with-segments")
    public ResponseEntity<RouteResponseDto> getRouteWithSegments(@PathVariable Long id) {
        Optional<RouteResponseDto> route = routeService.getRouteWithSegments(id);
        return route.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-flights")
    public ResponseEntity<RouteResponseDto> getRouteWithFlights(@PathVariable Long id) {
        Optional<RouteResponseDto> route = routeService.getRouteWithFlights(id);
        return route.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalRouteCount() {
        Long count = routeService.getTotalRouteCount();
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<RouteResponseDto> createRoute(@Valid @RequestBody RouteRequestDto requestDto) {
        RouteResponseDto createdRoute = routeService.createRoute(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDto> updateRoute(@PathVariable Long id, 
                                                     @Valid @RequestBody RouteRequestDto requestDto) {
        Optional<RouteResponseDto> updatedRoute = routeService.updateRoute(id, requestDto);
        return updatedRoute.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

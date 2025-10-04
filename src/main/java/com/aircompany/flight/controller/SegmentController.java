package com.aircompany.flight.controller;

import com.aircompany.flight.dto.SegmentRequestDto;
import com.aircompany.flight.dto.SegmentResponseDto;
import com.aircompany.flight.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight/segments")
@CrossOrigin(origins = "*")
public class SegmentController {
    
    @Autowired
    private SegmentService segmentService;
    
    @GetMapping
    public ResponseEntity<List<SegmentResponseDto>> getAllSegments() {
        List<SegmentResponseDto> segments = segmentService.getAllSegments();
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SegmentResponseDto> getSegmentById(@PathVariable Long id) {
        Optional<SegmentResponseDto> segment = segmentService.getSegmentById(id);
        return segment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByRoute(@PathVariable Long routeId) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByRoute(routeId);
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/route/{routeId}/ordered")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByRouteOrdered(@PathVariable Long routeId) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByRouteOrdered(routeId);
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/origin/{originAirportId}")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByOriginAirport(@PathVariable Long originAirportId) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByOriginAirport(originAirportId);
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/destination/{destinationAirportId}")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByDestinationAirport(@PathVariable Long destinationAirportId) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByDestinationAirport(destinationAirportId);
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/distance/range")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByDistanceRange(
            @RequestParam BigDecimal minDistance, 
            @RequestParam BigDecimal maxDistance) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByDistanceRange(minDistance, maxDistance);
        return ResponseEntity.ok(segments);
    }
    
    @GetMapping("/origin/{originId}/destination/{destinationId}")
    public ResponseEntity<List<SegmentResponseDto>> getSegmentsByOriginAndDestination(
            @PathVariable Long originId, 
            @PathVariable Long destinationId) {
        List<SegmentResponseDto> segments = segmentService.getSegmentsByOriginAndDestination(originId, destinationId);
        return ResponseEntity.ok(segments);
    }
    
    @PostMapping
    public ResponseEntity<SegmentResponseDto> createSegment(@Valid @RequestBody SegmentRequestDto requestDto) {
        try {
            SegmentResponseDto createdSegment = segmentService.createSegment(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSegment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SegmentResponseDto> updateSegment(@PathVariable Long id, 
                                                         @Valid @RequestBody SegmentRequestDto requestDto) {
        try {
            Optional<SegmentResponseDto> updatedSegment = segmentService.updateSegment(id, requestDto);
            return updatedSegment.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSegment(@PathVariable Long id) {
        boolean deleted = segmentService.deleteSegment(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

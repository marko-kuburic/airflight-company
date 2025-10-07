package com.aircompany.flight.repository;

import com.aircompany.flight.model.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
    
    List<Segment> findByRouteId(Long routeId);
    
    List<Segment> findByOriginAirportId(Long originAirportId);
    
    List<Segment> findByDestinationAirportId(Long destinationAirportId);
    
    List<Segment> findByDistanceBetween(BigDecimal minDistance, BigDecimal maxDistance);
    
    @Query("SELECT s FROM Segment s LEFT JOIN FETCH s.route WHERE s.id = :id")
    Segment findByIdWithRoute(@Param("id") Long id);
    
    @Query("SELECT s FROM Segment s LEFT JOIN FETCH s.originAirport LEFT JOIN FETCH s.destinationAirport WHERE s.id = :id")
    Segment findByIdWithAirports(@Param("id") Long id);
    
    @Query("SELECT s FROM Segment s WHERE s.route.id = :routeId ORDER BY s.id")
    List<Segment> findByRouteIdOrderById(@Param("routeId") Long routeId);
    
    @Query("SELECT s FROM Segment s WHERE s.originAirport.id = :originId AND s.destinationAirport.id = :destinationId")
    List<Segment> findByOriginAndDestination(@Param("originId") Long originId, @Param("destinationId") Long destinationId);
}

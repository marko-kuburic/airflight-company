package com.aircompany.flight.repository;

import com.aircompany.flight.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    List<Route> findByNameContainingIgnoreCase(String name);
    
    List<Route> findByTotalDistanceBetween(BigDecimal minDistance, BigDecimal maxDistance);
    
    List<Route> findByTotalDistanceGreaterThanEqual(BigDecimal minDistance);
    
    List<Route> findByTotalDistanceLessThanEqual(BigDecimal maxDistance);
    
    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.segments WHERE r.id = :id")
    Optional<Route> findByIdWithSegments(@Param("id") Long id);
    
    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.flights WHERE r.id = :id")
    Optional<Route> findByIdWithFlights(@Param("id") Long id);
    
    @Query("SELECT COUNT(r) FROM Route r")
    Long countAllRoutes();
    
    @Query("SELECT r FROM Route r WHERE r.totalDistance >= :minDistance ORDER BY r.totalDistance ASC")
    List<Route> findByTotalDistanceGreaterThanEqualOrderByDistance(@Param("minDistance") BigDecimal minDistance);
}

package com.aircompany.flight.repository;

import com.aircompany.flight.model.Flight;
import com.aircompany.flight.model.Flight.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    List<Flight> findByStatus(FlightStatus status);
    
    List<Flight> findByDepTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Flight> findByArrTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Flight> findByAircraftId(Long aircraftId);
    
    List<Flight> findByRouteId(Long routeId);
    
    List<Flight> findByFlightDispatcherId(Long flightDispatcherId);
    
    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.aircraft WHERE f.id = :id")
    Optional<Flight> findByIdWithAircraft(@Param("id") Long id);
    
    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.route WHERE f.id = :id")
    Optional<Flight> findByIdWithRoute(@Param("id") Long id);
    
    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.flightDispatcher WHERE f.id = :id")
    Optional<Flight> findByIdWithFlightDispatcher(@Param("id") Long id);
    
    @Query("SELECT f FROM Flight f WHERE f.depTime >= :startTime AND f.depTime <= :endTime AND f.status = :status")
    List<Flight> findByDepTimeBetweenAndStatus(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime, 
                                              @Param("status") FlightStatus status);
    
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.status = :status")
    Long countByStatus(@Param("status") FlightStatus status);
    
    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId AND f.status IN :statuses")
    List<Flight> findByAircraftIdAndStatusIn(@Param("aircraftId") Long aircraftId, 
                                           @Param("statuses") List<FlightStatus> statuses);
}

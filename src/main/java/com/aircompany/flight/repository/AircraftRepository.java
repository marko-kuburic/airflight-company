package com.aircompany.flight.repository;

import com.aircompany.flight.model.Aircraft;
import com.aircompany.flight.model.Aircraft.AircraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    
    List<Aircraft> findByStatus(AircraftStatus status);
    
    List<Aircraft> findByModelContainingIgnoreCase(String model);
    
    List<Aircraft> findByCapacityGreaterThanEqual(Integer minCapacity);
    
    List<Aircraft> findByCapacityLessThanEqual(Integer maxCapacity);
    
    List<Aircraft> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);
    
    @Query("SELECT a FROM Aircraft a LEFT JOIN FETCH a.service WHERE a.id = :id")
    Optional<Aircraft> findByIdWithService(@Param("id") Long id);
    
    @Query("SELECT a FROM Aircraft a LEFT JOIN FETCH a.flights WHERE a.id = :id")
    Optional<Aircraft> findByIdWithFlights(@Param("id") Long id);
    
    @Query("SELECT COUNT(a) FROM Aircraft a WHERE a.status = :status")
    Long countByStatus(@Param("status") AircraftStatus status);
    
    @Query("SELECT a FROM Aircraft a WHERE a.status IN :statuses")
    List<Aircraft> findByStatusIn(@Param("statuses") List<AircraftStatus> statuses);
}

package com.aircompany.hr.repository;

import com.aircompany.hr.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    
    List<Technician> findBySpecializationContainingIgnoreCase(String specialization);
    
    List<Technician> findByFlightHoursGreaterThanEqual(Integer minFlightHours);
    
    List<Technician> findByFlightHoursLessThanEqual(Integer maxFlightHours);
    
    List<Technician> findByFlightHoursBetween(Integer minFlightHours, Integer maxFlightHours);
    
    @Query("SELECT t FROM Technician t WHERE t.airport.id = :airportId")
    List<Technician> findByAirportId(@Param("airportId") Long airportId);
    
    @Query("SELECT t FROM Technician t LEFT JOIN FETCH t.airport WHERE t.id = :id")
    Optional<Technician> findByIdWithAirport(@Param("id") Long id);
    
    @Query("SELECT t FROM Technician t LEFT JOIN FETCH t.services WHERE t.id = :id")
    Optional<Technician> findByIdWithServices(@Param("id") Long id);
    
    @Query("SELECT t FROM Technician t WHERE t.lastDutyEnd <= :cutoffTime")
    List<Technician> findAvailableTechnicians(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(t) FROM Technician t WHERE t.specialization = :specialization")
    Long countBySpecialization(@Param("specialization") String specialization);
    
    @Query("SELECT t FROM Technician t WHERE t.specialization = :specialization AND t.airport.id = :airportId")
    List<Technician> findBySpecializationAndAirport(@Param("specialization") String specialization, 
                                                   @Param("airportId") Long airportId);
}

package com.aircompany.hr.repository;

import com.aircompany.hr.model.FlightDispatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightDispatcherRepository extends JpaRepository<FlightDispatcher, Long> {
    
    List<FlightDispatcher> findByDispatchLicenseContainingIgnoreCase(String license);
    
    List<FlightDispatcher> findByExperienceYearsGreaterThanEqual(Integer minExperience);
    
    List<FlightDispatcher> findByAircraftTypesQualifiedContainingIgnoreCase(String aircraftType);
    
    @Query("SELECT fd FROM FlightDispatcher fd WHERE fd.airport.id = :airportId")
    List<FlightDispatcher> findByAirportId(@Param("airportId") Long airportId);
    
    @Query("SELECT fd FROM FlightDispatcher fd LEFT JOIN FETCH fd.airport WHERE fd.id = :id")
    Optional<FlightDispatcher> findByIdWithAirport(@Param("id") Long id);
    
    @Query("SELECT fd FROM FlightDispatcher fd LEFT JOIN FETCH fd.flights WHERE fd.id = :id")
    Optional<FlightDispatcher> findByIdWithFlights(@Param("id") Long id);
    
    @Query("SELECT COUNT(fd) FROM FlightDispatcher fd WHERE fd.experienceYears >= :minExperience")
    Long countByMinExperience(@Param("minExperience") Integer minExperience);
    
    @Query("SELECT fd FROM FlightDispatcher fd WHERE fd.dispatchLicense IS NOT NULL AND fd.dispatchLicense != ''")
    List<FlightDispatcher> findWithValidLicense();
}

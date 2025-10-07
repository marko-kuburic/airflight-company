package com.aircompany.flight.repository;

import com.aircompany.flight.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    
    Optional<Airport> findByIataCode(String iataCode);
    
    List<Airport> findByCityContainingIgnoreCase(String city);
    
    List<Airport> findByNameContainingIgnoreCase(String name);
    
    List<Airport> findByCountryCode(String countryCode);
    
    @Query("SELECT a FROM Airport a LEFT JOIN FETCH a.country WHERE a.id = :id")
    Optional<Airport> findByIdWithCountry(@Param("id") Long id);
    
    @Query("SELECT a FROM Airport a LEFT JOIN FETCH a.country WHERE a.iataCode = :iataCode")
    Optional<Airport> findByIataCodeWithCountry(@Param("iataCode") String iataCode);
    
    @Query("SELECT a FROM Airport a WHERE a.country.code = :countryCode")
    List<Airport> findByCountryCodeWithCountry(@Param("countryCode") String countryCode);
    
    boolean existsByIataCode(String iataCode);
}

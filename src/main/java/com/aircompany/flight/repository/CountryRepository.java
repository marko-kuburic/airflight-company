package com.aircompany.flight.repository;

import com.aircompany.flight.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    
    Optional<Country> findByCode(String code);
    
    List<Country> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.airports WHERE c.code = :code")
    Optional<Country> findByCodeWithAirports(@Param("code") String code);
    
    boolean existsByCode(String code);
}

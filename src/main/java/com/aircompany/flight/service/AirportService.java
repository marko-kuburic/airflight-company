package com.aircompany.flight.service;

import com.aircompany.flight.dto.AirportRequestDto;
import com.aircompany.flight.dto.AirportResponseDto;
import com.aircompany.flight.model.Airport;
import com.aircompany.flight.model.Country;
import com.aircompany.flight.repository.AirportRepository;
import com.aircompany.flight.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AirportService {
    
    @Autowired
    private AirportRepository airportRepository;
    
    @Autowired
    private CountryRepository countryRepository;
    
    public List<AirportResponseDto> getAllAirports() {
        return airportRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<AirportResponseDto> getAirportById(Long id) {
        return airportRepository.findById(id)
                .map(this::convertToResponseDto);
    }
    
    public Optional<AirportResponseDto> getAirportByIataCode(String iataCode) {
        return airportRepository.findByIataCode(iataCode)
                .map(this::convertToResponseDto);
    }
    
    public List<AirportResponseDto> searchAirportsByCity(String city) {
        return airportRepository.findByCityContainingIgnoreCase(city).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<AirportResponseDto> searchAirportsByName(String name) {
        return airportRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<AirportResponseDto> getAirportsByCountryCode(String countryCode) {
        return airportRepository.findByCountryCode(countryCode).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public AirportResponseDto createAirport(AirportRequestDto requestDto) {
        if (airportRepository.existsByIataCode(requestDto.getIataCode())) {
            throw new IllegalArgumentException("Airport with IATA code " + requestDto.getIataCode() + " already exists");
        }
        
        Country country = countryRepository.findByCode(requestDto.getCountryCode())
                .orElseThrow(() -> new IllegalArgumentException("Country with code " + requestDto.getCountryCode() + " not found"));
        
        Airport airport = new Airport(
                requestDto.getIataCode(),
                requestDto.getName(),
                requestDto.getCity(),
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getTimezone(),
                country
        );
        
        Airport savedAirport = airportRepository.save(airport);
        return convertToResponseDto(savedAirport);
    }
    
    public Optional<AirportResponseDto> updateAirport(Long id, AirportRequestDto requestDto) {
        return airportRepository.findById(id)
                .map(airport -> {
                    Country country = countryRepository.findByCode(requestDto.getCountryCode())
                            .orElseThrow(() -> new IllegalArgumentException("Country with code " + requestDto.getCountryCode() + " not found"));
                    
                    airport.setIataCode(requestDto.getIataCode());
                    airport.setName(requestDto.getName());
                    airport.setCity(requestDto.getCity());
                    airport.setLatitude(requestDto.getLatitude());
                    airport.setLongitude(requestDto.getLongitude());
                    airport.setTimezone(requestDto.getTimezone());
                    airport.setCountry(country);
                    
                    Airport savedAirport = airportRepository.save(airport);
                    return convertToResponseDto(savedAirport);
                });
    }
    
    public boolean deleteAirport(Long id) {
        if (airportRepository.existsById(id)) {
            airportRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private AirportResponseDto convertToResponseDto(Airport airport) {
        AirportResponseDto responseDto = new AirportResponseDto();
        responseDto.setId(airport.getId());
        responseDto.setIataCode(airport.getIataCode());
        responseDto.setName(airport.getName());
        responseDto.setCity(airport.getCity());
        responseDto.setLatitude(airport.getLatitude());
        responseDto.setLongitude(airport.getLongitude());
        responseDto.setTimezone(airport.getTimezone());
        responseDto.setCountryCode(airport.getCountry().getCode());
        responseDto.setCountryName(airport.getCountry().getName());
        responseDto.setCreatedAt(airport.getCreatedAt());
        responseDto.setModifiedAt(airport.getModifiedAt());
        return responseDto;
    }
}

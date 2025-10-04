package com.aircompany.flight.service;

import com.aircompany.flight.dto.CountryRequestDto;
import com.aircompany.flight.dto.CountryResponseDto;
import com.aircompany.flight.model.Country;
import com.aircompany.flight.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CountryService {
    
    @Autowired
    private CountryRepository countryRepository;
    
    public List<CountryResponseDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Optional<CountryResponseDto> getCountryByCode(String code) {
        return countryRepository.findByCode(code)
                .map(this::convertToResponseDto);
    }
    
    public List<CountryResponseDto> searchCountriesByName(String name) {
        return countryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public CountryResponseDto createCountry(CountryRequestDto requestDto) {
        if (countryRepository.existsByCode(requestDto.getCode())) {
            throw new IllegalArgumentException("Country with code " + requestDto.getCode() + " already exists");
        }
        
        Country country = new Country(requestDto.getCode(), requestDto.getName());
        Country savedCountry = countryRepository.save(country);
        return convertToResponseDto(savedCountry);
    }
    
    public Optional<CountryResponseDto> updateCountry(String code, CountryRequestDto requestDto) {
        return countryRepository.findByCode(code)
                .map(country -> {
                    country.setName(requestDto.getName());
                    Country savedCountry = countryRepository.save(country);
                    return convertToResponseDto(savedCountry);
                });
    }
    
    public boolean deleteCountry(String code) {
        if (countryRepository.existsByCode(code)) {
            countryRepository.deleteById(code);
            return true;
        }
        return false;
    }
    
    public Optional<CountryResponseDto> getCountryWithAirports(String code) {
        return countryRepository.findByCodeWithAirports(code)
                .map(country -> {
                    CountryResponseDto responseDto = convertToResponseDto(country);
                    responseDto.setAirportCodes(
                            country.getAirports().stream()
                                    .map(airport -> airport.getIataCode())
                                    .collect(Collectors.toList())
                    );
                    return responseDto;
                });
    }
    
    private CountryResponseDto convertToResponseDto(Country country) {
        CountryResponseDto responseDto = new CountryResponseDto();
        responseDto.setCode(country.getCode());
        responseDto.setName(country.getName());
        responseDto.setCreatedAt(country.getCreatedAt());
        responseDto.setModifiedAt(country.getModifiedAt());
        return responseDto;
    }
}

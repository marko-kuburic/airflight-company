package com.aircompany.sales.dto;

import com.aircompany.flight.model.Flight;
import com.aircompany.sales.model.Offer;
import com.aircompany.sales.model.CabinClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FlightSearchResponse {
    
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private String aircraft;
    private BigDecimal basePrice;
    private BigDecimal currentPrice;
    private BigDecimal discountPercentage;
    private List<CabinClassResponse> cabinClasses;
    private List<OfferResponse> offers;
    private Integer availableSeats;
    private Boolean hasActiveOffers;
    
    // Constructors
    public FlightSearchResponse() {}
    
    public FlightSearchResponse(Flight flight, List<Offer> offers) {
        this.flightId = flight.getId();
        this.origin = flight.getRoute().getSegments().get(0).getOriginAirport().getName();
        this.destination = flight.getRoute().getSegments().get(flight.getRoute().getSegments().size()-1).getDestinationAirport().getName();
        this.departureTime = flight.getDepTime();
        this.arrivalTime = flight.getArrTime();
        this.aircraft = flight.getAircraft().getModel();
        this.hasActiveOffers = !offers.isEmpty();
    }
    
    // Getters and Setters
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getAircraft() {
        return aircraft;
    }
    
    public void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public List<CabinClassResponse> getCabinClasses() {
        return cabinClasses;
    }
    
    public void setCabinClasses(List<CabinClassResponse> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }
    
    public List<OfferResponse> getOffers() {
        return offers;
    }
    
    public void setOffers(List<OfferResponse> offers) {
        this.offers = offers;
    }
    
    public Integer getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
    
    public Boolean getHasActiveOffers() {
        return hasActiveOffers;
    }
    
    public void setHasActiveOffers(Boolean hasActiveOffers) {
        this.hasActiveOffers = hasActiveOffers;
    }
    
    // Nested classes for response structure
    public static class CabinClassResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer availableSeats;
        
        // Constructors, getters and setters
        public CabinClassResponse() {}
        
        public CabinClassResponse(CabinClass cabinClass) {
            this.id = cabinClass.getId();
            this.name = cabinClass.getName();
            this.description = cabinClass.getDescription();
            this.price = cabinClass.getBasePrice();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    }
    
    public static class OfferResponse {
        private Long id;
        private String title;
        private String description;
        private BigDecimal discountPercentage;
        private BigDecimal finalPrice;
        private LocalDateTime expiresAt;
        
        // Constructors, getters and setters
        public OfferResponse() {}
        
        public OfferResponse(Offer offer) {
            this.id = offer.getId();
            this.title = offer.getTitle();
            this.description = offer.getDescription();
            this.discountPercentage = offer.getDiscountPercentage();
            this.expiresAt = offer.getExpiresAt();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public BigDecimal getFinalPrice() { return finalPrice; }
        public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }
}
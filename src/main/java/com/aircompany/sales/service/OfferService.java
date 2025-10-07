package com.aircompany.sales.service;

import com.aircompany.sales.model.Offer;
import com.aircompany.sales.model.Fare;
import com.aircompany.sales.model.CabinClass;
import com.aircompany.sales.repository.OfferRepository;
import com.aircompany.flight.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class OfferService {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private DynamicPricingService dynamicPricingService;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Get all active offers for a specific flight
     */
    public List<Offer> getActiveOffersForFlight(Long flightId) {
        TypedQuery<Offer> query = entityManager.createQuery(
            "SELECT o FROM Offer o " +
            "WHERE o.flight.id = :flightId " +
            "AND o.isActive = true " +
            "AND (o.expiresAt IS NULL OR o.expiresAt > :now) " +
            "ORDER BY o.discountPercentage DESC",
            Offer.class
        );
        query.setParameter("flightId", flightId);
        query.setParameter("now", LocalDateTime.now());
        
        return query.getResultList();
    }
    
    /**
     * Get offer by ID
     */
    public Optional<Offer> getOfferById(Long id) {
        return offerRepository.findById(id);
    }
    
    /**
     * Check if offer is still valid
     */
    public boolean isOfferValid(Long offerId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            return false;
        }
        
        Offer offer = offerOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        return offer.getIsActive() && 
               (offer.getExpiresAt() == null || offer.getExpiresAt().isAfter(now));
    }
    
    /**
     * Get all active offers
     */
    public List<Offer> getAllActiveOffers() {
        TypedQuery<Offer> query = entityManager.createQuery(
            "SELECT o FROM Offer o " +
            "WHERE o.isActive = true " +
            "AND (o.expiresAt IS NULL OR o.expiresAt > :now) " +
            "ORDER BY o.createdAt DESC",
            Offer.class
        );
        query.setParameter("now", LocalDateTime.now());
        
        return query.getResultList();
    }
    
    /**
     * Create a new offer
     */
    public Offer createOffer(Offer offer) {
        logger.info("Creating new offer: {}", offer.getTitle());
        return offerRepository.save(offer);
    }
    
    /**
     * Regenerate offer with new pricing when expired
     */
    @Transactional
    public Offer regenerateOfferForFlight(Long flightId) {
        logger.info("Regenerating offer for flight ID: {}", flightId);
        
        // Find flight
        Flight flight = entityManager.find(Flight.class, flightId);
        if (flight == null) {
            throw new RuntimeException("Flight not found with ID: " + flightId);
        }
        
        // Deactivate old expired offers for this flight
        entityManager.createQuery(
            "UPDATE Offer o SET o.isActive = false " +
            "WHERE o.flight.id = :flightId AND o.expiresAt <= :now")
            .setParameter("flightId", flightId)
            .setParameter("now", LocalDateTime.now())
            .executeUpdate();
        
        // Create new offer with current pricing
        return createDynamicOfferForFlight(flight);
    }
    
    /**
     * Create dynamic offer for flight with current pricing
     */
    private Offer createDynamicOfferForFlight(Flight flight) {
        // Create new offer
        Offer offer = new Offer();
        offer.setTitle("Flight " + flight.getFlightNumber() + " - Dynamic Pricing");
        offer.setDescription("Updated pricing offer for flight " + flight.getFlightNumber());
        offer.setDiscountPercentage(new BigDecimal("0.00"));
        offer.setStartDate(LocalDateTime.now());
        offer.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10 minutes validity
        offer.setEndDate(flight.getDepTime().minusHours(1));
        offer.setFlight(flight);
        offer.setBasePrice(dynamicPricingService.getBasePrice(flight));
        offer.setIsActive(true);
        
        // Save offer first
        offer = offerRepository.save(offer);
        
        // Create fares for each cabin class with current dynamic pricing
        List<CabinClass> cabinClasses = entityManager.createQuery(
            "SELECT c FROM CabinClass c ORDER BY c.basePrice", CabinClass.class)
            .getResultList();
        List<Fare> fares = new ArrayList<>();
        
        for (CabinClass cabinClass : cabinClasses) {
            Fare fare = new Fare();
            fare.setCabinClass(cabinClass);
            fare.setOffer(offer);
            
            // Calculate current dynamic price
            BigDecimal dynamicPrice = calculateCurrentFarePrice(cabinClass, flight);
            fare.setPrice(dynamicPrice);
            
            entityManager.persist(fare);
            fares.add(fare);
        }
        
        offer.setFares(fares);
        
        logger.info("Created new offer with ID: {} for flight: {}", offer.getId(), flight.getFlightNumber());
        return offer;
    }
    
    private BigDecimal calculateCurrentFarePrice(CabinClass cabinClass, Flight flight) {
        // Get current dynamic price for flight
        BigDecimal flightDynamicPrice = dynamicPricingService.getCurrentPrice(flight);
        
        // Apply cabin class multiplier to dynamic price
        BigDecimal cabinMultiplier = getCabinClassMultiplier(cabinClass.getName());
        
        return flightDynamicPrice.multiply(cabinMultiplier)
               .setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    private BigDecimal getCabinClassMultiplier(String cabinClassName) {
        switch (cabinClassName.toUpperCase()) {
            case "ECONOMY": return new BigDecimal("1.0");
            case "BUSINESS": return new BigDecimal("2.5");
            case "FIRST": return new BigDecimal("4.0");
            default: return new BigDecimal("1.0");
        }
    }
    
    /**
     * Update existing offer
     */
    public Offer updateOffer(Offer offer) {
        logger.info("Updating offer: {}", offer.getId());
        return offerRepository.save(offer);
    }
    
    /**
     * Deactivate offer
     */
    public void deactivateOffer(Long offerId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isPresent()) {
            Offer offer = offerOpt.get();
            offer.setIsActive(false);
            offerRepository.save(offer);
            logger.info("Deactivated offer: {}", offerId);
        }
    }
}
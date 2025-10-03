package com.aircompany.sales.service;

import com.aircompany.sales.model.Offer;
import com.aircompany.sales.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);
    
    @Autowired
    private OfferRepository offerRepository;
    
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
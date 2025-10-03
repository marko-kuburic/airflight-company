package com.aircompany.sales.controller;

import com.aircompany.sales.model.Offer;
import com.aircompany.sales.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);
    
    @Autowired
    private OfferRepository offerRepository;
    
    /**
     * Get all valid offers
     */
    @GetMapping
    public ResponseEntity<List<Offer>> getAllValidOffers() {
        List<Offer> offers = offerRepository.findValidOffers(LocalDateTime.now());
        return ResponseEntity.ok(offers);
    }
    
    /**
     * Get offer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOffer(@PathVariable Long id) {
        try {
            Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
            
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            logger.error("Error getting offer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get offers by flight ID
     */
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Offer>> getOffersByFlight(@PathVariable Long flightId) {
        List<Offer> offers = offerRepository.findByFlightId(flightId);
        return ResponseEntity.ok(offers);
    }
    
    /**
     * Search offers by title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Offer>> searchOffers(@RequestParam String query) {
        List<Offer> offers = offerRepository.searchByTitle(query);
        return ResponseEntity.ok(offers);
    }
    
    /**
     * Get offers with minimum discount
     */
    @GetMapping("/discount/{minDiscount}")
    public ResponseEntity<List<Offer>> getOffersByMinDiscount(@PathVariable java.math.BigDecimal minDiscount) {
        List<Offer> offers = offerRepository.findByMinimumDiscount(minDiscount);
        return ResponseEntity.ok(offers);
    }
    
    /**
     * Check if offer is valid and active
     */
    @GetMapping("/{id}/validity")
    public ResponseEntity<?> checkOfferValidity(@PathVariable Long id) {
        try {
            Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
            
            boolean isValid = offer.isValidAndActive();
            boolean isExpired = offer.isExpired();
            
            return ResponseEntity.ok(java.util.Map.of(
                "valid", isValid,
                "expired", isExpired,
                "active", offer.getIsActive(),
                "expiresAt", offer.getExpiresAt(),
                "finalPrice", offer.getFinalPrice()
            ));
        } catch (Exception e) {
            logger.error("Error checking offer validity: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
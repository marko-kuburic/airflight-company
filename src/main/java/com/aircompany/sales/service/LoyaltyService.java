package com.aircompany.sales.service;

import com.aircompany.sales.model.Loyalty;
import com.aircompany.sales.repository.LoyaltyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoyaltyService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoyaltyService.class);
    
    @Autowired
    private LoyaltyRepository loyaltyRepository;
    
    /**
     * Add loyalty points to customer
     */
    public void addPoints(Long customerId, Integer points) {
        logger.info("Adding {} points to customer {}", points, customerId);
        
        Loyalty loyalty = loyaltyRepository.findByCustomerId(customerId)
            .orElseGet(() -> createNewLoyalty(customerId));
        
        loyalty.setPoints(loyalty.getPoints() + points);
        updateTier(loyalty);
        
        loyaltyRepository.save(loyalty);
    }
    
    /**
     * Deduct loyalty points from customer
     */
    public void deductPoints(Long customerId, Integer points) {
        logger.info("Deducting {} points from customer {}", points, customerId);
        
        Loyalty loyalty = loyaltyRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Customer loyalty record not found"));
        
        if (loyalty.getPoints() < points) {
            throw new RuntimeException("Insufficient loyalty points");
        }
        
        loyalty.setPoints(loyalty.getPoints() - points);
        updateTier(loyalty);
        
        loyaltyRepository.save(loyalty);
    }
    
    /**
     * Get customer loyalty points
     */
    public Integer getCustomerPoints(Long customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
            .map(Loyalty::getPoints)
            .orElse(0);
    }
    
    /**
     * Get customer loyalty tier
     */
    public Loyalty.LoyaltyTier getCustomerTier(Long customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
            .map(Loyalty::getTier)
            .orElse(Loyalty.LoyaltyTier.BRONZE);
    }
    
    private Loyalty createNewLoyalty(Long customerId) {
        Loyalty loyalty = new Loyalty();
        loyalty.setPoints(0);
        loyalty.setTier(Loyalty.LoyaltyTier.BRONZE);
        // Note: Customer relationship will be set when Customer entity is available
        return loyalty;
    }
    
    private void updateTier(Loyalty loyalty) {
        Integer points = loyalty.getPoints();
        
        if (points >= 100000) {
            loyalty.setTier(Loyalty.LoyaltyTier.DIAMOND);
        } else if (points >= 50000) {
            loyalty.setTier(Loyalty.LoyaltyTier.PLATINUM);
        } else if (points >= 25000) {
            loyalty.setTier(Loyalty.LoyaltyTier.GOLD);
        } else if (points >= 10000) {
            loyalty.setTier(Loyalty.LoyaltyTier.SILVER);
        } else {
            loyalty.setTier(Loyalty.LoyaltyTier.BRONZE);
        }
    }
}
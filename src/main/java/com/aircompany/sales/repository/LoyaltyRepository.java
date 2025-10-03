package com.aircompany.sales.repository;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.model.Loyalty;
import com.aircompany.sales.model.Loyalty.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyRepository extends JpaRepository<Loyalty, Long> {
    
    /**
     * Find loyalty record by customer ID
     */
    Optional<Loyalty> findByCustomerId(Long customerId);
    
    /**
     * Find loyalty record by customer
     */
    Optional<Loyalty> findByCustomer(Customer customer);
    
    /**
     * Find loyalty records by tier
     */
    List<Loyalty> findByTier(LoyaltyTier tier);
    
    /**
     * Find customers with points greater than specified amount
     */
    @Query("SELECT l FROM Loyalty l WHERE l.points >= :minPoints")
    List<Loyalty> findByMinimumPoints(@Param("minPoints") Integer minPoints);
    
    /**
     * Find top customers by points
     */
    @Query("SELECT l FROM Loyalty l ORDER BY l.points DESC")
    List<Loyalty> findTopCustomersByPoints();
    
    /**
     * Count customers by tier
     */
    @Query("SELECT l.tier, COUNT(l) FROM Loyalty l GROUP BY l.tier")
    List<Object[]> countCustomersByTier();
    
    /**
     * Check if customer exists in loyalty program
     */
    boolean existsByCustomerId(Long customerId);
}
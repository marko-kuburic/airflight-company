package com.aircompany.sales.repository;

import com.aircompany.sales.model.SavedPaymentMethod;
import com.aircompany.hr.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPaymentMethodRepository extends JpaRepository<SavedPaymentMethod, Long> {
    
    /**
     * Find all payment methods for a customer, ordered by creation date
     */
    List<SavedPaymentMethod> findByCustomerOrderByCreatedAtDesc(Customer customer);
    
    /**
     * Find all payment methods for a customer by customer ID
     */
    @Query("SELECT spm FROM SavedPaymentMethod spm WHERE spm.customer.id = :customerId ORDER BY spm.createdAt DESC")
    List<SavedPaymentMethod> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId);
    
    /**
     * Find the default payment method for a customer
     */
    Optional<SavedPaymentMethod> findByCustomerAndIsDefaultTrue(Customer customer);
    
    /**
     * Find a payment method by ID and customer ID (for security)
     */
    @Query("SELECT spm FROM SavedPaymentMethod spm WHERE spm.id = :id AND spm.customer.id = :customerId")
    Optional<SavedPaymentMethod> findByIdAndCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);
    
    /**
     * Set all payment methods for a customer to non-default
     */
    @Modifying
    @Query("UPDATE SavedPaymentMethod spm SET spm.isDefault = false WHERE spm.customer.id = :customerId")
    void clearDefaultForCustomer(@Param("customerId") Long customerId);
    
    /**
     * Count payment methods for a customer
     */
    long countByCustomer(Customer customer);
    
    /**
     * Delete all payment methods for a customer
     */
    void deleteByCustomer(Customer customer);
}
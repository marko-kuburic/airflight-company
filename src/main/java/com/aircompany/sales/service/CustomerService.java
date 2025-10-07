package com.aircompany.sales.service;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.dto.*;
import com.aircompany.sales.model.Loyalty;
import com.aircompany.sales.model.Reservation;
import com.aircompany.sales.model.SavedPaymentMethod;
import com.aircompany.sales.repository.CustomerRepository;
import com.aircompany.sales.repository.LoyaltyRepository;
import com.aircompany.sales.repository.SavedPaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private LoyaltyRepository loyaltyRepository;
    
    @Autowired
    private SavedPaymentMethodRepository savedPaymentMethodRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<UserProfileResponse> getCustomerProfile(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        return customerOpt.map(this::convertToUserProfileResponse);
    }
    
    public UserProfileResponse updateCustomerProfile(Long customerId, UpdateUserProfileRequest updateRequest) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        customer.setFirstName(updateRequest.getFirstName());
        customer.setLastName(updateRequest.getLastName());
        customer.setEmail(updateRequest.getEmail());
        customer.setPhone(updateRequest.getPhone());
        customer.setDateOfBirth(updateRequest.getDateOfBirth());
        customer.setPreferredLanguage(updateRequest.getPreferredLanguage());
        
        customer = customerRepository.save(customer);
        return convertToUserProfileResponse(customer);
    }
    
    public List<SavedPaymentMethodResponse> getCustomerPaymentMethods(Long customerId) {
        List<SavedPaymentMethod> paymentMethods = savedPaymentMethodRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        return paymentMethods.stream()
            .map(this::convertToPaymentMethodResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public SavedPaymentMethodResponse savePaymentMethod(Long customerId, SavedPaymentMethodRequest request) {
        // Find customer
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        
        // If this is set as default, clear other defaults
        if (request.isDefault()) {
            savedPaymentMethodRepository.clearDefaultForCustomer(customerId);
        }
        
        // Encrypt the card number using Base64 encoding (simple encryption for now)
        String encryptedCardNumber = Base64.getEncoder().encodeToString(request.getCardNumber().getBytes());
        
        // Get masked card number from request
        String maskedCardNumber = request.getMaskedCardNumber();
        
        // Create new saved payment method
        SavedPaymentMethod paymentMethod = new SavedPaymentMethod(
            customer,
            encryptedCardNumber,
            maskedCardNumber,
            request.getCardholderName(),
            request.getExpiryDate(),
            request.getCardType(),
            request.isDefault()
        );
        
        // Save to database
        paymentMethod = savedPaymentMethodRepository.save(paymentMethod);
        
        return convertToPaymentMethodResponse(paymentMethod);
    }
    
    @Transactional
    public void deletePaymentMethod(Long customerId, Long paymentMethodId) {
        // Find payment method and verify it belongs to the customer
        Optional<SavedPaymentMethod> paymentMethodOpt = savedPaymentMethodRepository.findByIdAndCustomerId(paymentMethodId, customerId);
        
        if (paymentMethodOpt.isEmpty()) {
            throw new RuntimeException("Payment method not found or does not belong to this customer");
        }
        
        // Delete the payment method
        savedPaymentMethodRepository.deleteById(paymentMethodId);
    }
    
    private SavedPaymentMethodResponse convertToPaymentMethodResponse(SavedPaymentMethod paymentMethod) {
        return new SavedPaymentMethodResponse(
            paymentMethod.getId(),
            paymentMethod.getMaskedCardNumber(),
            paymentMethod.getCardholderName(),
            paymentMethod.getExpiryDate(),
            paymentMethod.getCardType(),
            paymentMethod.isDefault(),
            paymentMethod.getCreatedAt()
        );
    }
    
    public Optional<LoyaltyResponse> getCustomerLoyaltyData(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Customer customer = customerOpt.get();
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        
        if (loyaltyOpt.isPresent()) {
            Loyalty loyalty = loyaltyOpt.get();
            LoyaltyResponse response = new LoyaltyResponse();
            response.setPoints(loyalty.getPoints());
            response.setTier(loyalty.getTier().toString());
            return Optional.of(response);
        }
        
        return Optional.empty();
    }
    
    @Transactional
    public void awardManualPoints(Long customerId, int points, String reason) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        
        if (loyaltyOpt.isPresent()) {
            Loyalty loyalty = loyaltyOpt.get();
            loyalty.setPoints(loyalty.getPoints() + points);
            
            // Update tier based on points
            if (loyalty.getPoints() >= 10000) {
                loyalty.setTier(Loyalty.LoyaltyTier.PLATINUM);
            } else if (loyalty.getPoints() >= 5000) {
                loyalty.setTier(Loyalty.LoyaltyTier.GOLD);
            } else if (loyalty.getPoints() >= 1000) {
                loyalty.setTier(Loyalty.LoyaltyTier.SILVER);
            }
            
            loyaltyRepository.save(loyalty);
        }
    }
    
    public List<Reservation> getCustomerReservations(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        // TODO: Implement reservation retrieval
        return new ArrayList<>();
    }
    
    @Transactional
    public void awardPointsForReservation(Long customerId, Long reservationId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        
        if (loyaltyOpt.isPresent()) {
            Loyalty loyalty = loyaltyOpt.get();
            // Award base points for flight (e.g., 100 points per flight)
            int basePoints = 100;
            loyalty.setPoints(loyalty.getPoints() + basePoints);
            
            // Update tier based on points
            if (loyalty.getPoints() >= 10000) {
                loyalty.setTier(Loyalty.LoyaltyTier.PLATINUM);
            } else if (loyalty.getPoints() >= 5000) {
                loyalty.setTier(Loyalty.LoyaltyTier.GOLD);
            } else if (loyalty.getPoints() >= 1000) {
                loyalty.setTier(Loyalty.LoyaltyTier.SILVER);
            }
            
            loyaltyRepository.save(loyalty);
        }
    }
    
    private UserProfileResponse convertToUserProfileResponse(Customer customer) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(customer.getId());
        profile.setFirstName(customer.getFirstName());
        profile.setLastName(customer.getLastName());
        profile.setEmail(customer.getEmail());
        profile.setPhone(customer.getPhone());
        profile.setDateOfBirth(customer.getDateOfBirth());
        profile.setPreferredLanguage(customer.getPreferredLanguage());
        
        // Get loyalty info
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        if (loyaltyOpt.isPresent()) {
            Loyalty loyalty = loyaltyOpt.get();
            UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
            loyaltyInfo.setTier(loyalty.getTier().toString());
            loyaltyInfo.setPoints(loyalty.getPoints());
            profile.setLoyalty(loyaltyInfo);
        } else {
            UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
            loyaltyInfo.setTier("BRONZE");
            loyaltyInfo.setPoints(0);
            profile.setLoyalty(loyaltyInfo);
        }
        
        return profile;
    }
}
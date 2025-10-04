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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private LoyaltyRepository loyaltyRepository;
    
    @Autowired
    private SavedPaymentMethodRepository savedPaymentMethodRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<UserProfileResponse> authenticateUser(LoginRequest loginRequest) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(loginRequest.getEmail());
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            
            // Check password
            if (passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                return Optional.of(convertToUserProfileResponse(customer));
            }
        }
        
        return Optional.empty();
    }
    
    public UserProfileResponse registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setPhone(registerRequest.getPhone());
        
        // Save customer
        customer = customerRepository.save(customer);
        
        // Create loyalty account
        Loyalty loyalty = new Loyalty();
        loyalty.setCustomer(customer);
        loyalty.setTier(Loyalty.LoyaltyTier.BRONZE);
        loyalty.setPoints(0);
        loyaltyRepository.save(loyalty);
        
        return convertToUserProfileResponse(customer);
    }
    
    private UserProfileResponse convertToUserProfileResponse(Customer customer) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(customer.getId());
        profile.setFirstName(customer.getFirstName());
        profile.setLastName(customer.getLastName());
        profile.setEmail(customer.getEmail());
        profile.setPhone(customer.getPhone());
        
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
    
    public Optional<UserProfileResponse> getUserProfile(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isPresent()) {
            return Optional.of(convertToUserProfileResponse(customerOpt.get()));
        }
        return Optional.empty();
    }
    
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Customer customer = customerOpt.get();
        
        // Check if email is being changed and if it's already taken by another user
        if (!customer.getEmail().equals(request.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already taken by another user");
            }
        }
        
        // Update customer information
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPreferredLanguage(request.getPreferredLanguage());
        // customer.setAddress(request.getAddress()); // If you have address field
        
        customer = customerRepository.save(customer);
        return convertToUserProfileResponse(customer);
    }
    
    public List<SavedPaymentMethodResponse> getUserPaymentMethods(Long userId) {
        List<SavedPaymentMethod> paymentMethods = savedPaymentMethodRepository.findByCustomerIdOrderByCreatedAtDesc(userId);
        return paymentMethods.stream()
                .map(this::convertToPaymentMethodResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SavedPaymentMethodResponse savePaymentMethod(Long userId, SavedPaymentMethodRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Customer customer = customerOpt.get();
        
        // If this is set as default, clear other defaults
        if (request.isDefault()) {
            savedPaymentMethodRepository.clearDefaultForCustomer(userId);
        }
        
        // Create masked card number
        String maskedCardNumber = maskCardNumber(request.getCardNumber());
        
        // In a real application, you would encrypt the card number
        // For demo purposes, we'll store the masked version
        SavedPaymentMethod paymentMethod = new SavedPaymentMethod(
            customer,
            request.getCardNumber(), // In real app: encrypt this
            maskedCardNumber,
            request.getCardholderName(),
            request.getExpiryDate(),
            request.getCardType(),
            request.isDefault()
        );
        
        paymentMethod = savedPaymentMethodRepository.save(paymentMethod);
        return convertToPaymentMethodResponse(paymentMethod);
    }
    
    @Transactional
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        Optional<SavedPaymentMethod> paymentMethodOpt = 
            savedPaymentMethodRepository.findByIdAndCustomerId(paymentMethodId, userId);
        
        if (paymentMethodOpt.isEmpty()) {
            throw new RuntimeException("Payment method not found");
        }
        
        savedPaymentMethodRepository.delete(paymentMethodOpt.get());
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
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        if (cleanNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cleanNumber.substring(cleanNumber.length() - 4);
    }
    
    public List<Reservation> getUserReservations(Long userId) {
        return List.of();
    }
}

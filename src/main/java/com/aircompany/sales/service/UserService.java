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
    private NotificationService notificationService;
    
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
    
    public Optional<LoyaltyResponse> getUserLoyaltyData(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Customer customer = customerOpt.get();
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        
        if (loyaltyOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Loyalty loyalty = loyaltyOpt.get();
        
        // Return loyalty response without earning history for simplicity
        // Earning history implementation would require complex joins across multiple tables
        List<LoyaltyResponse.EarningHistoryItem> emptyHistory = new ArrayList<>();
        
        return Optional.of(new LoyaltyResponse(loyalty, emptyHistory));
    }
    
    /**
     * Award points for a completed reservation
     * This should be called when a reservation status changes to COMPLETED
     */
    public void awardPointsForReservation(Long customerId, Long reservationId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return;
        }
        
        Customer customer = customerOpt.get();
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        
        if (loyaltyOpt.isEmpty()) {
            return;
        }
        
        Loyalty loyalty = loyaltyOpt.get();
        Loyalty.LoyaltyTier oldTier = loyalty.getTier();
        
        // Calculate points for this reservation
        int pointsToAward = 750; // Simplified - in real system would calculate based on flight details
        String reservationNumber = "RES-" + reservationId;
        
        // Add points to loyalty account
        loyalty.setPoints(loyalty.getPoints() + pointsToAward);
        
        // Update tier if necessary
        updateTierBasedOnPoints(loyalty);
        
        // Save updated loyalty
        loyaltyRepository.save(loyalty);
        
        // Create real notifications for the reservation
        notificationService.notifyPointsCredited(customerId, pointsToAward, "Reservation " + reservationNumber + " completed");
        
        // Check for tier upgrade
        if (!oldTier.equals(loyalty.getTier())) {
            notificationService.notifyTierUpgrade(customerId, loyalty.getTier().toString());
        }
        
        // Create ticket confirmation notification
        notificationService.notifyTicketIssued(customerId, "TCK-" + reservationId);
    }
    
    /**
     * Award manual points (for admin/testing purposes)
     */
    public void awardManualPoints(Long customerId, int points, String reason) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        Loyalty loyalty = loyaltyRepository.findByCustomer(customer)
            .orElseGet(() -> {
                Loyalty newLoyalty = new Loyalty();
                newLoyalty.setCustomer(customer);
                newLoyalty.setPoints(0);
                newLoyalty.setTier(Loyalty.LoyaltyTier.BRONZE);
                return loyaltyRepository.save(newLoyalty);
            });
        
        // Store old tier to check for upgrades
        Loyalty.LoyaltyTier oldTier = loyalty.getTier();
        
        // Add points
        loyalty.setPoints(loyalty.getPoints() + points);
        
        // Update tier if necessary
        updateTierBasedOnPoints(loyalty);
        
        // Save updated loyalty
        loyaltyRepository.save(loyalty);
        
        // Create notifications
        if (points > 0) {
            // Notify about points credited
            notificationService.notifyPointsCredited(customerId, points, reason);
            
            // Check for tier upgrade
            if (!oldTier.equals(loyalty.getTier())) {
                notificationService.notifyTierUpgrade(customerId, loyalty.getTier().toString());
            }
        }
        
        // Log the manual point award
        System.out.println("Manually awarded " + points + " points to customer " + customerId + " for: " + reason);
    }
    
    private void updateTierBasedOnPoints(Loyalty loyalty) {
        int points = loyalty.getPoints();
        
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
